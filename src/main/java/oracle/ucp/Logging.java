package oracle.ucp;

import java.util.Set;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import oracle.ucp.jdbc.PoolDataSource;

public abstract class Logging {

	static {
		 //System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS.%1$tL %4$-7s [%3$s] (%2$s) %5$s %6$s%n");
		 System.setProperty("java.util.logging.SimpleFormatter.format","[%1$tF %1$tT] [%4$-7s] %5$s %n");
		 ConsoleHandler consoleHandler = new ConsoleHandler();
		 consoleHandler.setLevel(Level.ALL);
		 consoleHandler.setFormatter(new SimpleFormatter());
		 //consoleHandler.setFormatter(new oracle.ucp.util.logging.UCPFormatter());

		 Logger app1 = Logger.getLogger("oracle.ucp"); // If you want to log everything just create logger with empty string
        app1.setLevel(Level.FINEST);
        app1.addHandler(consoleHandler);
        
        Logger app2 = Logger.getLogger("com.arjuna");
        app2.setLevel(Level.FINEST);
        app2.addHandler(consoleHandler);
    }
	
	public static void  printStatistics(PoolDataSource ds) throws Exception {
		
		System.out.println("----------------------------------------------------------");
		System.out.println(ds.getStatistics());
//		System.out.println("Initial Pool Size : "+ ds.getInitialPoolSize());
//		System.out.println("Max Pool Size : "+ ds.getMaxPoolSize());
//		System.out.println("Available Connections : "+ ds.getAvailableConnectionsCount());
//		System.out.println("Borrowed Connections : "+ ds.getBorrowedConnectionsCount());
//		System.out.println("Max Reuse Count : "+ ds.getMaxConnectionReuseCount());
//		System.out.println("Connection Pool Name : "+ ds.getConnectionPoolName());
		System.out.println("----------------------------------------------------------");
	}
	
	protected static void printThreads() {
		Set<Thread> threads = Thread.getAllStackTraces().keySet();
		System.out.println("---------------------------------------------");
		for (Thread t : threads) {
			String name = t.getName();
		    Thread.State state = t.getState();
		    int priority = t.getPriority();
		    String type = t.isDaemon() ? "Daemon" : "Normal";
		    System.out.printf("%-20s \t %s \t %d \t %s\n", name, state, priority, type);
		}
		System.out.println("---------------------------------------------");
	}
}
