package il.swhm.data.config;

import org.apache.commons.configuration.PropertiesConfiguration;


public class CrmJcoConfiguration {
	
	public static final String HOST="JCO_ASHOST";
	public static final String SYSTEM_NUMBER="JCO_SYSNR";
	public static final String CLIENT="JCO_CLIENT";
	public static final String USER="JCO_USER";
	public static final String PASSWORD="JCO_PASSWD";
	public static final String LANG="JCO_LANG";
	public static final String POOL_CAPACITY="JCO_POOL_CAPACITY";
	public static final String PEAK_LIMIT="JCO_PEAK_LIMIT";
	public static final String ORDER_BAPI="ORDER_BAPI";
	public static final String HOSPITAL_DATA_TABLE_NAME="HOSPITAL_DATA_TABLE_NAME";
	public static final String USERS_DATA_TABLE_NAME="USERS_DATA_TABLE_NAME";
	public static final String CONNECTIVITY_TEST_TIMEOUT="CONNECTIVITY_TEST_TIMEOUT";
	
	
	private PropertiesConfiguration config=new PropertiesConfiguration();
	private static CrmJcoConfiguration instance=null;
	
	public static CrmJcoConfiguration getInstance(){
		if(instance==null){
			instance=new CrmJcoConfiguration();
		}
		return instance;
	}
	
	private CrmJcoConfiguration(){
		super();
	}
	
	public void setConfig(PropertiesConfiguration configuration){
		this.config=configuration;
	}

	public String getHost(){
		return config.getString(HOST);
	}
	
	public String getSystemNumber(){
		return config.getString(SYSTEM_NUMBER);
	}
	
	public String getClient(){
		return config.getString(CLIENT);
	}
	
	public String getUser(){
		return config.getString(USER);
	}
	
	public String getPassword(){
		return config.getString(PASSWORD);
	}
	
	public String getLanguage(){
		return config.getString(LANG);
	}
	
	
	public String getPoolCapacity(){
		return config.getString(POOL_CAPACITY);
	}
	
	public String getPeakLimit(){
		return config.getString(PEAK_LIMIT);
	}
	
	public String getOrderBapi(){
		return config.getString(ORDER_BAPI);
	}
	
	public String getHospitalTableName(){
		return config.getString(HOSPITAL_DATA_TABLE_NAME);
	}
	
	public String getUsersTableName(){
		return config.getString(USERS_DATA_TABLE_NAME);
	}

	public int getConnectTestTimeout(){
		return config.getInt(CONNECTIVITY_TEST_TIMEOUT, 3000);
	}
	
}
