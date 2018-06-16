package basics;

import org.h2.tools.Server;

import basics.H2DSFactory.H2WrapperDataSouce;

public class H2AccessInMemoryFromBrowser {
	
	public static void main(String[] args) throws Exception {
		H2WrapperDataSouce wrapper = H2DSFactory.getDataSource();
		H2WrapperDataSouce.verify();
		
		Server h2WebServer = Server.createWebServer("-web","-webAllowOthers","-webPort","8082");
		h2WebServer.start();
		
		//To run as database server through program
		//Server h2Server = Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092");
		//h2Server.start();
		
	}

}
