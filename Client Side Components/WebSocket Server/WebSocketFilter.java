import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

@WebFilter(value="/bilevelserver")
public class WebSocketFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;

		final Map<String, String[]> parameters = new HashMap<String, String[]>();
		parameters.put("IP", new String[] { httpServletRequest.getRemoteAddr() });
		
		// wrapping the request object inside request wrapper
		HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper(httpServletRequest) {
			@Override
			public Map<String, String[]> getParameterMap() {
				return parameters;
			}
		};
		// passing the request and response object to next chain in filter until
		// it reaches server end point
		chain.doFilter(requestWrapper, response);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub

	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

}
