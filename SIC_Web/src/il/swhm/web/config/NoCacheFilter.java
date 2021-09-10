package il.swhm.web.config;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class NoCacheFilter implements Filter{
	private static String PRAGMA_HEADER="Pragma";
	private static String CACHE_CONTROL_HEADER="Cache-Control";
	private static String EXPIRES_HEADER="Expires";
	
	private static Logger logger=Logger.getLogger(NoCacheFilter.class);
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req=(HttpServletRequest)request;
		HttpServletResponse res=(HttpServletResponse)response;
		
		try{
			logger.trace("HtmlNoCache:setting no cache for request ["+req.getRequestURL().toString()+"]");
			res.setHeader(CACHE_CONTROL_HEADER, "no-cache, must-revalidate");
			res.setHeader(PRAGMA_HEADER, "No-cache");
			res.setDateHeader(EXPIRES_HEADER, 1);
			chain.doFilter(request, response);
		}
		catch (Exception e) {
			logger.error(": error occured request ["+req.getRequestURL().toString()+"].",e);
			chain.doFilter(request, response);
		}
		
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
		
	}

}
