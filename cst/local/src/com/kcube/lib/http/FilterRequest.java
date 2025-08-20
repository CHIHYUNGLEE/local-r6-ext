package com.kcube.lib.http;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

/**
 * XSS 처리를 위한 FilterRequest Class
 */
public class FilterRequest
{
	/**
	 * @param key
	 * @param value
	 * @return
	 */
	public static String replaceValue(String key, String value)
	{
		if ("content".equals(key))
		{
			System.out.println("--------FilterRequest");
			System.out.println(key);
			System.out.println(value);
			System.out.println("-------end---------------FilterRequest");
			value = Jsoup.clean(value, Safelist.basic());
			//value = Jsoup.clean(value, Whitelist.basic()); jdk 1.7이하에서 사용
		}
		else
		{
			// value = stripXSS(value);
		}
		return value;
	}
}

