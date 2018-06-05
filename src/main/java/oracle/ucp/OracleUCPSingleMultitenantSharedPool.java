package oracle.ucp;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import oracle.ucp.admin.UniversalConnectionPoolManager;
import oracle.ucp.admin.UniversalConnectionPoolManagerImpl;
import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;

/*
 * https://blogs.oracle.com/dev2dev/ucp-multi-tenant-shared-pool-configuration
 * 
 * � Only the JDBC Thin driver supports the Shared Pool feature, and not the
 * JDBC OCI driver.
 * 
 * � For using this feature, you must use an XML configuration file.
 * 
 * Following are the two scenarios in which this feature can be implemented:
 *  � Single Multitenant Data Source Using Shared Pool
 *  � One Data Source per Tenant Using Shared Pool
 *  
 *  ----CREATING PDB's, each PDB can have multiple schema's------------
 *  login as sysdba (user/password@localhost:1521/orcl as sysdba)
 *  show con_name; [output orcl]
 *  select * from v$pdbs; [Shows current pdb your are connected to]
 *  alter session set container=cdb$root; [now selecting from v$pdbs shows all pdbs and seed from root container]
 *  
	CREATE PLUGGABLE DATABASE pdb1 ADMIN USER syspdb1 identified by "syspdb1"
	DEFAULT TABLESPACE USERS
	DATAFILE '/u01/app/oracle/oradata/cdb12/orcl/pdb1.dbf'
	SIZE 200M AUTOEXTEND ON
	FILE_NAME_CONVERT=(
	'/u01/app/oracle/oradata/cdb12/pdbseed/','/u01/app/oracle/oradata/cdb12/pdb1');
	
 *	alter pluggable database PDB1 open; [This changes the PDB4 OPEN_MODE='MOUNTED' to READ WRITE]
 *  alter session set container=PDB1; [set to newly created PDB to grant permissions]
 *  grant RESOURCE, CONNECT to syspdb1; [grant access so that application/user can connect]
 *  
 *  --CREATE ANOTHER PDB BY NAME PDB2 ---------
 *  
 *  ----CREATING COMMON USER TO CDB------------
 *  create user c##pdbmaster identified by pdbmaster container = all;
 *  grant RESOURCE, CONNECT, DBA to c##pdbmaster container = all;
 *  
 *  select * from cdb_users;
 *  
 *  ---CREATE  SCHEMA's in PDB1---
 *  login to PDB1 as syspdb1
 *  
 *  create a table TEST_PDB1
 *  
 *  create role sys_pdb1 identified by syspdb1; //Not required
 *  SELECT * FROM user_role_privs;
 *  
 *   ---CREATE  SCHEMA's in PDB2---
 *   login to PDB2 as syspdb2
 *   
 *   create a table TEST_PDB2
 *     
 *  create role sys_pdb2 identified by syspdb2; //Not required
 *  SELECT * FROM user_role_privs;
 *  
 *  --To identify root service of CDB, login as sys/oracle as sysdba---
 *  select instance_name, version, status, con_id from v$instance;
 *  
 */
public class OracleUCPSingleMultitenantSharedPool extends Logging{
	
	static {
		System.setProperty("oracle.multitenant.enabled", "true");
	}
	
	/* Here cdb12 is root service of CDB */
	static String url = "jdbc:oracle:thin:@(DESCRIPTION=(SOURCE_ROUTE=YES) (ADDRESS=(PROTOCOL=TCP)(HOST=localhost)(PORT=1521))(CONNECT_DATA=(SERVICE_NAME=cdb12)))";
	static String user = "c##pdbmaster";
	static String password = "pdbmaster";
	static UniversalConnectionPoolManager ucpm;
	
	
	static PoolDataSource multiTenantDS;
	
	static void createSingleMultiTenantSharedPool() throws Exception {
		 ucpm = UniversalConnectionPoolManagerImpl.getUniversalConnectionPoolManager();
		 multiTenantDS = PoolDataSourceFactory.getPoolDataSource();
		 multiTenantDS.setConnectionFactoryClassName("oracle.jdbc.pool.OracleDataSource");
		 multiTenantDS.setURL(url);
		 multiTenantDS.setUser(user);
		 multiTenantDS.setPassword(password);
		 multiTenantDS.setInitialPoolSize(6);
		 multiTenantDS.setMinPoolSize(6);
		 multiTenantDS.setMaxPoolSize(12);
	}
	
    static Connection getPDB1Connection() throws Exception {
    	Properties tenantRole = new Properties();
    	tenantRole.put("CONNECT","syspdb1"); //Role not working
    	return multiTenantDS.createConnectionBuilder().serviceName("pdb1")/*.pdbRoles(tenantRole)*/.build();
    }
    
    static Connection getPDB2Connection() throws Exception {
    	Properties tenantRole = new Properties();
    	tenantRole.put("CONNECT","syspdb2"); //Role not working
    	return multiTenantDS.createConnectionBuilder().serviceName("pdb2")/*.pdbRoles(tenantRole)*/.build();
    }
    
    public static void main(String[] args) throws Exception {
    	createSingleMultiTenantSharedPool();
    	
    	//----------------PDB2 with schema SYSPDB2 ----------------//
    	Connection conPdb2 = getPDB2Connection();
    	testConnection(conPdb2);
    	conPdb2.close();
    	
       	conPdb2 = getPDB2Connection();
    	ResultSet rs = conPdb2.prepareStatement("select NAME, AGE from SYSPDB2.TABLE_PDB2").executeQuery();
    	while (rs.next()) {
			System.out.println(rs.getString(1) + "-" + rs.getInt(2));
		}
        rs.close();
        conPdb2.close();
        
        //----------------PDB1 with schema SYSPDB1 ------------------//
    	
    	Connection conPdb1 = getPDB1Connection();
    	testConnection(conPdb1);
    	conPdb1.close();
    	
    	conPdb1 = getPDB1Connection();
    	rs = conPdb1.prepareStatement("select NAME, AGE from SYSPDB1.TABLE_PDB1").executeQuery();
    	while (rs.next()) {
			System.out.println(rs.getString(1) + "-" + rs.getInt(2));
		}

		rs.close();
		conPdb1.close();
		
		ucpm.destroyConnectionPool(multiTenantDS.getConnectionPoolName());
	}
    
    static void testConnection(Connection conn) throws SQLException {
        Statement stmt = null;
        ResultSet rs = null;
        try {
          stmt = conn.createStatement();
          String query = "select sys_context('userenv', 'instance_name'),"
              + "sys_context('userenv', 'server_host'),"
              + "sys_context('userenv', 'service_name'),"
              + "sys_context('userenv', 'db_unique_name')" + ",user" + " from dual";
          rs = stmt.executeQuery(query);
          if (rs.next()) {
            String serviceName = rs.getString(3);
            String dbName = rs.getString(4);
            String userName = rs.getString(5);
            System.out.println("Connection Db name from sys context=" + dbName);
            System.out.println("Connection Svc name from sys context="
                + serviceName);
            System.out.println("Connection user Name : " + userName);
          }
        } catch (SQLException sqlexc) {
          throw sqlexc;
        } finally {
          if (rs != null)
            rs.close();
          if (stmt != null)
            stmt.close();
        }
      }

}
