function OpnViewer(parent, style) {
	this.style = style || {};
	this.parent = parent;
	this.className = this.style.className || 'OpnViewer';
	this.isCenter = this.style.isCenter ? eval(this.style.isCenter) : false;
	this.isHideBorder = this.style.isHideBorder ? eval(this.style.isHideBorder) : false;
	this.isFold = (this.style.isFold && this.style.isFold=='true') ? true : false;
	this.maxLength = this.style.maxLength ? parseInt(this.style.maxLength) : 600;
	this.reloadUrl = this.style.reloadUrl;
	this.useEditable = this.style.useEditable ? eval(this.style.useEditable) : false;
	this.vrtl = this.style.vrtl || null;
	this.countPerOne = this.style.countPerOne || 30;
	this.layerMode = this.style.layerMode ? eval(this.style.layerMode) : false;
	this.printMode = this.style.printMode ? eval(this.style.printMode) : false;
	this.mentionUse = OpnWriter.mentionUseGlobal && (this.style.mentionUse == null ? true : eval(this.style.mentionUse));
	if (this.layerMode)
		this.printMode = true;
	this.opnList = {};
	this.seq = JSV.SEQUENCE++;
	this.appendRight = $(this.parent).hasClass('TemplateLayoutContent');
	var moreCnt = JSV.loadJSON('/jsl/ConfigAction.Value.json', 'key=com.kcube.doc.opn.OpinionConfig.more');
	this.moreCnt = moreCnt && moreCnt.value && moreCnt.value > 0 ? moreCnt.value : 50;
	this.desc = this.style.desc || true; //최신순 과거순 초기 선택여부(초기데이터까지 컨트롤할시 read페이지 호출하는액션에서 desc초기값을 set해줘야 합니다.)
	this.lastId = 0;
	this.gs = JSV.getParameter(this.style.gs) || JSV.getParameter('gs') || '';
	this.dataName = this.style.dataName || 'opinions';
	this.itemIdName = this.style.itemIdName ? this.style.itemIdName : 'id';
	this.itemId = this.style.itemId ? this.style.itemId : 'id';
	this.itemIdVal = this.style.itemIdVal;
	this.hashOpnId = JSV.getParameter('hashOpnId');
	this.isFix = (null != this.style.isFix) ? eval(this.style.isFix) : false;
	this.closestSlt = this.style.closestSlt || null;
	this.moreBtnEvent = true;
	var self = this;
	var headerName = this.style.headerName || JSV.getLang('OpnViewer', 'opn');
	if (this.appendRight && !this.desc)
		OpnViewer.RIGHTFRAME = $(this.parent).parent().get(0);

	var opnCnt = this.appendRight ? '<div class="opnVwCntDiv" id="opnVwCntMain' + this.seq + '">\
										<ul class="tab"><li class="tabItem selected" id="opnVwCntMainLi' + this.seq + '">\
										<a id="opnVwCntA' + this.seq + '" hidefocus="true" class="opnCntA">\
										<span class="text selectedTab">' + JSV.getLang('OpnViewer', 'opn') + '</span><span class="number selectedNumberTab" id="opnVwCnt' + this.seq + '"></span>\
										</a></li></ul>\
									</div>'
									: '<div id="opnVwCntMain' + this.seq + '" class="opnVwCntBDiv">\
											<ul class="tab"><li class="tabItem selected" id="opnVwCntArrow' + this.seq + '">\
												<a id="opnVwCntA' + this.seq + '" hidefocus="true" class="opnCntA">\
												<span class="text selectedTab">' + JSV.getLang('OpnViewer', 'opn') + '</span><span class="number selectedNumberTab" id="opnVwCnt' + this.seq + '"></span>'
									+ (this.style.sympathy ? '<span class="bar">/</span>\
															  <span class="text selectedTab">' + JSV.getLang('SympathyViewer', 'symp') + '</span><span class="number selectedNumberTab" id="sympVwCnt' + this.seq + '"></span>'
															  : '')
				                    + '</a></li><li class="refresh"><img id="refreshBtn' + this.seq + '" src="../../img/personal_btn_refresh.png"/></li></ul></div>';

	this.widget = $('<div>' + opnCnt + '<div class="opnVwContent" id="opnVwContent' + this.seq + '">\
							<div id="opnVwHeadDiv' + this.seq + '"></div>\
							<ul class="opnVwVsbUl" id="opnVwVsbUl' + this.seq  + '">\
								<li class="opnVwWrtRpy" id="opnVwWrtRpy' + this.seq  + '">\
									 <i class="i_cmt_reply"></i>\
								</li>\
							</ul>\
							<div id="opnVwTailDiv' + this.seq + '"></div>\
							<div id="opnVwMoreBar' + this.seq + '" class="opnVwMoreBar"></div>\
						</div>\
					</div>').attr('seq', this.seq).addClass(this.className).appendTo(parent);
	
	if (this.style.opnView) {
		this.widget.find('#opnVwHeadDiv' + this.seq)
			.after('<div class="opnVwFilter" id="opnVwFilter' + this.seq + '">\
						<a href="javascript:void(0);" class="select_filter" id="desc">' + JSV.getLang('OpnViewer', 'desc') + '</a>\
						<a href="javascript:void(0);" class="select_filter" id="asc">' + JSV.getLang('OpnViewer', 'asc') + '</a>\
					</div>');
		if (this.desc) {
			this.widget.find('#opnVwFilter' + this.seq).children('#desc').addClass('selected');
		} else {
			this.widget.find('#opnVwFilter' + this.seq).children('#asc').addClass('selected');
		}
	}
	
	if (this.appendRight) {
		if (this.style.rightColor)
			$(this.parent).parent().css('background-color', this.style.rightColor);
		this.widget.addClass('right');
		if (this.style.sympathy)
			this.widget.addClass('sympathy');
	}
	
	this.writerArea = $('#opnVwHeadDiv' + this.seq).addClass('writerArea').get(0);
	if (this.layerMode)
		this.widget.addClass('OpnViewerLayer');
	
	if (this.printMode) {
		$('#opnVwWrtRpy' + this.seq).remove();
	} else {
		this.hiddenLi = $('#opnVwWrtRpy' + this.seq);
		this.writer = new OpnWriter(this.hiddenLi.get(0), {'resetScroll':false, 'maxLength':this.maxLength, 'className':'OpnEditor', 'mentionUse':this.mentionUse});
	}
	var param = [];
	var dataList;
	var opnDataList;
	
	if (this.style.opnView) {
		this.opinionUrl = JSV.getModuleUrl('/jsl/' + this.style.opnView + '.json');
		var selectFilter = $('#opnVwFilter' + this.seq).children('.select_filter');
		selectFilter.click(function() {
			selectFilter.removeClass('selected');
			$(this).toggleClass('selected');
			OpnViewer.cancel();
			self.widget.find('#opnVwVsbUl' + self.seq).find('li').not('#opnVwWrtRpy' + self.seq).remove();
			if (self.more) {
				self.widget.find('#moreText' + self.seq).show();
				self.widget.find('#moreBar' + self.seq).show();
				self.widget.find('#moreBtn' + self.seq).css('cursor', 'pointer');
				self.moreBtnEvent = true;
			}
			if ($(this).is('#desc')) {
				self.desc = true;
			} else {
				self.desc = false;
			}
			param.push(self.itemIdName + '=' + self.itemIdVal);
			param.push('desc=' + self.desc);
			if (self.gs) {
				param.push('gs=' + self.gs);
			}
			dataList = JSV.loadJSON(self.opinionUrl, param.join('&'));
			opnDataList = dataList[self.dataName];
			self.array = opnDataList['array'];
			self.length = self.array.length;
			self.total = opnDataList.total;
			self.reloadCount();
			self.createOpinionCall(self.array);
			if (self.style.opnView && !self.more && self.total > self.length) {
				self.more = true;
				self.createAreaMore();
			}
			param = [];
		});
	}
	
	//새로고침
	var refreshBtn = $('#refreshBtn' + this.seq);
	var reloadUrl = this.reloadUrl;
	refreshBtn.click(function() {
		JSV.doGET(reloadUrl);
	});
	
}
OpnViewer.prototype.isEditable = function(element){
	var userId = element.user.id ? element.user.id : element.user.userId ? element.user.userId : null; 
	return (this.style.userId && this.style.userId == userId)|| element.currentOwner;
}
OpnViewer.prototype.isDeletable = function(element){
	var userId = element.user.id ? element.user.id : element.user.userId ? element.user.userId : null; 
	return (this.style.userId && this.style.userId == userId)|| this.isCenter || element.currentOwner;
}
OpnViewer.prototype.getValue = function() {
	return this.opnList;
}
OpnViewer.prototype.setValue = function(elements, comp) {
	this.component = comp;
	if (!this.itemIdVal) {
		if (comp) {
			this.itemIdVal = comp.getProperty(this.itemId);
		}
		if (!this.itemIdVal) {
			this.itemIdVal = JSV.getParameter(this.itemId);
		}
	}
	if (elements) {
		this.opnList = elements;
	}
	if (comp && comp.layout.form) {
		var layout = comp.layout.form;
	}
	if (this.useEditable && elements[1] && (typeof(eval(elements[1])) == 'boolean')) {
		this.writer.setEditable(eval(elements[1]));
		elements = elements[0];
	}
	this.total = elements.total || elements.totalRows || 0;
	if (this.total == 0) {
		$('#opnVwFilter' + this.seq).hide();
	}
	if (this.style.noTitle) {
		$('#opnVwCntMain' + this.seq).hide();
	} else {
		if (!this.appendRight) {
			var fold = !this.isFold;
			$('#opnVwCntA' + this.seq).on('click', this, function(e) {
				if (fold) {
					$('#opnVwContent' + e.data.seq).slideUp('fast');
					if(layout) $(layout).find('table.opnWrtTable').slideUp('fast');
					$('#opnVwCntArrow' + e.data.seq).removeClass('selected');
					fold = false;
				} else {
					$('#opnVwContent' + e.data.seq).slideDown('fast');
					if(layout) $(layout).find('table.opnWrtTable').slideDown('fast');
					$('#opnVwCntArrow' + e.data.seq).addClass('selected');
					fold = true;
				}
			});
			if (!fold) {
				$('#opnVwContent' + this.seq).hide();
				if(layout) $(layout).find('table.opnWrtTable').hide();
				$('#opnVwCntArrow' + this.seq).removeClass('selected');
			}
		}
		$('#opnVwCnt' + this.seq).text(this.total);
	}
	this.more = ((elements.rest || !isNaN(elements.rest)) && elements.rest > 0) ? true : false;
	if (this.more) {
		if (this.style.opnView) {
			this.createAreaMore();
		}
	}
	
	if (this.style.sympathy) {
		this.style.sympathy.style.seq = this.seq;
		this.style.sympathy.style.appendRight = this.appendRight;
		var symComp = this.createArea($('#opnVwContent' + this.seq).get(0), this.style.sympathy);
		JSV.register(symComp, this, 'updateSympathyCount');
	}
	
	this.array = elements.array;
	this.length = elements.array.length;
	this.createOpinionCall(this.array);
	
	if (OpnViewer.RIGHTFRAME) {	
		setTimeout(function() {
			OpnViewer.resetScroll();
		}, 500);
	}
}
OpnViewer.prototype.createArea = function(parent, obj) {
	var clazz = eval(obj.component);
	var style = obj.style;
	var value = this.component.getProperty(obj.reference);
	var _clazz = new clazz(parent, style);
	_clazz.setValue(value, this.component);
	
	return _clazz;
}
OpnViewer.prototype.createAreaMore = function() {
	$('<div class="moreVw" id="moreBtn' + this.seq + '">\
			<a class="btnMoreOp" id="moreText' + this.seq + '"><span>' + JSV.getLang('OpnViewer', 'seeMore') + '</span></a>\
			<span class="bar" id="moreBar' + this.seq + '"></span>\
			<a href="#opnVwHeadDiv' + this.seq + '" class="pageUp btnGoTopOp"><span>' + JSV.getLang('OpnViewer', 'topMove') + '</span></a>\
		</div>').appendTo(this.widget.find('#opnVwMoreBar' + this.seq).show());
	
	var moreBtnArea = this.widget.find('#moreBtn' + this.seq);
	
	moreBtnArea.find('.pageUp').on('click', this, function(e) {
		e.stopPropagation();
	});
	moreBtnArea.on('click', this, function(e) {
		if (e.data.moreBtnEvent) {
			var param = [];
			param.push(e.data.itemIdName + '=' + e.data.itemIdVal);
			param.push('desc=' + e.data.desc);
			param.push('opnLastId=' + e.data.lastId);
			if (e.data.gs) {
				param.push('gs=' + e.data.gs);
			}
			var dataList = JSV.loadJSON(e.data.opinionUrl, param.join('&'));
			var opnMoreList = dataList[e.data.dataName];
			e.data.array = opnMoreList['array'];
			e.data.length = e.data.array.length;
			e.data.total = opnMoreList.total;
			e.data.reloadCount();
			e.data.createOpinionCall(e.data.array);
			if (e.data.moreCnt > e.data.length) {
				$(this).find('#moreText' + e.data.seq).hide();
				$(this).find('#moreBar' + e.data.seq).hide();
				moreBtnArea.css('cursor', 'default');
				e.data.moreBtnEvent = false;
			}
		}
	});
}
OpnViewer.prototype.createOpinionCall = function(array) {
	var self = this;
	var i = 0;
	
	(function loop() {
		for (var j = 0; self.length && j < self.countPerOne; j++) {
			self.createOpinion(array[i++]).appendTo('#opnVwVsbUl' + self.seq);
			self.resetUnderLine();
			if (i >= self.length) {
				break;
			}
		}
		if (i < self.length) {
			setTimeout(loop, 0);
		} else if (self.hashOpnId){
			setTimeout(function() {
				if (document.getElementById(self.hashOpnId)) {
					if (self.isFix) {
						var headDiv = undefined;
						var prev = $('#' + self.hashOpnId).prev();
						
						if (prev.length && prev.css('display') != 'none') {
							headDiv = prev.get(0);
						} else if (document.getElementById('opnVwHeadDiv' + self.seq)) {
							headDiv = document.getElementById('opnVwHeadDiv' + self.seq);
						}

						if(headDiv !== undefined){
							window.scrollBy(0, headDiv.offsetTop);
						}
					} else {
						var opnElem = $('#' + self.hashOpnId);
						var pCls = opnElem.closest(self.closestSlt || 'div.TemplateLayoutRight');
						if (pCls.length) {
							var top = opnElem.offset().top - pCls.offset().top;
							pCls.scrollTop(top);
						} else {
							document.getElementById(self.hashOpnId).scrollIntoView();
						}
					}
				}
				self.hashOpnId = null;
			}, 200);
		}
	})();
}
OpnViewer.prototype.createOpinion = function(element) {
	var isGlobal = (this.style.isGlobal) ? this.style.isGlobal : null;
	var seq = JSV.SEQUENCE++;
	var li = $('<li>\
		<div class="inner_wrap">\
			<div class="thumb" id="opnVwThumb' + seq + '"></div>\
			<p class="cmt">\
				<span class="username" id="opnVwName' + seq + '"></span>\
				<span class="opnCon" id="opnVspan' + seq + '"></span>\
			</p>\
			<div class="cmt_bottom">\
				<div class="regtime" id="opnVwTime' + seq + '"></div>\
				<div class="cmt_fn" id="opnVwRpCtr' + seq + '"></div>\
			</div>\
		</div>\
	</li>').attr({'id':element.id,'gid':element.gid,'itemId':element.itemId,'seq':seq}).addClass('opnVwOpn').data('component', this);

	this.lastId = element.id;
	var content = element.content;
	if (content.indexOf('|') > -1) {
		var idx = content.indexOf('|');
		var tmpEtcn = content.substring(0, idx);
		if (OpnWriter.Emotions[tmpEtcn])
			content = content.substring(idx +1);
	}
	content = OpnViewer.contentDecode(content);

	var vwName = li.find('p.cmt > span.username');
	if (!this.layerMode && element.gid && element.id != element.gid) {
		$('<i>').addClass('i_reply').prependTo(li.find('div.inner_wrap'));
		li.addClass('opnReply');
	}
	if (this.vrtl != null && element.user.id == this.vrtl) {	
		vwName.html('<span class="bold">' + element.user.name);
	} else {
		if (isGlobal != null && isGlobal == 'true') {
			vwName.html(AnchorMbr.generateBoldAnchor(element.user.id, element.user.name));
		} else {
			vwName.append(AnchorEmp.generateBoldAnchor(element.user.id, element.user.name));
		}
	}
	$('<img>').attr('src', JSV.getContextPath('/jsl/inline/ImageAction.Download?cacheOpt=DAY&path=' + element.user.id + '&type=' + JSV.EMPTHUMB_TYPE))
	.one('error', function() {
		$(this).attr('src', JSV.getContextPath(EmpImageViewer.DEFAULT_IMAGE));
	}).appendTo(li.find('div.thumb'));

	var $opnCon = li.find('span.opnCon').data('opnCon', content);
	$opnCon.append(OpnViewer.contentToMentionHtmlNodes(content, function(mentionUsers) {
		$opnCon.data('mentionUsers', mentionUsers);
	}));

	OpnViewer.setTime(li.find('.regtime'), element.rgstDate);
	
	if (!this.printMode) {
		var rplyArea = li.find('div.cmt_bottom > div.cmt_fn'); 
		if (element.id == element.gid && this.style.actionAddUrl) {
			$('<a href="javascript:void(0);"></a>').addClass('fn_reply').attr('title', JSV.getLang('OpnViewer', 'replyIcon'))
			.text(JSV.getLang('OpnViewer', 'replyIcon')).appendTo(rplyArea)
			.click(function(e) {
				e.stopPropagation();
				if (OpnViewer.isEdit) {
					OpnViewer.save();
					return false;
				}
				OpnViewer.isReply ? OpnViewer.replySave(li) : OpnViewer.reply(li);
			});
		}
		if (this.style.opnUpdate && this.isEditable(element)) {
			$('<a href="javascript:void(0);"></a>').addClass('fn_edit').attr('title', JSV.getLang('OpnViewer', 'editIcon'))
			.text(JSV.getLang('OpnViewer', 'editIcon')).appendTo(rplyArea)
			.click(function(e) {
				e.stopPropagation();
				if (OpnViewer.isReply) {
					OpnViewer.replySave();
					return false;
				}
				OpnViewer.isEdit ? OpnViewer.save(li) : OpnViewer.edit(li);
			});
		}
		if (this.style.opnDelete && this.isDeletable(element)) {
			$('<a href="javascript:void(0);"></a>').addClass('fn_del').attr('title', JSV.getLang('OpnViewer', 'deleteIcon'))
			.text(JSV.getLang('OpnViewer', 'deleteIcon')).appendTo(rplyArea)
			.click(function(e) {
				JSV.confirm(JSV.getLang('OpnViewer', 'deleteConfirm'), function(res) {
					if (res) {
						OpnViewer.del(li);	
					}
				});
			});
		}
		if (this.hideButtons) {
			rplyArea.hide();
		}
	}
	return li;
}
OpnViewer.prototype.setButtonVisible = function(visible) {
	this.hideButtons = !visible;
}
OpnViewer.prototype.notify = function(value, observable) {
	var self = this;
	if (value == null) {
		return false;
	} else {
		if (this.opnList.array) {
			this.opnList.array.push(value);
			if (self.desc && observable.objName == 'OpnWriter') {
				var li = this.createOpinion(value);
				li.prependTo('#opnVwVsbUl' + this.seq);
				li.find('div.inner_wrap').hide().fadeIn(700).focus();
				this.updateCount(OpnViewer.ADD);
				this.resetUnderLine();
				observable.widget.find('#opnWrtBtn' + observable.seq).children().attr('href', '#' + observable.wtextarea.widget.get(0).id);
			} else if (self.style.opnView && !self.desc && observable.objName == 'OpnWriter') {
				$('#opnVwFilter' + self.seq).find('#desc').trigger('click');
			}
		}
	}
	if (observable.objName != 'OpnWriter') {
		var node = JSV.loadJSON('/jsl/FolderAction.NodeInfo.json?folderId=' + value.id);
		var isEditable = ((parseInt(node.flagCode) & 8) != 0);
		if (this.writer) {
			this.writer.setEditable(isEditable);
		}
	}
}
OpnViewer.prototype.updateSympathyCount = function(total) {
	JSV.notify(total, this, 'updateSympathyCount');
}
OpnViewer.prototype.updateCount = function(operator) {
	this.total = (operator) ? ++this.total : --this.total; 
	$('#opnVwCnt' + this.seq).text(this.total);
	if (this.total > 0) {
		$('#opnVwFilter' + this.seq).show();
	} else {
		$('#opnVwFilter' + this.seq).hide();
	}
	this.lastId = $('#opnVwVsbUl' + this.seq).find('li:visible :last').attr('id');
	JSV.notify(this.total, this, 'updateCount');
}
OpnViewer.prototype.reloadCount = function() {
	$('#opnVwCnt' + this.seq).text(this.total);
	if (this.total > 0) {
		$('#opnVwFilter' + this.seq).show();
	} else {
		$('#opnVwFilter' + this.seq).hide();
	}
	JSV.notify(this.total, this, 'updateCount');
}
OpnViewer.prototype.resetUnderLine = function() {
	$('#opnVwVsbUl' + this.seq).find('li.last-child').removeClass('last-child').end().find('li:visible :last').addClass('last-child');
}
OpnViewer.setTime = function(obj, time) {
	if (time) {
		if (typeof(time) != 'number') time = parseInt(time);
		obj.html(DateTextWriter.text(time, JSV.getLang('DateFormat','fullType1')));
	}
}
OpnViewer.edit = function(obj) {
	if (OpnViewer.isEdit) return;
	var comp = obj.data('component');
	var seq = obj.attr('seq');
	var writer = comp.writer;
	var wWidget = $(writer.widget); 
	var hiddenLi = comp.hiddenLi;
	obj.hide();
	hiddenLi.addClass('edit').insertAfter(obj).show();
	if(JSV.browser.msie6){
		ItemViewer.subShow(hiddenLi);
	}
	$(document).off('click.opnView').on('click.opnView', function(event) {
		OpnViewer.save();
	});
	hiddenLi.off('click').on('click', function(event) {
		event.stopPropagation();
	});
	var conSpan = $('#opnVspan' + seq).get(0);
	var textArea = $('#opnWrtText' + writer.seq);
	if (writer.editable) {
		$('#opnInpNick' + writer.seq).val($('#opnVwName' + seq).text()).prev('label').hide();
	}
	if (comp.mentionUse) {
		textArea.mentionsInput('setValue', OpnViewer.contentEncode($(conSpan).data('opnCon')));
	} else {
		textArea.val(OpnViewer.contentEncode($(conSpan).data('opnCon')));
	}
	
	var replaceValue = textArea.val().replace(eval("/<br>/gi"),'\r\n');
	textArea.val(replaceValue).keyup().focus();
	OpnViewer.compareValue = writer.getValue();
	OpnViewer.isEdit = true;
	OpnViewer.editor = obj;

	// writer onclick 재정의
	writer.onclick = function() {
		OpnViewer.save(OpnViewer.editor);
	}
}
OpnViewer.save = function(obj) {
	var chObj = OpnViewer.editor;
	var seq = chObj.attr('seq');
	var name = $('#opnVwName' + seq).text();
	var writer = chObj.data('component').writer;
	if (writer.getValue() != '' && OpnViewer.compareValue != writer.getValue() || (writer.editable && (TrimString($('#opnInpNick' + writer.seq).val()) != TrimString(name)))) {
			if (OpnViewer.editor != obj) {
				JSV.confirm(JSV.getLang('OpnViewer','cancelConfirm'), function(res) {
					if (res) {
						if (OpnViewer.isEmpty(writer.getValue())) {
							JSV.alert(JSV.getLang('OpnViewer','isEmpty'));
							return false;
						}
						if (writer.editable && OpnViewer.isEmpty($('#opnInpNick' + writer.seq).val())) {
							JSV.alert(JSV.getLang('OpnViewer','isNameEmpty'));
							return false;
						}
						OpnViewer.update(OpnViewer.editor);
						OpnViewer.cancel(OpnViewer.editor);
						return false;
					} else {
						OpnViewer.cancel(OpnViewer.editor);
						if (obj != null)
							OpnViewer.edit(obj);
						return false;	
					}
				});
			} else {
				if (OpnViewer.isEmpty(writer.getValue())) {
					JSV.alert(JSV.getLang('OpnViewer','isEmpty'));
					return false;
				}
				if (writer.editable && OpnViewer.isEmpty($('#opnInpNick' + writer.seq).val())) {
					JSV.alert(JSV.getLang('OpnViewer','isNameEmpty'));
					return false;
				}
				OpnViewer.update(OpnViewer.editor);
				OpnViewer.cancel(OpnViewer.editor);
				return false;
			}
	} else {
		OpnViewer.cancel(OpnViewer.editor);
		if (obj != null && OpnViewer.editor != obj)
			OpnViewer.edit(obj);		
	}
}
OpnViewer.update = function(obj) {
	var comp = obj.data('component');
	var seq = obj.attr('seq');
	var writer = comp.writer;
	var value = writer.getValue();
	var opnVspan = $('#opnVspan' + seq);
	if (comp.mentionUse) {
		var compareMentionUsers = opnVspan.data('mentionUsers');
		var currentMentionUsers = OpnWriter.removeDuplicateMention(writer.getMentionUsers());
		currentMentionUsers = OpnWriter.removeDiffMention(currentMentionUsers, compareMentionUsers);
		
		if (currentMentionUsers.length > 0) {
			value = value.concat(OpnWriter.mentionSeperator + JSV.toJSON(currentMentionUsers));
		}
	}
	var param = {'content':value, 'id':obj.attr('id')};
	if (writer.editable) {
		param.name = TrimString($('#opnInpNick' + writer.seq).val());
		param.vrtl = comp.vrtl;
	}
	$.ajax({'url':JSV.getModuleUrl(JSV.getContextPath('/jsl/' + comp.style.opnUpdate + '.json')),
			'dataType':'json',
			'type' : 'POST',
			'data':param,
			'success':function(data, status) {
						var content = OpnViewer.contentDecode(data.content);
						opnVspan.html('');
						opnVspan.data('opnCon', content);
						opnVspan.append(OpnViewer.contentToMentionHtmlNodes(content, function(mentionUsers){
						opnVspan.data('mentionUsers', mentionUsers);
						}));
						if (writer.editable) {
							$('#opnVwName' + seq).text(data.name);
						}
						OpnViewer.setTime($('#opnVwTime' + seq), data.rgstDate);
						
						for (i = 0; i < comp.opnList.array.length; i++) {
							if(comp.opnList.array[i].id == obj.attr('id')) {
								comp.opnList.array[i].content = data.content;
								comp.opnList.array[i].rgstDate = data.rgstDate;
								break;
							}
						}
					},
			'error':function(xhr) {
				JSV.alert(JSV.getLang('OpnViewer','updateError'));
			}
	});
}
OpnViewer.del = function(obj) {
	var comp = obj.data('component');
	var divs = $('#opnVwVsbUl' + comp.seq).find('li[gid=' + obj.attr('id') + ']');
	if (divs.length > 1) {
		JSV.alert(JSV.getLang('OpnViewer','delParentError'));
		return false;
	}
	var action = comp.style.opnDelete;
	var id = obj.attr('id');
	$.ajax({'url':JSV.getModuleUrl(JSV.getContextPath('/jsl/' + action + '.json')),
			'dataType':'html',
			'type' : 'POST',
			'data':{'id':id},
			'success':function(data, status) {
						if (data.indexOf('error') > -1) {
							JSV.alert(JSV.getLang('OpnViewer','delError'));
							return false;
						}
						comp.updateCount(OpnViewer.MINUS);
						obj.fadeOut(400);
						window.setTimeout(function() {
							JSV.finalize(obj.get(0));
							obj.remove();
							comp.resetUnderLine();
						}, 400);
						
						for (i = 0; i < comp.opnList.array.length; i++) {
							if(comp.opnList.array[i].id == obj.attr('id')) {
								comp.opnList.array.splice(i,1);
								break;
							}
						}
					},
			'error':function(xhr) {
				JSV.alert(JSV.getLang('OpnViewer','delParentError'));
			}
	});
}
OpnViewer.reply = function(obj) {
	if(OpnViewer.isReply) {
		return;
	}
	var comp = obj.data('component');
	var seq = obj.attr('seq');
	var writer = comp.writer;
	var wWidget = $(writer.widget); 
	var hiddenLi = comp.hiddenLi;
	hiddenLi.removeClass('edit').insertAfter(obj).show();
	if(JSV.browser.msie6) {
		ItemViewer.subShow(hiddenLi);
	}
	comp.resetUnderLine();

	$(document).off('click.opnView').on('click.opnView', function(event){
		OpnViewer.replySave();
		comp.resetUnderLine();
	});
	hiddenLi.off('click').on('click', function(event){
		event.stopPropagation();
	});

	var conSpan = $('#opnVspan' + seq).get(0);
	var textArea = $('#opnWrtText' + writer.seq);
	if (writer.editable) {
		$('#opnInpNick' + writer.seq).val('').trigger('blur');
	}
	
	if (comp.mentionUse) {
		textArea.mentionsInput('setValue', '');
	} else {
		textArea.val('');
	}
	textArea.keyup().focus();

	OpnViewer.isReply = true;
	OpnViewer.editor = obj;

	// writer onclick 재정의
	writer.onclick = function() {
		writer.widget.find('#opnWrtBtn' + writer.seq).children().attr('href', '#' + obj.get(0).id);
		OpnViewer.replySave(OpnViewer.editor);
	}
	
}
OpnViewer.replySave = function(obj) {
	var chObj = OpnViewer.editor; 
	var seq = chObj.attr('seq');
	var writer = chObj.data('component').writer;

	if (!OpnViewer.isEmpty(writer.getValue()) || (writer.editable && !OpnViewer.isEmpty($('#opnInpNick' + writer.seq).val()))) {
		if(OpnViewer.editor != obj) {
			JSV.confirm(JSV.getLang('OpnViewer','replyConfirm'), function(res) {
				if (res) {
					if(OpnViewer.isEmpty(writer.getValue())) {
						JSV.alert(JSV.getLang('OpnViewer','isEmpty'));
						return false;
					}
					if(writer.editable && OpnViewer.isEmpty($('#opnInpNick' + writer.seq).val())) {
						JSV.alert(JSV.getLang('OpnViewer','isNameEmpty'));
						return false;
					}
					OpnViewer.replyUpdate(OpnViewer.editor);
					OpnViewer.cancel();
					return false;
				} else {
					OpnViewer.cancel();
					if (obj != null)
						OpnViewer.reply(obj);
					return false;
				}
			});
		} else {
			if(OpnViewer.isEmpty(writer.getValue())) {
				JSV.alert(JSV.getLang('OpnViewer','isEmpty'));
				return false;
			}
			if(writer.editable && OpnViewer.isEmpty($('#opnInpNick' + writer.seq).val())) {
				JSV.alert(JSV.getLang('OpnViewer','isNameEmpty'));
				return false;
			}
			OpnViewer.replyUpdate(OpnViewer.editor);
			OpnViewer.cancel();
			return false;			
		}
	} else {
		OpnViewer.cancel();
		if(obj != null && OpnViewer.editor != obj)
			OpnViewer.reply(obj);
	}
}
OpnViewer.replyUpdate = function(obj) {
	var comp = obj.data('component');
	var action = JSV.getModuleUrl(comp.style.actionAddUrl);
	var gid = obj.attr('id');
	var itemId = obj.attr('itemId');
	var writer = comp.writer;
	var value = writer.getValue();
	if (comp.mentionUse) {
		var currentMentionUsers = OpnWriter.removeDuplicateMention(writer.getMentionUsers());
		if (currentMentionUsers.length > 0) {
			value = value.concat(OpnWriter.mentionSeperator + JSV.toJSON(currentMentionUsers));
		}
	}
	var param = {'gid':gid, 'itemId':itemId, 'content':value};
	if (writer.editable) {
		var name = TrimString($('#opnInpNick' + writer.seq).val());
		param.user = {'id':comp.vrtl, 'name':name, 'displayName':name};
	}
	$.ajax({'url':JSV.getContextPath(action),
			'dataType':'json',
			'type' : 'POST',
			'data':{'opn':JSV.toJSON(param), 'gid':gid, 'itemid':itemId, 'content':value},
			'success':function(data, status) {
						var li = comp.createOpinion(data);
						var last = $('#opnVwVsbUl' + comp.seq).find('li[gid=' + gid + ']:last');
						li.insertAfter(last).find('div.inner_wrap').hide().fadeIn(400).focus();
						comp.updateCount(OpnViewer.ADD);
						
						var isRply = false;
						for (i = 0; i < comp.opnList.array.length + 1; i++) {
							if (comp.opnList.array[i] && comp.opnList.array[i].id == gid) {
								isRply = true;
							} else if (comp.opnList.array[i] && comp.opnList.array[i].gid != gid && isRply) {
								comp.opnList.array.splice(i,0,data);
								break;
							} else if (!comp.opnList.array[i] && isRply) {
								comp.opnList.array.push(data);
								break;
							}
						}
					},
			'error':function(xhr) {
				JSV.alert(JSV.getLang('OpnViewer','reError'));
			}
	});
}
OpnViewer.cancel = function(obj) {
	if (!(OpnViewer.isEdit || OpnViewer.isReply)) {
		return false;
	}
	OpnViewer.isEdit = false;
	OpnViewer.isReply = false;
	$(document).off('click.opnView');
	if (obj != null) {
		var hiddenLi = obj.data('component').hiddenLi;
		hiddenLi.hide();
		if(JSV.browser.msie6) {
			ItemViewer.subHide(hiddenLi);
		}
		obj.show();
		OpnViewer.compareValue = null;
	} else {
		var hiddenLi = OpnViewer.editor.data('component').hiddenLi;
		hiddenLi.hide();
		if(JSV.browser.msie6) {
			ItemViewer.subHide(hiddenLi);
		}
	}
}
OpnViewer.isEmpty = function(value) {
	return $('<span>').html(OpnViewer.contentDecode(value)).text() == '';
}
OpnViewer.showPopup = function(style) {
	this.style = style ? style : {};
	var id = this.style.id;
	var gs = this.style.gs;
	var mp = this.style.mp;
	var inAppId = this.style.inAppId;
	var isGlobal = this.style.isGlobal;
	var isSympathy = this.style.isSympathy || true;
	var isAnony = this.style.isAnony || false;
	var itemIdName = this.style.itemId || 'id';
	var catalogUrl = this.style.catalogUrl;
	var readProperty = this.style.readProperty || 'opinions.read';
	var writeProperty = this.style.writeProperty || 'opinions.write';
	var targetUrl = this.style.targetUrl;
	var feature = 'menubar=no,toolbar=no,location=no,scrollbars=no,resizable=yes';
	var url = '/sys/jsv/doc/OpnPopup.jsp?id=' + id + '&itemIdName=' + itemIdName + '&readProperty=' + readProperty + '&writeProperty=' + writeProperty;
	url += '&catalogUrl=' + catalogUrl + '&opnView=' + this.style.opnView;
	url += '&isSympathy=' + isSympathy;
	url += '&isAnony=' + isAnony;
	url += '&code=100';
	if (gs) {
		url += '&gs=' + gs;
	}
	if (isGlobal) {
		url += '&isGlobal=' + isGlobal;
	}
	if (inAppId) {
		url += '&inAppId=' + inAppId;
	}
	if (mp) {
		url += '&' + mp;
	} else {
		url = JSV.getModuleUrl(url, this.style.portletId || null, true);
	}
	if (targetUrl) {
		url += '&targetUrl=' + targetUrl;
	}
	if (this.style.isGroup == 'true') {
		OpenWindowCenter.normal(690, 610, '', 'OpnListViewer', feature);
		JSV.doGET(url, 'OpnListViewer');
	} else {
		OpenWindowCenter.normal(690, 610, JSV.getContextPath(url), 'OpnListViewer', feature);
	}
	return;
}
OpnViewer.contentDecode = function(value) {
	if(value != null) {
		value = value.replace(/<br>/gi, '\n').replace(/[<]/gi, '&lt;').replace(/[>]/gi, '&gt;').replace(/\n/gi, '<br>');
	}
	return value;
}
OpnViewer.contentEncode = function(value) {
	if(value != null) {
		value = value.replace(/<br>/gi, '\n').replace(/&lt;/gi, '<').replace(/&gt;/gi, '>');
	}
	return value;
}
OpnViewer.resetScroll = function(writer) {
	if (OpnViewer.RIGHTFRAME) {
		OpnViewer.RIGHTFRAME.scrollTop = OpnViewer.RIGHTFRAME.scrollHeight;
	} else if (writer) {
		var $window = $(window); 
		var top = writer.offset().top;
		var wTop = $window.scrollTop();
		var wHeight = $window.height();
		var cY = top - wTop;
		if (cY + 110 > wHeight) {
			$(window).scrollTop(wTop + ((cY + 110) - wHeight));
		}
	}
}
OpnViewer.contentToMentionHtmlNodes  = function(content, callback) {
	var $tempDiv = $('<div>');
	var mentionRex = /@\[([^\]]+)\]\(([^ \)]+)\)/g;
	var match = mentionRex.exec(content);
	var mentionUsers = new Array();
	while(match) {
		var start = content.substr(0, match.index);
		var mentionSpan = $('<span>').addClass('mentionUser').attr('uid', match[2]).text(match[1]).get(0).outerHTML;
		var end = content.substr(match.index + match[0].length, content.length);
		content = start + mentionSpan + end;
		
		var mentionUser = new Object();
		mentionUser.id = match[2];
		mentionUser.name = match[1];
		mentionUsers.push(mentionUser);
		
		match = mentionRex.exec(content);
	}
	if (callback && $.isFunction(callback)) {
		mentionUsers = OpnWriter.removeDuplicateMention(mentionUsers);
		callback(mentionUsers);
	}
	$tempDiv.html(content);;
	$tempDiv.find('span.mentionUser').each(function() {
		  var $this =  $( this );
		  var anchorMention = AnchorEmp.generateAnchor($this.attr('uid'), $this.text());
		  $this.after(anchorMention);
		  $this.remove();
	});
	return $tempDiv.get(0).childNodes;
}
OpnViewer.isReply = false;
OpnViewer.isEdit = false;
OpnViewer.editor = null;
OpnViewer.compareValue = null;
OpnViewer.ADD = true;
OpnViewer.MINUS = false;
OpnViewer.RIGHTFRAME = null;
