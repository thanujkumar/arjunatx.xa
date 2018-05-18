package arjunatx.xa.jdbc;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MainOperationApp {

	private static ApplicationContext context;
	
	public static void main(String[] args) {
		context = new ClassPathXmlApplicationContext("applicationContext-jdbc.xml");
		OperationService op = (OperationService) context.getBean("operationService",OperationService.class);
		System.out.println(op);
		
		TEST_TX obj1 = new TEST_TX();
		obj1.setAge(6);
		obj1.setName("Nimisha");
		int i = op.doTxInsert(obj1);
		System.out.println(i +" Record(s) inserted");
		
	}
	
	
}
