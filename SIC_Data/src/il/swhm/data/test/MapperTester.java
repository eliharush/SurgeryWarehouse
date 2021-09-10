package il.swhm.data.test;

import il.swhm.data.MyBatisUtil;
import il.swhm.data.mappers.GeneralMapper;

import org.apache.ibatis.session.SqlSession;


public class MapperTester {
	
	
	public static void main(String [] args){
			SqlSession session=null;
			try{
				session=MyBatisUtil.getInstance().openSession();
				GeneralMapper mapper=session.getMapper(GeneralMapper.class);
				mapper.selectApprovers();
				
				
			}
			finally{
				if(session!=null){
					session.close();
				}
			}
	}
	
}
