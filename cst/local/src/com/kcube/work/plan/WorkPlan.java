package com.kcube.work.plan;

import java.util.Date;
import java.util.List;

import com.kcube.doc.Item;
import com.kcube.sys.usr.User;
import com.kcube.sys.usr.UserService;

/**
 * @author 김경수
 *         <P>
 *         업무방 수행 계획 Bean class
 */
public class WorkPlan extends Item
{
	private static final long serialVersionUID = 819359906636376270L;

	private Long _workId;
	private Date _startDate;
	private Date _endDate;
	private List<User> _planActors;
	private Date _completeDate;

	/**
	 * 대기상태
	 */
	public static final int STAY_STATUS = 3400;

	/**
	 * 진행상태
	 */
	public static final int WORKING_STATUS = 3500;

	/**
	 * 정지상태
	 */
	public static final int STOP_STATUS = 3800;

	/**
	 * 완료상태
	 */
	public static final int COMPLETE_STATUS = 7000;

	/**
	 * 지연상태
	 */
	public static final int DELAY_STATUS = 3510;

	/**
	 * 업무방 일련번호를 돌려준다.
	 * @return
	 */
	public Long getWorkId()
	{
		return _workId;
	}

	public void setWorkId(Long workId)
	{
		_workId = workId;
	}

	/**
	 * 업무 수행 계획 시작 날짜를 돌려준다.
	 * @return
	 */
	public Date getStartDate()
	{
		return _startDate;
	}

	public void setStartDate(Date startDate)
	{
		_startDate = startDate;
	}

	/**
	 * 업무 수행 계획 종료 날짜를 돌려준다.
	 * @return
	 */
	public Date getEndDate()
	{
		return _endDate;
	}

	public void setEndDate(Date endDate)
	{
		_endDate = endDate;
	}

	/**
	 * 업무 수행 계획 담당자를 돌려준다.
	 * @return
	 */
	public List<User> getPlanActors()
	{
		return _planActors;
	}

	public void setPlanActors(List<User> planActors)
	{
		_planActors = planActors;
	}

	/**
	 * 업무 수행 계획 완료 날짜를 돌려준다.
	 * @return
	 */
	public Date getCompleteDate()
	{
		return _completeDate;
	}

	public void setCompleteDate(Date completeDate)
	{
		_completeDate = completeDate;
	}

	/**
	 * (PreWrite) 가장 빠른 시작 날짜를 설정한다.
	 * @param date
	 */
	public void setMinStartDate(Date date)
	{
		_startDate = date;
	}

	/**
	 * (PreWrite) 가장 빠른 시작 날짜를 설정한다.
	 * @param date
	 */
	public void setMaxStartDate(Date date)
	{
		_startDate = date;
	}

	/**
	 * (PreWrite) 가장 늦은 종료 날짜를 설정한다.
	 * @param date
	 */
	public void setMinEndDate(Date date)
	{
		_endDate = date;
	}

	/**
	 * (PreWrite) 가장 늦은 종료 날짜를 설정한다.
	 * @param date
	 */
	public void setMaxEndDate(Date date)
	{
		_endDate = date;
	}

	/**
	 * 현재 로그인한 사용자가 해당 Plan의 담당자인지를 체크한다.
	 */
	public boolean isPlanActor()
	{
		return (null == getPlanActors()) ? false : getPlanActors().contains(UserService.getUser());
	}

	/**
	 * 첨부파일 Class
	 */
	public static class Attachment extends com.kcube.doc.file.Attachment
	{
		private static final long serialVersionUID = 2792597947675268814L;
	}
}
