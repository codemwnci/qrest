package qweb;

import java.io.*;
import java.util.*;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import static java.lang.String.format;
import static spark.Spark.*;
import static qweb.Util.*;

public class ApplicationBootstrap {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private Properties props = new Properties();

	@SuppressWarnings("rawtypes")
	public ApplicationBootstrap() {
		
		try {
			props.load(new FileInputStream("app.conf"));
			configureDB();
		} catch (Exception e1) {
			logger.warn("DB Connection Error", e1);
		}
		
		setPort(Integer.parseInt(getOrElse(props.getProperty("port"), "9000")));
		externalStaticFileLocation("/Users/wayne/git/isitmygo/client");
		JWT.SECRET = props.getProperty("app.secret").trim();
		
		logger.info("Loading routes");
		Set<Class<?>> classes = new Reflections("").getTypesAnnotatedWith(Controller.class);		
		logger.info(format("Found %d controllers", classes.size()));		
				
		for ( Class clazz : classes ) {
			logger.info("Loading Controller -> " + clazz.getName());
			
			try {
				clazz.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				logger.error("Problem launchingcontroller {}", clazz.getName(), e);
				System.exit(1);
			}
		}		
	}
	
	private void configureDB() throws Exception {
				
		if (props.getProperty("db.driver") != null) {			 
			// read app.conf to get these values
			ComboPooledDataSource cpds = new ComboPooledDataSource();
			cpds.setDriverClass( props.getProperty("db.driver").trim() ); //loads the jdbc driver            
			cpds.setJdbcUrl( props.getProperty("db.url").trim() );
			cpds.setUser( props.getProperty("db.user").trim() );                                  
			cpds.setPassword( props.getProperty("db.pass").trim() ); 
			cpds.setAutoCommitOnClose(true);
			
			DB.pds = cpds;
		}
	}

	public static void main(String [] args) {		
		new ApplicationBootstrap();
	}	
}
