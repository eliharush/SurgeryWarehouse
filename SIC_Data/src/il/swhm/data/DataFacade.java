package il.swhm.data;
import il.swhm.data.config.CrmJcoConfiguration;
import il.swhm.data.config.SapJcoConfiguration;
import il.swhm.data.dao.impl.ImportDao;
import il.swhm.data.dao.impl.InvCountDao;
import il.swhm.data.dao.interfaces.IImportDao;
import il.swhm.data.dao.interfaces.IInvCountDao;


public class DataFacade {
	private static IInvCountDao invCountDao;
	private static IImportDao importDao;
	
	static{
		invCountDao=new InvCountDao();
		importDao=new ImportDao();
	}

	public static IInvCountDao getInvCountDao() {
		return invCountDao;
	}

	public static IImportDao getImportDao() {
		return importDao;
	}
	
	public static SapJcoConfiguration getSapJcoConfiguration(){
		return SapJcoConfiguration.getInstance();
	}
	
	public static CrmJcoConfiguration getCrmJcoConfiguration(){
		return CrmJcoConfiguration.getInstance();
	}
	
	
	
}
