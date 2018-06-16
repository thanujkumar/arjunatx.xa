package basics.wrapper.test.spf2;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.arjuna.ats.jbossatx.jta.TransactionManagerService;

public class SpringBasedWrapperMain {
	
	static {
		 //System.setProperty("java.util.logging.SimpleFormatter.format","[%1$tF %1$tT] [%4$-7s] [%3$s] %5$s %n");
		 ConsoleHandler consoleHandler = new ConsoleHandler();
		 consoleHandler.setLevel(Level.ALL);
		 consoleHandler.setFormatter(new SimpleFormatter());
		 //consoleHandler.setFormatter(new oracle.ucp.util.logging.UCPFormatter());

		 Logger app1 = Logger.getLogger("org.springframework"); // If you want to log everything just create logger with empty string
        app1.setLevel(Level.FINEST);
        app1.addHandler(consoleHandler);
       
        Logger app2 = Logger.getLogger("com.arjuna"); // If you want to log everything just create logger with empty string
        app2.setLevel(Level.FINEST);
        app2.addHandler(consoleHandler);
        
        Logger app3 = Logger.getLogger("org.h2");
        app3.setLevel(Level.FINEST);
        app3.addHandler(consoleHandler);
	}
	
	//Setup initial data as per the applicationContext-wrapper.xml testdb1 and testdb2
	
	private static ApplicationContext context;
	
	public static void main(String[] args) throws Exception {
		//https://access.redhat.com/documentation/en-US/JBoss_Enterprise_Web_Platform/5/html/Transactions_JTA_Development_Guide/sect-Transactions_JTA_Programmers_Guide-Test-Transaction_timeouts.html
		//Default Tx timeout, this can be overriden at xml configuration
		System.setProperty("com.arjuna.ats.arjuna.coordinator.defaultTimeout", "180");
		SetUp.setUp();
		SetUp.verify();
		
		context = new ClassPathXmlApplicationContext("applicationContext-wrapper.xml");
		DriverManagerDataSource ds1 =  context.getBean("dataSourceTestdb1", DriverManagerDataSource.class);
		System.out.println(ds1.getConnection());
		DriverManagerDataSource ds2 =  context.getBean("dataSourceTestdb2", DriverManagerDataSource.class);
		System.out.println(ds2.getConnection());
		
		//Next is transaction handling
		H2DbService service = context.getBean("operationService", H2DbService.class); //Ensure to get tx proxy bean
		
		System.out.println("===================" + service);
		
		if (Proxy.isProxyClass(service.getClass())) {
			InvocationHandler ihandle = Proxy.getInvocationHandler(service);
			System.out.println("=: Proxy->" + service +" and its handle" + ihandle);
		}
		
		
		service.doTxInsert();
		SetUp.verify();
		
		TransactionInterceptor txIn; // This is the interceptor for the below
		JtaTransactionManager jta;
		TransactionSynchronizationManager txSM;
		TransactionAspectSupport tasp;
		TransactionManagerService tms;
			
	}
}
