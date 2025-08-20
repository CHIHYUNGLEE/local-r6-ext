package com.kcube.sys.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.kcube.lib.http.FilterRequest;

public class LocalRequest extends HttpServletRequestWrapper
{

	public LocalRequest(HttpServletRequest request) {
		super(request);
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * @see javax.servlet.ServletRequestWrapper#getParameter(java.lang.String)
	 */
	public String getParameter(String name)
	{
		String result = null;
		try
		{
			result = ((HttpServletRequest) getRequest()).getParameter(name);
			if (result != null)
			{
				result = FilterRequest.replaceValue(name, result);
			}
		}
		catch (Exception e)
		{

		}
		return result;
	}
}
