package com.kcube.work;

/**
 * @author WJ
 *         <P>
 *         업무방 관련 Exception
 */
public class WorkFileException
{
	/**
	 * @author WJ
	 *         <P>
	 *         이미 체크아웃 되어 있을 경우
	 */
	public static class WorkAlreadyCheckout extends Exception
	{
		private static final long serialVersionUID = 8660746218961839767L;
	}

	/**
	 * @author WJ
	 *         <P>
	 *         이미 체크아웃 되어 있을 경우
	 */
	public static class WorkFileCheckedOut extends Exception
	{
		private static final long serialVersionUID = -220691277126554075L;
	}

	/**
	 * 업무방에 업로드를 제한할때 발생한다.
	 */
	public static class WorkUploadDeniedException extends Exception
	{
		private static final long serialVersionUID = -9040805200991770205L;
	}
}
