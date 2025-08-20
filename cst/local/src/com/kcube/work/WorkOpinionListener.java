package com.kcube.work;

/**
 * 업무방 의견 Listener
 */
public interface WorkOpinionListener
{
	/**
	 * 의견이 등록될때 실행되는 리스너
	 */
	public void add(Work item, Work.Opinion opn) throws Exception;

	/**
	 * 의견이 수정될때 실행되는 리스터
	 */
	public void update(Work item, Work.Opinion opn) throws Exception;

	/**
	 * 의견이 삭제될때 실행되는 리스너
	 */
	public void delete(Work item, Work.Opinion opn) throws Exception;
}
