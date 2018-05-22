package jndi.custom;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

public class LocalInitialContextFactory implements InitialContextFactory {

	//Ensure this is static
	private static final LocalJndiContext ctx = new LocalJndiContext();
	
	@Override
	public Context getInitialContext(Hashtable<?, ?> environment) throws NamingException {
		return ctx;
	}

}
