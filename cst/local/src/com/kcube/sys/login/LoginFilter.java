package com.kcube.sys.login;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.kcube.lib.http.JslService;
import com.kcube.lib.jdbc.DbService;
import com.kcube.sys.tenant.TenantOnetime;
import com.kcube.sys.usr.UserService;
import com.kcube.sys.usr.UserSession;

/**
 * 모든 요청에 대해서 LoginModule을 호출한다.
 * <p>
 * 사이트마다 web.xml을 변경할 수 없으므로 Filter를 직접 이용하지 않고,
 * <p>
 * 사이트별 kcube.xml에서 지정된 LoginModule을 사용한다.
 */
public class LoginFilter implements Filter
{
	/**
	 * 이미 필터를 거쳤는지를 검사하는 boolean값의 key
	 */
	private static final String FILTER = "com.kcube.lib.http.JslFilter";
	private static final String ACCESS_METHOD = "Access-Control-Allow-Methods";
	private static final String ACCESS_METHOD_VALUE = "POST, GET, HEAD, OPTIONS";
	private static final String ACCESS_MAX = "Access-Control-Max-Age";
	private static final String ACCESS_HEADER = "Access-Control-Allow-Headers";
	private static final String ACCESS_HEADER_VALUE = "Content-Type, Accept, X-Requested-With";
	private static final String ACCESS_ORIGIN = "Access-Control-Allow-Origin";
	private static final String ACCESS_CREDENTIAL = "Access-Control-Allow-Credentials";
	private static final String ACCESS_CREDENTIAL_VALUE = "true";
	private static final String ORIGIN = "Origin";
	private static final String IEORIGIN = "IEORigin";
	private static final String HOST = "host";
	private static final String LOCALHOST = "localhost";
	private static final String NULL = "null";

	private static LoginModule _loginModule;
	private static Map<String, String> _menuCodes = new HashMap<String, String>();

	/**
	 * 설정된 로그인 모듈을 돌려준다.
	 */
	static LoginModule getLoginModule()
	{
		return _loginModule;
	}

	static void setLoginModule(LoginModule loginModule)
	{
		_loginModule = loginModule;
	}

	/**
	 * 메뉴코드를 저장한다.
	 */
	static void addMenuCodes(Map<String, String> menuCodes)
	{
		_menuCodes.putAll(menuCodes);
	}

	/**
	 * (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig filterConfig) throws ServletException
	{
	}

	/**
	 * (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy()
	{
	}

	/**
	 * 필터를 실행한다.
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
		throws IOException,
			ServletException
	{
		// 9i등에서는 forward/include 할 때에 filter가 실행되므로 한번만 실행되도록
		if (request.getAttribute(FILTER) == null)
		{
			request.setAttribute(FILTER, Boolean.TRUE);
			filter(request, response, chain);
		}
		else
		{
			try
			{
				HttpServletRequest req = (HttpServletRequest) request;
				HttpServletResponse res = (HttpServletResponse) response;

				dispatcherUrl(req, res);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			chain.doFilter(request, response);
		}
	}

	/**
	 * 현재 로그인한 사용자의 UserSessin정보를 session에 저장한다.
	 */
	private void filter(ServletRequest request, ServletResponse response, FilterChain chain)
		throws IOException,
			ServletException
	{
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		try
		{
			// request character set 설정
			String charset = JslService.getCharset(req);
			req.setCharacterEncoding(charset);
			doFilter(req, res, chain);
			// transaction 종료
			DbService.commitAndClose();
		}
		catch (IOException e)
		{
			DbService.rollbackAndCloseQuietly();
			throw e;
		}
		catch (ServletException e)
		{
			DbService.rollbackAndCloseQuietly();
			throw e;
		}
		catch (Exception e)
		{
			DbService.rollbackAndCloseQuietly();
			throw new ServletException(e);
		}
	}

	/**
	 * 현재 로그인한 사용자의 UserSession 정보를 session에 저장한다.
	 * <p>
	 * Thread 종료 후 userSession을 null로 초기화한다.
	 */
	protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws Exception
	{
		if (_loginModule == null)
		{
			throw new LoginModuleNotFoundException();
		}
		doCORS(req, res);
		UserSession userSession = null;
		try
		{
			userSession = _loginModule.getUserSession(req, res);
		}
		catch (Exception e)
		{
			return;
		}
		try
		{
			UserService.setUserSession(userSession);
			doSession(req, res, chain);
		}
		finally
		{
			UserService.setUserSession(null);
			TenantOnetime.removeOnetimeTenantId();
		}
	}

	/*
	 * 타도메인에서 AJAX 를 호출하면 응답해 줄 수 있도록 응답 헤더에 해당 도메인을 추가한다.
	 */
	private void doCORS(HttpServletRequest req, HttpServletResponse res) throws Exception
	{
		String origin = req.getHeader(ORIGIN);
		if (origin == null && !StringUtils.isEmpty(req.getParameter(IEORIGIN)))
		{
			origin = req.getParameter(IEORIGIN);
		}
		String cOrigin = null;

		if (origin != null && JslService.getCORSDomains().size() > 0)
		{
			String host = req.getHeader(HOST);
			String onlyOrigin = origin.substring(origin.indexOf("/") + 2);
			if (NULL.equals(origin) && JslService.getCORSDomains().contains(LOCALHOST))
			{
				cOrigin = origin;
			}
			else if (JslService.getCORSDomains().contains(origin))
			{
				cOrigin = origin;
			}
			else if (!onlyOrigin.equals(host))
			{
				for (String domain : JslService.getCORSDomains())
				{
					if (origin.contains(domain))
					{
						cOrigin = origin;
						break;
					}
				}
			}
		}
		if (cOrigin != null)
		{
			res.setHeader(ACCESS_METHOD, ACCESS_METHOD_VALUE);
			res.setHeader(ACCESS_MAX, JslService.getMaxAge());
			res.setHeader(ACCESS_HEADER, ACCESS_HEADER_VALUE);
			res.setHeader(ACCESS_CREDENTIAL, ACCESS_CREDENTIAL_VALUE);
			res.setHeader(ACCESS_ORIGIN, cOrigin);
		}
	}

	/**
	 * 주어진 경로에 해당하는 code의 로그를 남긴다.
	 * <p>
	 * forward된 경우 filter가 실행되지 않으므로 역시 로그를 남기지 않는다.
	 */
	private void doSession(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws Exception
	{
		String query = req.getQueryString();
		String url = req.getServletPath();
		if (query != null)
		{
			url += "?" + query;
		}

		String code = _menuCodes.get(url);
		if (code != null)
		{
			JslService.setHeaderNoCache(res);
			LoginHistory.log(code);
		}
		dispatcherUrl(req, res);
		chain.doFilter(req, res);
	}

	static void dispatcherUrl(HttpServletRequest req, HttpServletResponse res) throws Exception
	{
		LoginSpring.getLoginModule().dispatcherUrl(req, res);
	}
}
