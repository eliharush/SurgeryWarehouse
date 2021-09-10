package il.swhm.web.config;

import java.net.URL;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

import com.sun.security.auth.module.NTSystem;

public class UserGrabber {
	public static final String SIC_CONFIG="conf/sic_conf.properties";
	public static Logger logger=Logger.getLogger(UserGrabber.class);
	public static void main(String[] argv){
		try{
		ClassLoader cl=UserGrabber.class.getClassLoader();

		PropertiesConfiguration config=new PropertiesConfiguration();
		URL url=cl.getResource(SIC_CONFIG);
		logger.debug("Got the properties file ..");
		config.load(url.openStream());
		logger.debug("Properties loaded...");
		SICConfiguration.getInstance().setConfig(config);		
		
		NTSystem nySystem = new NTSystem();
		logger.debug("Current user is " + nySystem.getName());
		SICConfiguration.getInstance().setUser(nySystem.getName());
		logger.debug("Write user in confgiration");
		config.save(url);
		logger.debug("Config saved.");
		}
		catch (Exception e) {
			logger.error("Exception occured.",e);
			
		}

	}
}
