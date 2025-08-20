package com.kcube.work.plan;

import com.kcube.doc.hist.HistoryCode;
import com.kcube.doc.hist.HistoryManager;
import com.kcube.sys.module.ModuleParam;
import com.kcube.work.Work;
import com.kcube.work.history.WorkItemHistory;
import com.kcube.work.history.WorkItemHistoryManager;

/**
 * 업무수행계획에 대한 로그처리 Class
 */
public class WorkPlanHistory
{
	/**
	 * 업무수행계획 추가
	 */
	public static final Integer REGISTER_WORK_PLAN = new Integer(HistoryCode.APPLY + 600);

	/**
	 * 업무수행계획 폐기
	 */
	public static final Integer REMOVE_WORK_PLAN = new Integer(HistoryCode.APPLY + 610);

	// custom
	/**
	 * 커스텀 업무수행계획 기준
	 */
	public static final Integer WORK_PLAN = new Integer(HistoryCode.APPLY + 700);

	/**
	 * 업무수행계획 상태가 변경됨.
	 */
	public static final Integer UPDATE_STATUS = new Integer(WORK_PLAN + 1);

	/**
	 * 업무 수행계획 일정 등록 시 로그를 남긴다.
	 */
	public static void registered(ModuleParam mp, Work item) throws Exception
	{
		// 업무 수행 계획 추가 시에 활동에 남김
		if (item.getStatus() != Work.DRAFT_STATUS)
		{
			WorkItemHistoryManager.history(WorkItemHistory.ADD_PLAN, item);
		}
		HistoryManager.history(REGISTER_WORK_PLAN, item);
	}

	/**
	 * 업무 수행계획 일정 삭제(폐기) 시에 로그를 남긴다.
	 * @param mp
	 * @param item
	 * @throws Exception
	 */
	static void deleted(ModuleParam mp, Work item) throws Exception
	{
		// 업무 수행 계획 추가 시에 활동에 남김
		if (item.getStatus() != Work.DRAFT_STATUS)
		{
			WorkItemHistoryManager.history(WorkItemHistory.REMOVE_PLAN, item);
		}
		HistoryManager.history(REMOVE_WORK_PLAN, item);
	}

	/**
	 * 업무수행계획 상태 수정시 로그를 남긴다.
	 * @param param
	 * @param plan
	 * @throws Exception
	 */
	static void updatedStatus(ModuleParam mp, WorkPlan plan) throws Exception
	{
		HistoryManager.history(UPDATE_STATUS, plan);
	}
}