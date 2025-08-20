JSV.addLang('ProcessSimpleReport', {
	'title' : '보고서 요약문'
});
JSV.addLang('WorkPlanPopup', {
	'WorkPlanPopupWriteTitle' : '업무방 수행 계획 작성',
	'WorkPlanPopupEditTitle' : '업무방 수행 계획 수정',
	'WorkPlanPopupViewTitle' : '업무방 수행 계획'
});
JSV.addLang('WorkStatusFieldEditor', {
	'working' : '작업중',
	'complete' : '완료',
	'reviewing' : '검토중',
	'approving' : '승인중',
	'reviewCmplt' : '검토완료',
	'needMore' : '보완요청',
	'stay' : '대기',
	'doing' : '진행',
	'stop' : '정지',
	'aprvComplete' : '승인완료',
	'layerTitle' : '기여도',
	'cpltMsg' : '완료 처리 하시겠습니까?\n완료 시 더 이상 상태를 변경할 수 없습니다.',
	'subPlanException' : '모든 업무 수행 계획이 완료되어 있어야 완료할 수 있습니다.'
});
JSV.addLang('WorkSecurityFieldEditor', {
	'checkBox' : '0:목록숨김'
});
JSV.addLang('WorkSecurityGradeViewer', {
	'scrtHide' : '목록숨김'
});
JSV.addLang('WorkElementController', {
	'helpMsg' : '<font color="#448ccb">필요한 기능</font><font color="#6f6f6f">들을</font><font color="#448ccb"> 선택</font><font color="#6f6f6f">하실 수 있습니다.</font>'
});
JSV.addLang('HelperThumbViewer', {
	'helperPopupTitle' : '협업자'
});
JSV.addLang('ShareViewer', {
	'helperPopupTitle' : '협업자',
	'addShare' : '사용자 추가',
	'message01' : ' 외 ',
	'message02' : ' 명 공유',
	'noShare' : '공유자가 없습니다.',
	'placeholder' : '이름입력 후 엔터',
	'del' : '공유자에서 제외하시겠습니까?',
	'err' : '오류가 발생했습니다.'
});
JSV.addLang('WorkReferenceComponent', {
	'fvrt' : '북마크 선택',
	'url' : 'URL 작성',
	'file' : '파일선택',
	'select' : {
		text : '추가',
		className : 'H25_blue'
	},
	'messageDialog' : '※ 추가할 사이트의 정보를 입력하세요.',
	'headerURL' : 'URL추가',
	'addError' : '권한이 없습니다.',
	'addFileDrmError' : '파일에 권한이 없습니다.',
	'uploadDenied' : '업무방 총 파일 크기를 초과하였습니다.',
	'fileUpload' : '파일 업로드',
	'fileUploadStatus' : '진행 상태',
	'fileName' : '파일이름',
	'fileSize' : '파일크기',
	'fileAdd' : '파일 추가',
	'complete' : '완료',
	'confirmDeleteMsg' : '참고자료를 삭제 하시겠습니까?',
	'fileLimitLabel' : '파일별 : ',
	'docLimitLabel' : '업무방 최대 : ',
	'filemsg' : '참고자료가 없습니다.<br>',
	'filemsg1' : 'PC에서<font color="#e2651d"> 파일을 끌어다</font> 놓거나, ',
	'filemsg2' : '<font color="#e2651d">[추가]버튼을 클릭</font>하여 참고자료를 등록해주세요.',
	'helpMsg' : '<font color="#e2651d">파일을 추가</font>하려면 첨부할 파일을 <font color="#e2651d">마우스로 끌어다</font> 놓으세요.',
	'none' : '기타'
});
JSV.addLang('MyWorkTreeContentProvider', {
	'myWorkRootTree' : '나의업무방'
});
JSV.addLang('FeedPortlet', {
	'add_sympathy' : {
		'action' : '@{user}님이 공감을 등록하였습니다.'
	},
	'del_sympathy' : {
		'action' : '@{user}님이 공감을 삭제하였습니다.'
	},
	'add_opinion' : {
		'action' : '@{user}님이 의견을 등록하였습니다.'
	},
	'update_opinion' : {
		'action' : '@{user}님이 의견을 수정하였습니다.'
	},
	'delete_opinion' : {
		'action' : '@{user}님이 의견을 삭제하였습니다.'
	},
	'add_share' : {
		'action' : '@{user}님이 @{refUser}님을 공유자로 등록하였습니다.'
	},
	'delete_share' : {
		'action' : '@{user}님이 @{refUser}님을 공유자에서 삭제하였습니다.'
	},
	'add_helper' : {
		'action' : '@{user}님이 @{refUser}님을 협업자로 등록하였습니다.'
	},
	'delete_helper' : {
		'action' : '@{user}님이 @{refUser}님을 협업자에서 삭제하였습니다.'
	},
	'change_working' : {
		'action' : '@{user}님이 작업중 상태로 변경하였습니다.'
	},
	'change_complete' : {
		'action' : '@{user}님이 완료 상태로 변경하였습니다.'
	},
	'add_plan' : {
		'action' : '@{user}님이 업무방 수행 계획을 추가하였습니다.'
	},
	'remove_plan' : {
		'action' : '@{user}님이 업무방 수행 계획을 삭제하였습니다.'
	},
	'update_itemVisible' : {
		'action' : '@{user}님이 기능 선택을 변경하였습니다.'
	},
	'update_apprsetting' : {
		'action' : '@{user}님이 검토 및 승인 설정을 변경하였습니다.'
	},
	'request_review' : {
		'action' : '@{user}님이 @{refUser}님에게 검토 요청을 하였습니다.'
	},
	'request_approval' : {
		'action' : '@{user}님이 @{refUser}님에게 승인 요청을 하였습니다.'
	},
	'processed_review' : {
		'action' : '@{user}님이 검토하였습니다.'
	},
	'complete_review' : {
		'action' : '검토가 완료되었습니다.'
	},
	'complete_approval' : {
		'action' : '@{user}님이 승인하였습니다.'
	},
	'request_need_supplement' : {
		'action' : '@{user}님이 보완을 요청하였습니다.'
	},
	'request_need_meeting' : {
		'action' : '@{user}님이 대면 요청하였습니다.'
	},
	'retrieved_approval' : {
		'action' : '@{user}님이 승인 요청 중에 회수하였습니다.'
	},
	'retrieved_review' : {
		'action' : '@{user}님이 검토 요청 중에 회수하였습니다.'
	},
	'changed_incharger' : {
		'action' : '@{refUser}님으로 담당자를 변경하였습니다.'
	},
	'delete' : {
		'action' : '@{user}님이 업무방을 삭제하였습니다.'
	},
	'change_secure' : {
		'action' : '@{user}님이 업무방의 공유레벨을 변경하였습니다.'
	},
	'workAttachment_add' : {
		'action' : '@{user}님이 @{fileName}을 추가하였습니다.',
		'url' : '/jsl/attach/WorkAttachmentVersioning.DownloadByUser?id='
	},
	'workAttachment_delete' : {
		'action' : '@{user}님이 @{fileName}을 삭제하였습니다.'
	},
	'workAttachment_versionup' : {
		'action' : '@{user}님이 @{fileName}을 버전업하였습니다.',
		'url' : '/jsl/attach/WorkAttachmentVersioning.DownloadByUser?id='
	},
	'workAttachment_download' : {
		'action' : '@{user}님이 @{fileName}을 다운로드하였습니다.',
		'url' : '/jsl/attach/WorkAttachmentVersioning.DownloadByUser?id='
	},
	'report_add' : {
		'action' : '@{user}님이 @{fileName}을 추가하였습니다.',
		'url' : '/jsl/attach/WorkReportFileVersioning.DownloadByUser?id='
	},
	'report_delete' : {
		'action' : '@{user}님이 @{fileName}을 삭제하였습니다.'
	},
	'report_versionup' : {
		'action' : '@{user}님이 @{fileName}을 버전업하였습니다.',
		'url' : '/jsl/attach/WorkReportFileVersioning.DownloadByUser?id='
	},
	'report_download' : {
		'action' : '@{user}님이 @{fileName}을 다운로드하였습니다.',
		'url' : '/jsl/attach/WorkReportFileVersioning.DownloadByUser?id='
	},
	'reference_url_add' : {
		'action' : '@{user}님이 URL @{rfrnName}을 추가하였습니다.'
	},
	'reference_file_add' : {
		'action' : '@{user}님이 @{rfrnName}을 추가하였습니다.',
		'url' : '/jsl/attach/WorkReferenceAction.DownloadByUser?id='
	},
	'reference_delete' : {
		'action' : '@{user}님이 @{rfrnName}을 삭제하였습니다.'
	},
	'reference_download' : {
		'action' : '@{user}님이 @{rfrnName}을 다운로드하였습니다.',
		'url' : '/jsl/attach/WorkReferenceAction.DownloadByUser?id='
	},
	'noData' : '활동이 없습니다.'
});
JSV.addLang('WorkTeamMenu', {
	'title' : '팀 업무방'
});
JSV.addLang('WorkChatListViewer', {
	'moreText' : '더보기',
	'topText' : '맨위로',
	'error' : '글을 가져오는데 실패하였습니다.',
	'noMoreData' : '더보기 할 내용이 없습니다.'
});
JSV.addLang('WorkChatWriter', {
	'addFileDrmError' : '파일에 권한이 없습니다.',
	'attachment' : '첨부파일',
	'noContent' : '채팅 내용을 작성해주세요',
	'overCount' : '입력 가능한 최대 글자수를 초과했습니다.',
	'fileOnlyTitle' : '파일 모아보기',
	'error' : '오류가 발생했습니다.',
	'fileOnlyBtn' : {
		'text' : '파일 모아보기',
		'className' : 'SMALL'
	},
	'send' : '보내기',
	'uploaderror' : '허용된 용량이 초과되어 업로드 할 수 없습니다.',
	'fileAdd' : '파일 추가'
});
JSV.addLang('DocFileViewer', {
	'fileAdd' : '파일 추가',
	'fileEdit' : '수정',
	'fileDel' : '삭제',
	'fileHistory' : '버전/히스토리 조회',
	'fileUpload' : '파일 업로드',
	'cancelChk' : '권한이 없거나 문서가 이미 체크아웃 중입니다.',
	'cancelChkErr' : '권한이 없거나 문서가 체크아웃상태가 아닙니다.',
	'reportSelect' : {
		'text' : '보고서 작성',
		'title' : '보고서 작성',
		'className' : 'H24'
	},
	'IsNotAvailable' : '보고서 템플릿 기능은 조회화면에서 사용가능합니다.',
	'helpMsg' : '<font color="#e2651d">파일을 추가</font><font color="#6f6f6f">하려면 첨부할 파일을 </font><font color="#e2651d">마우스로 끌어다</font><font color="#6f6f6f"> 놓으세요.</font>',
	'fileLimitLabel' : '파일별 : ',
	'docLimitLabel' : '업무방 최대 : ',
	'addError' : '권한이 없습니다.',
	'zeroReportTemplate' : '사용 가능한 보고서 템플릿이 없습니다.',
	'checkOutString' : '님이 수정중 입니다.',
	'editX' : '수정해제',
	'preview' : '미리보기',
	'fileName' : '파일이름',
	'fileSize' : '파일크기',
	'fileUploadStatus' : '진행 상태',
	'complete' : '완료',
	'checkInMsg' : '파일을 체크인 합니다.',
	'uploadDenied' : '업무방 총 파일 크기를 초과하였습니다.',
	'addFileDrmError' : '파일에 권한이 없습니다.',
	'title' : '보고서 파일명'
});
JSV.addLang('ReportFileDivMsg', {
	'filemsg' : '보고서가 없습니다.<br>',
	'filemsg1' : 'PC에서<font color="#e2651d"> 파일을 끌어다</font> 놓거나,',
	'filemsg2' : '<font color="#e2651d"> [추가]버튼을 클릭</font>하여 보고서를 등록해주세요.'
});
JSV.addLang('AttachmentFileDivMsg', {
	'filemsg' : '붙임파일이 없습니다.<br>',
	'filemsg1' : 'PC에서<font color="#e2651d"> 파일을 끌어다</font> 놓거나,',
	'filemsg2' : '<font color="#e2651d"> [추가]버튼을 클릭</font>하여 붙임파일을 등록해주세요.'
});
JSV.addLang('WorkProcessEditor', {
	'reviewer' : '검토자',
	'approver' : '승인자',
	'reportSketch' : '보고 요약문'
});
JSV.addLang('ProcessStatusViewer', {
	'tdId0' : 'writer',
	'tdId1' : 'reviewers',
	'tdId2' : 'approver',
	'tdId3' : 'cmlpt',
	'title0' : '작성',
	'title1' : '검토',
	'title2' : '승인',
	'title3' : '완료',
	'id0' : 'writerArea',
	'id1' : 'reviewerArea',
	'id2' : 'approverArea',
	'id3' : 'cmlptArea',
	'more' : '더보기',
	'mainTitle' : '검토 및 승인',
	'settingBtn' : {
		'text' : '설정',
		'className' : 'H24'
	},
	'settingTitle' : '설정',
	'permissionDeniedException' : '검토 및 보고 설정을 변경할 권한이 없습니다.',
	'invalidStatusException' : '검토 및 보고 설정을 변경할 상태가 아닙니다.',
	'close' : '닫기'
});
JSV.addLang('ProcessHistoryViewer', {
	'title' : '검토 및 승인이력',
	'reqDate' : '요청일자 : ',
	'noData' : '더 이상 불러올 데이터가 없습니다.',
	'review' : '검토',
	'approbation' : '승인',
	'collect' : '회수',
	'reviewing' : '검토 대기',
	'approbating' : '승인 대기',
	'selfapprove' : '자가 승인',
	'supplementation' : '보완요청',
	'supplementComp' : '보완 후 완료',
	'supplementAppr' : '보완 후 재승인',
	'needOffmit' : '대면보고 요청',
	'retrieved' : '회수',
	'read' : '읽음'
});
JSV.addLang('CheckBoxTextAreaEditor', {
	'label' : '보고시 사용할 요약문을 별도로 작성합니다.'
});
JSV.addLang('FolderImageViewer', {
	'none' : '분류없음'
});
JSV.addLang('WorkOrgSelectFieldEditor', {
	'scope' : '공유범위',
	'user' : '사용자',
	'defaultDuplicateLabel' : '업무방',
	'helpMsg' : '는 ',
	'helpMsg2' : '명 이상 등록할 수 없습니다.',
	'duplicateMsg' : '담당자와 동일한 협업자는 추가할 수 없습니다.'
});
JSV.addLang('AutoDprtUserNameComplete', {
	'duplicated' : '이미 공유자로 설정되어있습니다.',
	'selectError' : '공유할 사용자를 선택하세요.'
});
 