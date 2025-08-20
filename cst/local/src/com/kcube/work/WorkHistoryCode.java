package com.kcube.work;

import com.kcube.doc.hist.HistoryCode;

/**
 * 업무방 히스토리코드를 관리한다.
 * @author 신운재
 */
public class WorkHistoryCode
{
	/**
	 * 업무방의 담당자로 지정됨.
	 */
	public static final Integer ASSIGN_ACTOR = new Integer(HistoryCode.APPLY + 55);

	/**
	 * 업무방의 협업자로 지정됨.
	 */
	public static final Integer ASSIGN_HELPER = new Integer(HistoryCode.APPLY + 56);

	/**
	 * 업무방의 공유자로 지정됨.
	 */
	public static final Integer ASSIGN_SHARE = new Integer(HistoryCode.APPLY + 57);

	/**
	 * 업무방 상태가 수정됨
	 */
	public static final Integer UPDATE_STATUS = new Integer(HistoryCode.APPLY + 58);

	/**
	 * 업무방 기능 선택이 변경됨
	 */
	public static final Integer UPDATE_ITEMVISIBLE = new Integer(HistoryCode.APPLY + 59);

	/**
	 * 업무방 수정
	 */
	public static final Integer UPDATE = new Integer(HistoryCode.APPLY + 60);

	/**
	 * 보고서 등록됨.
	 */
	public static final Integer REPORT_ADD = new Integer(HistoryCode.APPLY + 110);

	/**
	 * 보고서 버전업
	 */
	public static final Integer REPORT_VERSIONUP = new Integer(HistoryCode.APPLY + 111);

	/**
	 * 보고서 삭제
	 */
	public static final Integer REPORT_DELETE = new Integer(HistoryCode.APPLY + 112);

	/**
	 * 보고서 다운로드
	 */
	public static final Integer REPORT_DOWNLOAD = new Integer(HistoryCode.APPLY + 113);

	/**
	 * 첨부파일 등록됨.
	 */
	public static final Integer ATTACHMENT_ADD = new Integer(HistoryCode.APPLY + 114);

	/**
	 * 첨부파일 버전업.
	 */
	public static final Integer ATTACHMENT_VERSIONUP = new Integer(HistoryCode.APPLY + 115);

	/**
	 * 첨부파일 삭제.
	 */
	public static final Integer ATTACHMENT_DELETE = new Integer(HistoryCode.APPLY + 116);

	/**
	 * 첨부파일 다운로드.
	 */
	public static final Integer ATTACHMENT_DOWNLOAD = new Integer(HistoryCode.APPLY + 117);

	/**
	 * 참고자료 추가.
	 */
	public static final Integer REFERENCE_ADD = new Integer(HistoryCode.APPLY + 118);

	/**
	 * 참고자료 삭제.
	 */
	public static final Integer REFERENCE_DELETE = new Integer(HistoryCode.APPLY + 119);

	/**
	 * 참고자료 다운로드.
	 */
	public static final Integer REFERENCE_DOWNLOAD = new Integer(HistoryCode.APPLY + 120);

	/**
	 * 업무방에 공유를 요청함
	 */
	public static final Integer REQUEST_SHARE = new Integer(HistoryCode.APPLY + 210);

	/**
	 * 업무방에 공유 요청를 승인함
	 */
	public static final Integer APPROVE_SHARE = new Integer(HistoryCode.APPLY + 211);

	/**
	 * 업무방에 공유 요청를 반려함
	 */
	public static final Integer REJECT_SHARE = new Integer(HistoryCode.APPLY + 212);

	/**
	 * 검토 및 보고 설정을 변경함.
	 */
	public static final Integer UPDATE_APPRSETTING = new Integer(HistoryCode.APPLY + 310);

	/**
	 * 검토를 요청함.
	 */
	public static final Integer REQUEST_REVIEW = new Integer(HistoryCode.APPLY + 315);

	/**
	 * 승인을 요청함.
	 */
	public static final Integer REQUEST_APPROVAL = new Integer(HistoryCode.APPLY + 320);

	/**
	 * 검토함.
	 */
	public static final Integer PROCESSED_REVIEW = new Integer(HistoryCode.APPLY + 322);

	/**
	 * 검토를 완료함.
	 */
	public static final Integer COMPELTE_REVIEW = new Integer(HistoryCode.APPLY + 325);

	/**
	 * 승인을 완료함.
	 */
	public static final Integer COMPELTE_APPROVAL = new Integer(HistoryCode.APPLY + 330);

	/**
	 * 보완 요청 함.
	 */
	public static final Integer NEED_MORE_REQ = new Integer(HistoryCode.APPLY + 335);

	/**
	 * 대면 요청함.
	 */
	public static final Integer NEED_MEETING_REQ = new Integer(HistoryCode.APPLY + 340);

	/**
	 * 검토 요청 회수함.
	 */
	public static final Integer RETRIEVED_REVIEW = new Integer(HistoryCode.APPLY + 350);

	/**
	 * 승인 요청 회수함.
	 */
	public static final Integer RETRIEVED_APPROVAL = new Integer(HistoryCode.APPLY + 355);

	/**
	 * 담당자를 변경함.
	 */
	public static final Integer CHANGED_INCHARGER = new Integer(HistoryCode.APPLY + 360);

	/**
	 * 문서등록 필요
	 */
	public static final Integer REQUEST_DOC_REGISTER = new Integer(HistoryCode.APPLY + 400);

	/**
	 * 웹채팅에 채팅글 등록함.
	 */
	public static final Integer REGISTER_WORK_CHAT = new Integer(HistoryCode.APPLY + 500);

	/**
	 * 업무수행계획 추가
	 */
	public static final Integer REGISTER_WORK_PLAN = new Integer(HistoryCode.APPLY + 600);

	/**
	 * 업무수행계획 폐기
	 */
	public static final Integer REMOVE_WORK_PLAN = new Integer(HistoryCode.APPLY + 610);
}
