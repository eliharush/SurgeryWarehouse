package il.swhm.data.config;

import org.apache.commons.configuration.PropertiesConfiguration;


public class SapJcoConfiguration {
	
	public static final String HOST="JCO_ASHOST";
	public static final String SYSTEM_NUMBER="JCO_SYSNR";
	public static final String CLIENT="JCO_CLIENT";
	public static final String USER="JCO_USER";
	public static final String PASSWORD="JCO_PASSWD";
	public static final String LANG="JCO_LANG";
	public static final String CODEPAGE="JCO_CODEPAGE";
	public static final String POOL_CAPACITY="JCO_POOL_CAPACITY";
	public static final String PEAK_LIMIT="JCO_PEAK_LIMIT";
	public static final String HOSPITAL_DATA_BAPI="HOSPITAL_DATA_BAPI";
	public static final String MAN_DATA_BAPI="MAN_DATA_BAPI";
	public static final String MARA_MAN_DATA_BAPI="MARA_MAN_DATA_BAPI";
	public static final String SUBSTITUTE_DATA_BAPI="SUBSTITUTE_DATA_BAPI";
	public static final String SUBSTITUTE_TABLE_NAME="SUBSTITUTE_TABLE_NAME";
	public static final String ALTERNATIV_DATA_BAPI="ALTERNATIV_DATA_BAPI";
	public static final String ALTERNATIV_TABLE_NAME="ALTERNATIV_TABLE_NAME";
	public static final String USERS_DATA_BAPI="USERS_DATA_BAPI";
	public static final String HOSPITAL_DATA_TABLE_NAME="HOSPITAL_DATA_TABLE_NAME";
	public static final String MAN_DATA_TABLE_NAME="MAN_DATA_TABLE_NAME";
	public static final String MARA_MAN_DATA_TABLE_NAME="MARA_MAN_DATA_TABLE_NAME";
	public static final String USERS_DATA_TABLE_NAME="USERS_DATA_TABLE_NAME";
	public static final String CONNECTIVITY_TEST_TIMEOUT="CONNECTIVITY_TEST_TIMEOUT";
	
	private PropertiesConfiguration config=new PropertiesConfiguration();
	private static SapJcoConfiguration instance=null;
	
	public static SapJcoConfiguration getInstance(){
		if(instance==null){
			instance=new SapJcoConfiguration();
		}
		return instance;
	}
	
	private SapJcoConfiguration(){
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
	
	public String getCodePage(){
		return config.getString(CODEPAGE);
	}
	
	public String getPoolCapacity(){
		return config.getString(POOL_CAPACITY);
	}
	
	public String getPeakLimit(){
		return config.getString(PEAK_LIMIT);
	}
	
	public String getHospitalDataBapi(){
		return config.getString(HOSPITAL_DATA_BAPI);
	}
	
	public String getManDataBapi(){
		return config.getString(MAN_DATA_BAPI);
	}
	
	public String getSubstituteDataBapi(){
		return config.getString(SUBSTITUTE_DATA_BAPI);
	}
	
	public String getSubstituteTableName(){
		return config.getString(SUBSTITUTE_TABLE_NAME);
	}
	
	public String getAlternativeDataBapi(){
		return config.getString(ALTERNATIV_DATA_BAPI);
	}
	
	public String getAlternativTableName(){
		return config.getString(ALTERNATIV_TABLE_NAME);
	}
	
	
	
	public String getUsersDataBapi(){
		return config.getString(USERS_DATA_BAPI);
	}
	
	public String getHospitalTableName(){
		return config.getString(HOSPITAL_DATA_TABLE_NAME);
	}
	
	public String getManTableName(){
		return config.getString(MAN_DATA_TABLE_NAME);
	}
	
	public String getUsersTableName(){
		return config.getString(USERS_DATA_TABLE_NAME);
	}
	
	public int getConnectTestTimeout(){
		return config.getInt(CONNECTIVITY_TEST_TIMEOUT, 3000);
	}

	public String getMaraManDataBapi(){
		return config.getString(MARA_MAN_DATA_BAPI);
	}
	
	public String getMaraManTableName(){
		return config.getString(MARA_MAN_DATA_TABLE_NAME);
	}
	

	

}
