package basics.wrapper.test.spf2;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class SpringBasedWrapperMain {
	
	//Setup initial data as per the applicationContext-wrapper.xml testdb1 and testdb2
	
	private static ApplicationContext context;
	
	public static void main(String[] args) throws Exception {
		SetUp.setUp();
		SetUp.verify();
		
		context = new ClassPathXmlApplicationContext("applicationContext-wrapper.xml");
		DriverManagerDataSource ds1 =  context.getBean("dataSourceTestdb1", DriverManagerDataSource.class);
		System.out.println(ds1.getConnection());
		DriverManagerDataSource ds2 =  context.getBean("dataSourceTestdb2", DriverManagerDataSource.class);
		System.out.println(ds2.getConnection());
		
		//Next is transaction handling
		H2DbService service = context.getBean("operationService", H2DbService.class); //Ensure to get tx proxy bean
		service.doTxInsert();
		SetUp.verify();
		
	}
}
