package il.swhm.web.job;

import il.swhm.data.dao.impl.GeneralDao;
import il.swhm.data.dao.interfaces.IGeneralDao;
import il.swhm.web.config.SICConfiguration;

import java.util.TimerTask;

import org.apache.log4j.Logger;

public class CleanDBTask extends TimerTask {
	private static Logger logger=Logger.getLogger(CleanDBTask.class);

	@Override
	public void run() {
		try{
			int period=SICConfiguration.getInstance().getCleanDBHistoryPeriod();
			int stationId=SICConfiguration.getInstance().getStationId();
			IGeneralDao dao=new GeneralDao();
			dao.cleanDb(stationId, period);
			logger.debug("Clean db run Ok.");
		}
		catch(Exception e){
			logger.error("Error occured while trying to clean DB.",e);
		}

	}

}
