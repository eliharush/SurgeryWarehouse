package il.swhm.web.config;

import org.apache.commons.configuration.PropertiesConfiguration;

public class SICConfiguration {
	
	public static final String STATION_ID="station_id";
	public static final String USER="user";
	public static final String CLEAN_DB_RUN_PERIOD_IN_DAYS="clean_db_run_period_in_days";
	public static final String CLEAN_DB_HISTORY_PERIOD="clean_db_history_period";
	
	private PropertiesConfiguration config=new PropertiesConfiguration();
	private static SICConfiguration instance=null;
	
	public static SICConfiguration getInstance(){
		if(instance==null){
			instance=new SICConfiguration();
		}
		return instance;
	}
	
	private SICConfiguration(){
		super();
	}
	
	public void setConfig(PropertiesConfiguration configuration){
		this.config=configuration;
	}
	
	public int getStationId(){
		return config.getInt(STATION_ID,1);
	}
	
	public String getUser(){
		return config.getString(USER,null);
	}
	
	public void setUser(String user){
		config.setProperty(USER,user);
	}
	
	public int getCleanDBRunPeriod(){
		return config.getInt(CLEAN_DB_RUN_PERIOD_IN_DAYS, 1);
	}
	
	public int getCleanDBHistoryPeriod(){
		return config.getInt(CLEAN_DB_HISTORY_PERIOD, 90);
	}
	

}
