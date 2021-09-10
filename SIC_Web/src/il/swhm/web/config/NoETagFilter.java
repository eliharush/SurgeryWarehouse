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
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.log4j.Logger;
public class NoETagFilter implements Filter {
	public static Logger logger = Logger.getLogger(NoETagFilter.class);
	public static String CACHE_CONTROL_HEADER="Cache-Control";
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		try{
			logger.trace("request ["+ ((HttpServletRequest)request).getRequestURL().toString()+"] ip ["+request.getRemoteAddr()+"]");
			HttpServletResponseWrapper resWrapper=new HttpServletResponseWrapper((HttpServletResponse) response) {
			      public void setHeader(String name, String value) {
			          if (!"etag".equalsIgnoreCase(name)) {
			              super.setHeader(name, value);
			          }
			          else{
			        	  
			        	  logger.trace("Ommiting the ETag from request");
			          }
			      }
			};
			chain.doFilter(request,resWrapper);
		}
		catch (Exception e) {
			logger.error("NoETagFilter : error in filter.",e);
			chain.doFilter(request,response);
		}
		
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
		
	}

}
