package com.kcube.work.recent;

import java.util.ArrayList;
import java.util.Date;

import com.kcube.sys.usr.User;
import com.kcube.work.process.WorkProcess;

/**
 * 가장 최근 이벤트 리스트
 * @author WJ
 */
public class WorkRecent
{
	private ArrayList<RecentData> _list;

	/**
	 * 최근 이벤트 리스트를 ArrayLisy형태로 돌려준다.
	 * @return
	 */
	public ArrayList<RecentData> getList()
	{
		return _list;
	}

	/**
	 * 최근 이벤트 리스트를 ArrayLisy형태로 설정한다.
	 * @param list
	 */
	public void setList(ArrayList<RecentData> list)
	{
		_list = list;
	}

	/**
	 * 최근 이벤트 출력을 위한 임시 객체 처리
	 * @author WJ
	 */
	public static class RecentData
	{
		private ArrayList<User> _actors;
		private Date _cmpltDate;
		private WorkProcess _prcs;

		public RecentData(ArrayList<User> actors)
		{
			_actors = actors;
		}

		public RecentData(Date cmpltDate)
		{
			_cmpltDate = cmpltDate;
		}

		public RecentData(WorkProcess prcs)
		{
			_prcs = prcs;
		}

		public ArrayList<User> getActors()
		{
			return _actors;
		}

		public void setActors(ArrayList<User> actors)
		{
			_actors = actors;
		}

		public Date getCmpltDate()
		{
			return _cmpltDate;
		}

		public void setCmpltDate(Date cmpltDate)
		{
			_cmpltDate = cmpltDate;
		}

		public WorkProcess getPrcs()
		{
			return _prcs;
		}

		public void setPrcs(WorkProcess prcs)
		{
			_prcs = prcs;
		}
	}
}