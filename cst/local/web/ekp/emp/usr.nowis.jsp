<%@ include file="/jspf/head.portlet.jsp" %>
<%@page import="com.kcube.sys.usr.UserService"%>
<%@page import="com.kcube.space.portal.SpacePortalLegacyService"%>
<style>
/*중앙 메인조회영역 > Nowis*/
.now_is${PORTLET_ID}{}
.now_is${PORTLET_ID} .view_status{*zoom:1;padding-top:19px;padding-bottom:6px;margin:0 40px;border-bottom:1px solid #ededee;}
.now_is${PORTLET_ID} .view_status:after{content:'';display:block;clear:both;}
.now_is${PORTLET_ID} .view_status > .title{float:left;margin-top:7px;margin-right:10px;color:#4d7cff;font-size:14px;font-weight:bold;letter-spacing:-0.5px;}
.now_is${PORTLET_ID} .view_status .status{float:left;height:34px;margin-right:11px;}
.now_is${PORTLET_ID} .view_status .status > .text{margin-top:8px;color:#333;font-size:14px;letter-spacing:-0.5px;}
.now_is${PORTLET_ID} .view_status .status select{padding-right:21px;height:100%;border:none;background-image:url(<c:url value="/ekp/emp/img/arr_select_status.png"/>);background-position:right 0 top 15px;background-repeat:no-repeat;color:#333;font-size:14px;letter-spacing:-0.5px;appearance:none;-webkit-appearance:none;-moz-appearance:none;outline-style:none;transition:all .15s ease-out;}
.now_is${PORTLET_ID} .view_status .status select:hover{background-image:url(<c:url value="/ekp/emp/img/arr_select_status_hover.png"/>);}
.now_is${PORTLET_ID} .view_status .status select::-ms-expand{opacity:0;}
.now_is${PORTLET_ID} .view_status .message_area{position:relative;padding-left:8px;overflow:hidden;}
.now_is${PORTLET_ID} .view_status .message_area:before{content:'';position:absolute;top:11px;left:0;display:block;width:1px;height:13px;background:#e5e5e6;}
.now_is${PORTLET_ID} .view_status .message{position:relative;height:34px;}
.now_is${PORTLET_ID} .view_status .message .text_area_wrap{*zoom:1;display:inline-block;max-width:100%;}
.now_is${PORTLET_ID} .view_status .message .text_area_wrap:after{content:'';display:block;clear:both;}
.now_is${PORTLET_ID} .view_status .message .text_area_wrap .btn_history{float:right;padding:5px;margin-top:6px;margin-left:-3px;}
.now_is${PORTLET_ID} .view_status .message .text_area_wrap .btn_history img{display:block;opacity:0.3;transition:opacity 150ms ease-out;}
.now_is${PORTLET_ID} .view_status .message .text_area_wrap .btn_history:hover img{opacity:0.6;}
.now_is${PORTLET_ID} .view_status .message .text_area{height:34px;padding-left:6px;padding-right:4px;border:1px solid transparent;border-radius:4px;overflow:hidden;box-sizing:border-box;}
.now_is${PORTLET_ID} .view_status .message .text_area .text{padding-top:7px;color:#111;font-size:14px;font-weight:bold;letter-spacing:-0.5px;overflow:hidden;white-space:nowrap;text-overflow:ellipsis;}
.now_is${PORTLET_ID} .view_status .message .edit_input{display:none;*zoom:1;position:absolute;top:0;left:0;right:0;}
.now_is${PORTLET_ID} .view_status .message .edit_input:after{content:'';display:block;clear:both;}
.now_is${PORTLET_ID} .view_status .message .edit_input .btn_area{float:right;font-size:0;}
.now_is${PORTLET_ID} .view_status .message .edit_input .btn_area .KButton{outline-style:none;}
.now_is${PORTLET_ID} .view_status .message .edit_input .input_area{overflow:hidden;}
.now_is${PORTLET_ID} .view_status .message .edit_input .input_area .TextFieldEditor{padding-left:6px;padding-right:4px;color:#111;font-size:14px;font-weight:bold;letter-spacing:-0.5px;}
.now_is${PORTLET_ID} .view_status .message.edit_mode .edit_input{display:block;}
.now_is${PORTLET_ID} .view_status .message.nodata .text_area .text{color:#999;}
.now_is${PORTLET_ID} .view_status.myself{}
.now_is${PORTLET_ID} .view_status.myself .status > .text{display:none;}
.now_is${PORTLET_ID} .view_status.myself .message .text_area{cursor:pointer;}
.now_is${PORTLET_ID} .view_status.myself .message .text_area:hover{border-color:#e7e9ee;}
.now_is${PORTLET_ID} .view_status.others{}
.now_is${PORTLET_ID} .view_status.others .status select{display:none;}

.now_is${PORTLET_ID} .view_work{padding-top:15px;margin:0 40px;min-height:36px;}
.now_is${PORTLET_ID} .view_work .title_area{float:left;width:85px;padding-top:8px;font-size:0;}
.now_is${PORTLET_ID} .view_work .title_area .title{display:inline;margin-right:5px;color:#111;font-size:14px;font-family:'NotoSans',sans-serif;font-weight:500;}
.now_is${PORTLET_ID} .view_work .title_area .btn_history{display:inline-block;vertical-align:top;padding:5px;margin-top:-2px;margin-left:-4px;}
.now_is${PORTLET_ID} .view_work .title_area .btn_history img{display:block;opacity:0.3;transition:opacity 150ms ease-out;}
.now_is${PORTLET_ID} .view_work .title_area .btn_history:hover img{opacity:0.6;}
.now_is${PORTLET_ID} .view_work .work_area{margin-left:85px;}
.now_is${PORTLET_ID} .view_work .work_area .view_text{padding:5px 10px;border:1px solid transparent;border-radius:4px;font-size:0;}
.now_is${PORTLET_ID} .view_work .work_area .view_text:hover{border-color:#ddd;}
.now_is${PORTLET_ID} .view_work .work_area .view_text .text{display:block;margin-right:5px;color:#333;font-size:14px;letter-spacing:-0.5px;line-height:24px;white-space:normal;word-wrap:break-word;overflow:hidden;}
.now_is${PORTLET_ID} .view_work .work_area .edit_input{display:none;position:relative;padding-bottom:49px;border:1px solid #9aabff;border-radius:3px;}
.now_is${PORTLET_ID} .view_work .work_area .edit_input textarea{width:100%;height:34px;padding:5px 10px 0 10px;border:none;background:none;color:#333;font-size:14px;font-family:'Malgun Gothic','맑은 고딕','Gulim','Dotum',sans-serif;overflow:hidden;letter-spacing:-0.5px;line-height:24px;white-space:pre-wrap !important;word-wrap:break-word !important;box-sizing:border-box;resize:none;}
.now_is${PORTLET_ID} .view_work .work_area .edit_input .byte_cnt{position:absolute;height:15px;bottom:40px;right:20px;font-size:13px;}
.now_is${PORTLET_ID} .view_work .work_area .edit_input .wrt_btn{position:absolute;bottom:10px;right:10px;font-size:0;}
.now_is${PORTLET_ID} .view_work .work_area .edit_input .wrt_btn .KButton{outline-style:none;}
.now_is${PORTLET_ID} .view_work .work.edit_mode .view_text{display:none;}
.now_is${PORTLET_ID} .view_work .work.edit_mode .edit_input{display:block;}
.now_is${PORTLET_ID} .view_work .work.nodata .view_text .text{color:#999;}
.now_is${PORTLET_ID} .view_work.others .work_area .view_text:hover{border-color:transparent;}
.now_is${PORTLET_ID} .view_work.others .work.nodata{display:none;}
.now_is${PORTLET_ID} .view_work.others .work .title_area{display:none;}

.now_is${PORTLET_ID} .view_cal{padding-top:13px;margin:0 40px;}
.now_is${PORTLET_ID} .view_cal .cal_top{*zoom:1;margin-bottom:8px;}
.now_is${PORTLET_ID} .view_cal .cal_top:after{content:'';display:block;clear:both;}
.now_is${PORTLET_ID} .view_cal .cal_top .year_month{float:left;margin-top:3px;margin-right:3px;color:#111;font-size:15px;font-weight:bold;}
.now_is${PORTLET_ID} .view_cal .cal_top .KButton{outline-style:none;}
.now_is${PORTLET_ID} .view_cal .cal_tbl{position:relative;}
.now_is${PORTLET_ID} .view_cal .cal_tbl table{table-layout:fixed;width:100%;border:1px solid #e9e9e9;}
.now_is${PORTLET_ID} .view_cal .cal_tbl table th{padding-left:18px;height:39px;background:#fafafb;text-align:left;vertical-align:middle;}
.now_is${PORTLET_ID} .view_cal .cal_tbl table th:first-child{padding-left:18px;}
.now_is${PORTLET_ID} .view_cal .cal_tbl table td{height:36px;min-height:36px;border-left:1px solid #f2f2f2;text-align:left;vertical-align:top;}
.now_is${PORTLET_ID} .view_cal .cal_tbl table td:first-child{border:none;}
.now_is${PORTLET_ID} .view_cal .cal_tbl .today{float:right;padding:1px 3px 2px 3px;margin-right:10px;border-radius:2px;background:#fff1f0;color:#e97d7c;font-size:12px;font-weight:normal;}
.now_is${PORTLET_ID} .view_cal .cal_tbl .cal_header{padding-left:1px;overflow:hidden;}
.now_is${PORTLET_ID} .view_cal .cal_tbl .cal_header .day{float:left;margin-bottom:2px;margin-right:8px;color:#111;font-size:14px;font-weight:bold;}
.now_is${PORTLET_ID} .view_cal .cal_tbl .cal_header .work_time{float:left;color:#999;font-size:13px;font-weight:normal;letter-spacing:-0.5px;}
.now_is${PORTLET_ID} .view_cal .cal_tbl .schedule_list{padding:6px 20px 16px 18px;}
.now_is${PORTLET_ID} .view_cal .cal_tbl .schedule_list li{padding-top:6px;}
.now_is${PORTLET_ID} .view_cal .cal_tbl .schedule_list .time{padding-right:5px;display:table-cell;min-width:35px;padding-right:5px;color:#777;font-size:13px;letter-spacing:-0.5px;line-height:18px;}
.now_is${PORTLET_ID} .view_cal .cal_tbl .schedule_list .schedule{display:table-cell;color:#555;font-size:13px;letter-spacing:-0.5px;line-height:18px;}
.now_is${PORTLET_ID} .view_cal .cal_tbl .cal_nav{}
.now_is${PORTLET_ID} .view_cal .cal_tbl .cal_nav .nav{position:absolute;top:8px;display:block;width:24px;height:24px;border-radius:50%;background:#FFF;box-shadow:0px 1px 4px 0px rgba(0, 0, 0, 0.2);overflow:hidden;opacity:0;transition:opacity 150ms ease-out;}
.now_is${PORTLET_ID} .view_cal .cal_tbl .cal_nav .nav img{display:block;margin-top:8px;margin-left:10px;opacity:0.6;transition:opacity 150ms ease-out;}
.now_is${PORTLET_ID} .view_cal .cal_tbl .cal_nav .nav:hover img{opacity:1;}
.now_is${PORTLET_ID} .view_cal .cal_tbl .cal_nav .nav.prev{left:-12px;}
.now_is${PORTLET_ID} .view_cal .cal_tbl .cal_nav .nav.next{right:-12px;}
.now_is${PORTLET_ID} .view_cal .cal_tbl:hover .cal_nav .nav{opacity:1;}
.now_is${PORTLET_ID} .view_opn{padding-top:21px;margin:0 40px;}
</style>
<%@ include file="/calendar/head.calendar.jsp"%>
<script type="text/javascript">
JSV.Block(function () {
	var userId = JSV.getParameter('userId') || <%=UserService.getUserId()%>;
	var value = JSV.loadJSON('/jsl/EmployeeSelector.SelectUser.json?userId=' + userId);
	
	JSV.setState('userId', JSV.getParameter('userId'));
	var nowisDiv = $('.now_is${PORTLET_ID}');
	var workDiv = $('#workDiv${PORTLET_ID}');
	var myself = userId == <%=UserService.getUserId()%>;
	
	var options = ComboViewer.createOptions({options:'<fmt:message key="ptl.068"/>'});
	var conditionText = $('#conditionText${PORTLET_ID}');
	var conditionMessage = $('#conditionMessage${PORTLET_ID}');
	var conditionInput = conditionMessage.find('input');
	var conditionValue = '';
	if (value.conditionText && value.conditionText != null) {
		conditionValue = value.conditionText;
		conditionText.text(conditionValue);
		conditionInput.val(conditionValue);
	} else {
		conditionMessage.addClass('nodata');
		if (myself)
			conditionText.text('<fmt:message key="ptl.065"/>');
	}
	var workTextArea = workDiv.find('textarea');
	if (myself) {
		nowisDiv.find('.view_status').addClass('myself');
		conditionMessage.find('.text_area').click(function() {
			conditionMessage.addClass('edit_mode');
			conditionInput.focus();
	    });

		var selectTag = nowisDiv.find('select');
		for (var key in options) {
			$('<option>').prop('selected', value.condition == key).val(key).text(options[key]).appendTo(selectTag);
		}

		selectTag.on('change', this, function(e) {
			$.ajax({
				url : JSV.getContextPath('/jsl/EmployeeAction.UpdateCondition.json'),
				data : {condition:$(this).val()},
				dataType : 'json',
				success : function(res) {
				},
				error : function() {
				}
			});
		});
		
		workDiv.addClass('myself');
		workTextArea.on('keyup', this, function(e) {
			var $this = $(this);
			var max = $this.data('max');
			var now = TextAreaEditor.getBytesLength($this.val());
			$('.byte_cnt').text(now+' / '+max);
			if (max > 0 && now > max) {
				$('.byte_cnt').text(max+' / '+max);
				JSV.alert(JSV.getLang('TextAreaEditor', 'onkeyAlert1') + max + JSV.getLang('TextAreaEditor', 'onkeyAlert2'));
				$this.val($this.val().replace(/\r\n$/, ""));
				$this.val(TextAreaEditor.assertMsg($this.val(), max));
			}		
			resizeWorkTextArea();
		}).on('keydown', this, function(e) {
			resizeWorkTextArea();
		});
		workDiv.find('.view_text').click(function() {
			workDiv.find('.work').addClass('edit_mode');
			workTextArea.focus();
			resizeWorkTextArea();
	    });
		
		function resizeWorkTextArea() {
			workTextArea.height(1);
			workTextArea.height(workTextArea.prop('scrollHeight') - 5);
		}
	} else {
		workDiv.addClass('others');
		nowisDiv.find('.view_status').addClass('others');
		$('#statusText${PORTLET_ID}').text(options[value.condition]);
	}
	
	$('#historyBtn${PORTLET_ID}').on('click', this, function(e) {
		var style = {'width':480, 'height':449, 'autoOpen': false, 'title':'<fmt:message key="ptl.063"/>', 'dialogClass':'LayerModalDialog', 'resizable': false};
		JSV.showLayerModalDialog(JSV.getContextPath('/ekp/vitae/layer/conditionHistoryLayer.jsp?userId=' + userId), [], style);
	});
	
	var confirm = new KButton(document.getElementById('editBtnArea${PORTLET_ID}'), <fmt:message key="btn.pub.save_color"/>);
	confirm.onclick = function() {
		conditionMessage.removeClass('edit_mode');
		var value = conditionInput.val();
    	if (value == '<fmt:message key="ptl.065"/>') {
    		conditionMessage.addClass('nodata');
    		conditionText.text('<fmt:message key="ptl.065"/>');
    	} else {
    		if (value == '') {
    			conditionText.text('<fmt:message key="ptl.065"/>');
    		} else {
    			conditionMessage.removeClass('nodata');
        		conditionText.text(value);
    		}
			conditionValue = value;
    		$.ajax({
    			url : JSV.getContextPath('/jsl/EmployeeAction.UpdateCondition.json'),
    			data : {condition: selectTag.val(), conditionText:value},
    			dataType : 'json',
    			success : function(res) {
    			},
    			error : function() {
    			}
    		});
    	}
	}
	
	var cancel = new KButton(document.getElementById('editBtnArea${PORTLET_ID}'), <fmt:message key="btn.pub.cancel_large"/>);
	cancel.onclick = function() {
		conditionInput.val(conditionValue);
		conditionMessage.removeClass('edit_mode');
	}
	
	var workData = JSV.loadJSON('/jsl/VitaeAction.WorkReadByUser.json?userId=' + userId);
	var workDataId = null;
	var workDataTitle = '<fmt:message key="ptl.060"/>';
	var workTitle = '';
	if (workData && workData.id && workData.id != null) {
		workTitle = workData.title;
		if (workTitle == '') {
			workDiv.find('.work').addClass('nodata');
		} else {
			workTextArea.val(workTitle);
		}
		$('#workData${PORTLET_ID}').html(workTitle == '' ? workDataTitle : JSV.escapeHtml(workTitle).replace(/\n/gi, '<br>'));
		workDataId = workData.id;
	} else {
		$('#workData${PORTLET_ID}').text(workDataTitle);
		workDiv.find('.work').addClass('nodata');
	}
	
	workDiv.find('.btn_history').on('click', this, function(e) {
		var style = {'width':480, 'height':466, 'autoOpen': false, 'title':'<fmt:message key="vit.050"/>', 'dialogClass':'LayerModalDialog', 'resizable': false};
		JSV.showLayerModalDialog(JSV.getContextPath('/ekp/vitae/layer/workHistoryLayer.jsp?userId=' + userId), [], style);
	});
	
	var workConfirm = new KButton(document.getElementById('workEditBtns${PORTLET_ID}'), <fmt:message key="btn.pub.ok_round"/>);
	workConfirm.onclick = function() {
		workDiv.find('.work').removeClass('edit_mode');
		var value = workTextArea.val();
    	if (value == '<fmt:message key="ptl.060"/>') {
    		workDiv.find('.work').addClass('nodata');
    		workDiv.find('.text').text('<fmt:message key="ptl.060"/>');
    	} else {
    		var val = {};
    		val.id = workDataId;
    		val.title = workTextArea.val();
    		val.code = <%=com.kcube.ekp.vitae.Vitae.WORK_CODE%>;
			$.ajax({'url':JSV.getContextPath('/jsl/VitaeAction.UpdateWork.jsl'),
				'type' : 'POST',
				'dataType' : 'json',
				'data' : {vitae:JSV.toJSON(val), userId:userId},
				'success' : function(data, status) {
					workDiv.find('.work').removeClass('nodata');
					workTitle = workTextArea.val();
		    		workDiv.find('.text').html(workTitle == '' ? workDataTitle : JSV.escapeHtml(workTitle).replace(/\n/gi, '<br>'));
				},
				'error':function(xhr) {
					JSV.alert('error');
				}
			});
    	}
	}
	
	var workCancel = new KButton(document.getElementById('workEditBtns${PORTLET_ID}'), <fmt:message key="btn.pub.cancel_round"/>);
	workCancel.onclick = function() {
		workTextArea.val(workTitle);
		workDiv.find('.work').removeClass('edit_mode');
	}
	
	JSV.doLOAD('/sys/jsv/usr/empOpinion.jsp?userId='+ userId, 'commentFrame');
	
	var pref = JSV.loadJSON('/jsl/EmployeeAction.ReadPreference.json');
	
	var language = ClientUserInfo.Language('${locale}');
	
	<% if (SpacePortalLegacyService.isAccessEmbeddedApp("ADM_CALENDAR")) { %>
	var tz = ClientUserInfo.TimeZone(language , pref["<%=com.kcube.calendar.Calendar.CACHE_DEFAULT_TIMEZONE%>"]);
	
	var calNavi = $('#calNav${PORTLET_ID}');
	var calTableDiv = $('#calTableDiv${PORTLET_ID}');
	var yearMonthDiv = $('#yearMonth${PORTLET_ID}');
	var today = moment.tz(new Date(), tz);
	
	var dateNum = 0;
	var dateTerm = 3;
	calNavi.find('.prev').on('click', this, function(e){
		dateNum -= dateTerm;
		drawCal();
	});
	
	calNavi.find('.next').on('click', this, function(e){
		dateNum += dateTerm;
		drawCal();
	});
	//todayBtn
	var todayBtn = new KButton(document.getElementById('calTopDiv${PORTLET_ID}'), {'text':'<fmt:message key="calendar.007"/>', 'type':'BORDER', 'className':'SMALL'});
	todayBtn.onclick = function() {
		dateNum = 0;
		drawCal();
	}
	
	var days = JSV.getLang('CalendarView', 'day');
	var sDate, eDate;
	function drawCal() {
		var th = calTableDiv.find('thead th').empty();
		var monthText = '';
		for (var i = 0; i < dateTerm; i++) {
			var date = today.clone().add((dateNum + i), 'days');
			if (i == 0) {
				monthText = date.format('YYYY.MM');
				sDate = date;
			} else if (i == dateTerm - 1) {
				eDate = date;
			}
			
			var dayOfWeek = days[date.day()];

			if (dateNum + i == 0) {
				$('<sapn>').addClass('today').text('<fmt:message key="calendar.007"/>').appendTo(th.eq(i));
			}
			var calHeader = $('<div>').addClass('cal_header').appendTo(th.eq(i).attr('day', date.date()).data('day', date.date()));
			$('<p>').addClass('day').text(date.date() + '(' + dayOfWeek + ')').appendTo(calHeader);
		}
		yearMonthDiv.text(monthText);

		var param = {start:sDate.format('YYYY-MM-DD'), end:eDate.clone().add(1, 'days').format('YYYY-MM-DD'), timezone:tz, userIds:userId};
		$.ajax({
			url : JSV.getContextPath('/jsl/CaEventFindTime.EventListForUser.json'),
			data : param,
			dataType : 'json',
			context : this,
			success : function(data) {
				if (data) {
					drawDays(data);
				}
			},
			error : function(e) {
			}
		});
		
		//사이트에서 확장할때 사용..
		drawScheduleExt();
	}
	
	function drawDays(data) {
		var eMonth = parseInt(sDate.clone().endOf('month').format('D'));
		
		var year = sDate.year();
		var month = sDate.month();
		var start = sDate.date();
		var endDay = eDate.date();
		var eMonth = eDate.month();
		calTableDiv.find('tbody ul').empty();
		
		var viewData = [];
		for (var i = 0; i < data.length; i++) {
			if (viewData.indexOf(data[i].eventId) === -1) {
				viewData.push(data[i].eventId);
				var cd = data[i];
				var s = moment.tz(cd.start, tz);
				var e = moment.tz(cd.end, tz);
				
				if (cd.allDay) {
					e = e.add(-1, 'days');
				}
				cd.sDate = s;
				cd.eDate = e;
				
				var sDay = s.date();
				var eDay = !cd.allDay && e.format('HH:mm:ss') == '00:00:00' ? e.clone().add(-1, 'days').date() : e.date();

				var diffMonth = e.month() != s.month();
				if (e - s > 0 && eDay < start && diffMonth) {
					if (month == eMonth) {
	 					if ((year == s.year() && month > s.month()) || (year > s.year())) {
							sDay = 1;
						}
						if ((year == e.year() && month < e.month()) || (year < e.year())) {
							eDay = eMonth;
						}
					} else {
						if (diffMonth) {
							for (var idx = 1; idx <= eDay; idx++) {
								CreateCalLi(idx, cd, s, e);
							}
						}
						eDay = eMonth;
					}
				} else {
					sDay = sDay != eDay && sDay > start ? start : sDay;
					eDay = sDay != eDay && eDay > endDay ? endDay : eDay;
				}
				
				for (var idx = sDay; idx <= eDay; idx++) {
					CreateCalLi(idx, cd, s, e);
				}
			}
		}
	}
	
	function CreateCalLi(idx, cd, s, e) {
		var thHead = calTableDiv.find('thead th[day=' + idx + ']');
		if (thHead.length > 0) {
			var indexTd = thHead.index();
			var ul = calTableDiv.find('.schedule_list').eq(indexTd);
			if (ul.length > 0) {
				var li = $('<li>').appendTo(ul);
				var time = '';
				if (cd.allDay) {
					time = JSV.getLang('CalendarView', 'allDay');
				} else {
					if (idx == s.format('DD')) {
						time = s.format('HH:mm');
					} else if (idx == e.format('DD')) {
						time = ' ~ ' + e.format('HH:mm');
					} else {
						time = JSV.getLang('CalendarView', 'allDay');
					}
				}
				$('<p>').addClass('time').text(time).appendTo(li);
				
				var hTitle = cd.editable || cd.shareLevel == 1000 ? cd.title : cd.shareLevel == 2000 ? JSV.getLang('CalendarView', 'hasPlan') : JSV.getLang('CalendarView', 'secret');
				$('<p>').addClass('schedule').text(hTitle).appendTo(li);
			}
		}
	}
	
	<jsp:include page="/ekp/emp/usr.nowis-ext.jsp" flush="false"/>
	
	drawCal(0);
	<% } %>
});
</script>
<div class="now_is${PORTLET_ID}">
	<div class="view_status"><!--자신 myself/타인 others-->
		<p class="title">Now is</p>
		<div class="status">
			<p class="text" id="statusText${PORTLET_ID}"></p>
			<select></select>
		</div>
		<div class="message_area">
			<div class="message" id="conditionMessage${PORTLET_ID}"><!--nodata(데이터없을경우)/edit_mode(수정모드)-->
				<div class="text_area_wrap">
					<a href="javascript:void(0);" class="btn_history" id="historyBtn${PORTLET_ID}"><img src="<%= request.getContextPath() %>/ekp/emp/img/btn_history.png" alt="history"></a>
					<div class="text_area">
						<p class="text" id="conditionText${PORTLET_ID}"></p>
					</div>
				</div>
				<div class="edit_input">
					<div class="btn_area" id="editBtnArea${PORTLET_ID}"></div>
					<div class="input_area">
						<input type="text" class="TextFieldEditor" maxlength="28">
					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="view_work" id="workDiv${PORTLET_ID}"><!--자신 myself/타인 others-->
		<div class="title_area">
 			<p class="title"><fmt:message key="vit.050"/></p>
 			<a href="javascript:void(0);" class="btn_history"><img src="<%= request.getContextPath() %>/ekp/emp/img/btn_history.png" alt="history"></a>
 		</div>
 		<div class="work_area">
 			<div class="work">
	 			<div class="view_text">
	 				<p class="text" id="workData${PORTLET_ID}"></p>
	 			</div>
	 			<div class="edit_input">
	 				<textarea data-max="500" placeholder="<fmt:message key="ptl.060"/>"></textarea>
	 				<div class="byte_cnt"></div>
	 				<div class="wrt_btn" id="workEditBtns${PORTLET_ID}"></div>
	 			</div>
	 		</div>
 		</div>
	</div>
	<% if (SpacePortalLegacyService.isAccessEmbeddedApp("ADM_CALENDAR")) { %>
	<div class="view_cal">
		<div class="cal_top" id="calTopDiv${PORTLET_ID}">
			<p class="year_month" id="yearMonth${PORTLET_ID}"></p>
		</div>
		<div class="cal_tbl" id="calTableDiv${PORTLET_ID}">
			<table cellpadding="0" cellspacing="0" border="0">
				<thead>
					<tr>
						<th></th>
						<th></th>
						<th></th>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td><ul class="schedule_list"></ul></td>
						<td><ul class="schedule_list"></ul></td>
						<td><ul class="schedule_list"></ul></td>
					</tr>
				</tbody>
			</table>
			<div class="cal_nav" id="calNav${PORTLET_ID}">
				<a href="javascript:void(0);" class="nav prev"><img src="<%= request.getContextPath() %>/ekp/emp/img/nav_cal_prev.png" alt="prev"></a>
				<a href="javascript:void(0);" class="nav next"><img src="<%= request.getContextPath() %>/ekp/emp/img/nav_cal_next.png" alt="next"></a>
			</div>
		</div>
	</div>
	<% } %>
	<div class="view_opn">
		<div id="commentFrame" class="comment"></div>
	</div>
</div>
<%@ include file="/jspf/tail.portlet.jsp" %>