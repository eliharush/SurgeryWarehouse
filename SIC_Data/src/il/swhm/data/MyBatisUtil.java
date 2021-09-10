package il.swhm.data;

import java.io.Reader;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.log4j.Logger;

public class MyBatisUtil {

	private static Logger logger=Logger.getLogger(MyBatisUtil.class);
	private static final String MYBATIS_CONFIGURATION_XML="il/swhm/data/config/MyBatisConfiguration.xml";

	private static SqlSessionFactory sqlSessionFactory=null;
	private static SqlSessionFactory sqlSessionFactorForEnv = null;
	private static String env=null;

	public static SqlSessionFactory getInstance(){
		if(sqlSessionFactory==null){
			try{
				Reader reader = Resources.getResourceAsReader(MYBATIS_CONFIGURATION_XML);
				sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
			}
			catch (Exception e)
			{
				logger.fatal("can't load MyBatis configuration",e);
			}
		}
		return sqlSessionFactory;
	}


	public static SqlSessionFactory getInstance(String env){
		if(sqlSessionFactorForEnv ==null || (env!=null && env.equals(MyBatisUtil.env))){
			try{
				Reader reader = Resources.getResourceAsReader(MYBATIS_CONFIGURATION_XML);
				if(env==null){
					sqlSessionFactorForEnv = new SqlSessionFactoryBuilder().build(reader);
				}
				else{
					sqlSessionFactorForEnv = new SqlSessionFactoryBuilder().build(reader,env);
				}
			}
			catch (Exception e)
			{
				logger.fatal("can't load MyBatis configuration",e);
			}
		}
		return sqlSessionFactorForEnv;
	}


}
