package com.kcube.work;

import com.kcube.sys.module.ModuleParam;

/**
 * 업무방 보고서에 대한 Listener
 * @author Soo
 */
public interface WorkReportFileListener
{
	/**
	 * 마지막 버전 삭제 시에 대한 처리
	 * @param file
	 * @throws Exception
	 */
	void lastVersionDeleted(Work.ReportFile file, ModuleParam mp) throws Exception;

	/**
	 * 보고서 버전 업 시에 대한 처리
	 * @param file
	 * @throws Exception
	 */
	void fileVersionUp(Work.ReportFile newFile, Work.ReportFile oldFile, ModuleParam mp) throws Exception;
}
