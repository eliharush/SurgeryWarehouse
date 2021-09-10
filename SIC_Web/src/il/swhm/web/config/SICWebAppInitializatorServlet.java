package il.swhm.web.config;

import il.swhm.data.DataFacade;

import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * Servlet implementation class WFWebAppInitializator
 */
@WebServlet("/SICWebAppInitializatorServlet")
public class SICWebAppInitializatorServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static final String SIC_CONFIG="conf/sic_conf.properties";
	public static final String SAPJCO_CONFIG="conf/sapjco.properties";
	public static final String CRMJCO_CONFIG="conf/crmjco.properties";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SICWebAppInitializatorServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	@Override
	public void init() throws ServletException {
		//int cleanDbPeriod=SICConfiguration.getInstance().getCleanDBRunPeriod();
		//Timer timer=new Timer(true);
		//timer.scheduleAtFixedRate(new CleanDBTask(), cleanDbPeriod*24*60*60, cleanDbPeriod*24*60*60);
		 // get class loader
        ClassLoader loader = SICWebAppInitializatorServlet.class.getClassLoader();
        if(loader==null)
          loader = ClassLoader.getSystemClassLoader();
        try{
        	setSICConfiguration(loader);
        	setSAPJCOConfiguration(loader);
        	setCRMJCOConfiguration(loader);
        }
        catch(Exception e){
        	System.out.println("Exception occured while loading web app configuration.e["+e+"]");
        	e.printStackTrace();
        }
		
	}
	
	private void setSICConfiguration(ClassLoader cl) throws Exception{
		PropertiesConfiguration config=getConfiguration(PropertiesConfiguration.class, cl,SIC_CONFIG);
		SICConfiguration.getInstance().setConfig(config);		
	}
	
	private void setSAPJCOConfiguration(ClassLoader cl) throws Exception{
		PropertiesConfiguration config=getConfiguration(PropertiesConfiguration.class, cl,SAPJCO_CONFIG);
		DataFacade.getSapJcoConfiguration().setConfig(config);
	}
	
	private void setCRMJCOConfiguration(ClassLoader cl) throws Exception{
		PropertiesConfiguration config=getConfiguration(PropertiesConfiguration.class, cl,CRMJCO_CONFIG);
		DataFacade.getCrmJcoConfiguration().setConfig(config);
	}
	
	private <T extends FileConfiguration> T getConfiguration(Class<T> clazz,ClassLoader cl,String urlName) throws Exception{
			T config;
			config = clazz.newInstance();
			URL url=cl.getResource(urlName);
           	config.load(url.openStream());
           	return config;
	}
    
    

	

}
