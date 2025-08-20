package com.kcube.work;

import com.kcube.doc.file.Attachment;

/**
 * 업무방 Listener
 */
public interface WorkListener
{
	/**
	 * 업무방이 등록된 후 호출 된다.
	 */
	void registered(Work server) throws Exception;

	/**
	 * 등록된 업무방이 삭제/기간만료 처리된다.
	 * <p>
	 * status를 이용하여 어떤 상태로 되었는지 구분할 수 있다.
	 */
	void deleted(Work item) throws Exception;

	/**
	 * 담당자 또는 협업자가 변경되었을 시에 호출된다.
	 * @param item
	 * @throws Exception
	 */
	void changeMember(Work item) throws Exception;

	/**
	 * 업무방 복구 시에 호출 된다.
	 * @param item
	 * @throws Exception
	 */
	void recover(Work item) throws Exception;

	/**
	 * 업무방이 완료상태일 때 호출된다.
	 * @param server
	 * @throws Exception
	 */
	void complete(Work server) throws Exception;

	/**
	 * 업무방이 작업중 상태일 때 호출된다.
	 * @param server
	 * @throws Exception
	 */
	void working(Work server) throws Exception;

	/**
	 * 업무방에 파일이 추가되었을때 호출.
	 * <p>
	 * 버전업 및 파일 추가
	 * @param server
	 * @throws Exception
	 */
	void AddAtt(Work server, Attachment att);

	/**
	 * 업무방에 파일이 삭제되었을때 호출.
	 * @param server
	 * @throws Exception
	 */
	void deleteAtt(Work server, Attachment att);
}
