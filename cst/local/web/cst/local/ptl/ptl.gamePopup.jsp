<%@ include file="/sys/jsv/template/template.head.jsp" %>
<style>
#comArea${PORTLET_ID}{
	float:left;
	height : 84%;
	width : 50%;
	font-size: 30px;
}
#userArea${PORTLET_ID}{
	float:right;
	height : 84%;
	width : 50%;
	font-size: 30px;
}
#notiArea${PORTLET_ID}{
	height : 8%;
	width : 100%;
	font-weight : bold;
	font-size : 30px;
	text-align:center;
}

#countArea${PORTLET_ID}{
	width : 140px;
	height : 0.7%;
	font-size : 20px;
}

#scoreArea${PORTLET_ID}{
	width : 140px;
	height : 4%;
	font-size : 20px;
}

.nextBtn${PORTLET_ID}{
	visibility : hidden;
	float:right;
	height : 5%;
	width : 45px;
	background-color: aqua;
	font-size : 20px;
}

.notiBtn${PORTLET_ID}{
	float:right;
	height : 5%;
	width : 45px;
	background-color: yellow;
	font-size : 20px;
}

</style>
<script>
JSV.Block(function (){
	
	$(document).ready(function() {
		keydown = false;
    	reset();
    	console.log(user);
    	if(user != null && user != 0 ){
    		$('#notiArea${PORTLET_ID}').text('<fmt:message key="cstm.rsp.033"/>');
    	}
	});

	
	$('#notiArea${PORTLET_ID}').text('<fmt:message key="cstm.rsp.033"/>');
	
	var keydown = false;
	document.addEventListener('keydown', (keyevent) => {
		if(keydown || !stop(keyevent.keyCode)) {
			return ;
		}else{
			keydown = true;
			
			//$('#notiArea${PORTLET_ID}').text('<fmt:message key="cstm.rsp.034"/>');//시작
			
			//유저가 고른후 컴퓨터 고르기 시작.
			let func1 = new Promise((resolve, reject) => {
				setTimeout(()=>{
					var usr = userChs(keyevent);	
					resolve(usr);
				}, 1000);
			})
			
			//가위바위보 보여준후 컴퓨터 결과리턴
			func1
			.then(function func2(usr) {
			    return new Promise((resolve, reject) => {
			        setTimeout(() => {
			        	$('#notiArea${PORTLET_ID}').text('<fmt:message key="cstm.rsp.038"/>!!!');
			        	resolve(usr);
			        }, 1000);
			    })
			})
			.then(function func3(usr) {
			    return new Promise((resolve, reject) => {
			        setTimeout(() => {
			        	$('#notiArea${PORTLET_ID}').text('<fmt:message key="cstm.rsp.038"/> 3!!!');
			        	//$('#com${PORTLET_ID}').attr('src',JSV.getContextPath(scissorsimgurl));
			        	$('#com${PORTLET_ID}').attr('src',JSV.getContextPath(threeimgurl));
			        	resolve(usr);
			        }, 1000);
			    })
			})
			.then(function func4(usr) {
			    return new Promise((resolve, reject) => {
			        setTimeout(() => {
			        	$('#notiArea${PORTLET_ID}').text('<fmt:message key="cstm.rsp.038"/> 2!!!');
			        	//$('#com${PORTLET_ID}').attr('src',JSV.getContextPath(rockimgurl));
			        	$('#com${PORTLET_ID}').attr('src',JSV.getContextPath(twoimgurl));
			        	resolve(usr);
			        }, 1000);
			    })
			})
			.then(function func5(usr) {
			    return new Promise((resolve, reject) => {
			        setTimeout(() => {
			        	$('#notiArea${PORTLET_ID}').text('<fmt:message key="cstm.rsp.038"/> 1!!!');
			        	//$('#com${PORTLET_ID}').attr('src',JSV.getContextPath(paperimgurl));
			        	$('#com${PORTLET_ID}').attr('src',JSV.getContextPath(oneimgurl));
			        	resolve(usr);
			        }, 1000);
			    })
			}) 
			.then(function func6(usr) {
		        setTimeout(() => {
		        	if(percentage()){
		        		var com = comchsfunc();
		        		result(usr , com);
		        	}else{
		        		var comper = comchsperfunc(usr);
		        		result(usr , comper);
		        	}
		        }, 1000);
			})
		} 
	});
	
    $('.notiBtn${PORTLET_ID}').click (function(){
    	keydown = false;
    	reset();
    });
    
    $('.nextBtn${PORTLET_ID}').click (function(){
    	keydown = false;
    	next();
    });
    
}, '${PORTLET_ID}');

var rockimgurl = 'img/rock.png';
var scissorsimgurl = 'img/scissors.png';
var paperimgurl = 'img/paper.png';
var oneimgurl = 'img/1.jpg';
var twoimgurl = 'img/2.jpg';
var threeimgurl = 'img/3.jpg';
var score = 0;
var count = 0;

var rock = null;
var scissors = null;
var paper = null;
var user = 100;//유저가 이길 확률 %. 소수 두번째자리까지 입력가능. 마이너스로 넣으면 0프로. 100프로 넘어가면 100프로. 순수 랜덤으로 할 경우 null로 두면 됨.

function percentage(){
	//유저가 이길확률 적용 안 할때 true
	//유저가 이길확률 적용 할때 false
	return (user == null) ? true : false;
}

//지면 -1 이기면 +1 비기면 0

//다른 키보드 차단
function stop(keyCode){
	return (keyCode == 37 || keyCode == 38 || keyCode == 39) ? true : false;
}

//사용자 가위바위보 선택 리턴
function userChs(keyevent){
	if(keyevent.keyCode == 37){ //ArrowLeft 가위
		$('#user${PORTLET_ID}').attr('src',JSV.getContextPath(scissorsimgurl));
		return 1;
	}
	
	if(keyevent.keyCode == 38){ //ArrowUp 바위
		$('#user${PORTLET_ID}').attr('src',JSV.getContextPath(rockimgurl));
		return 2;
	}
	
	if(keyevent.keyCode == 39){ //ArrowRight 보
		$('#user${PORTLET_ID}').attr('src',JSV.getContextPath(paperimgurl));
		return 3;
	}
}

//컴퓨터 가위바위보 리턴
function comchsfunc(){
	var comchs = Math.floor(Math.random() * (3 - 1 + 1)) + 1;
	if(comchs == 1){
		$('#com${PORTLET_ID}').attr('src',JSV.getContextPath(scissorsimgurl));
	}
	if(comchs == 2){
		$('#com${PORTLET_ID}').attr('src',JSV.getContextPath(rockimgurl));		
	}
	if(comchs == 3){
		$('#com${PORTLET_ID}').attr('src',JSV.getContextPath(paperimgurl));
	}
	return comchs;
}

function comchsperfunc(userKey){
	var comchsper = Math.floor(Math.random()*10000)/100;
	var comchs = null;
	if(user < 0){
		user = 0;
	}else if(user > 100){
		user = 100;
	}
	
	if(comchsper <= user){
		if(userKey == 1){
			comchs = 3;
		}
		if(userKey == 2){
			comchs = 1;
		}
		if(userKey == 3){
			comchs = 2;
		}
	}else{
		if(userKey == 1){
			comchs = 2;
		}
		if(userKey == 2){
			comchs = 3;
		}
		if(userKey == 3){
			comchs = 1;
		}
	}
	if(comchs == 1){//1 가위
		$('#com${PORTLET_ID}').attr('src',JSV.getContextPath(scissorsimgurl));
	}
	if(comchs == 2){//2 바위
		$('#com${PORTLET_ID}').attr('src',JSV.getContextPath(rockimgurl));		
	}
	if(comchs == 3){//3 보
		$('#com${PORTLET_ID}').attr('src',JSV.getContextPath(paperimgurl));
	}
	return comchs;
}


//결과 리턴
function result(user , com){
	if(user == 1 && com == 2){ // lose
		$('#notiArea${PORTLET_ID}').text('<fmt:message key="cstm.rsp.037"/>').append('<br><fmt:message key="cstm.rsp.039"/>');
		scoreMinus();
	}
	if(user == 1 && com == 3){ // win
		$('#notiArea${PORTLET_ID}').text('<fmt:message key="cstm.rsp.035"/>').append('<br><fmt:message key="cstm.rsp.039"/>');
		scorePlus();	
	}
	if(user == 2 && com == 3){ // lose
		$('#notiArea${PORTLET_ID}').text('<fmt:message key="cstm.rsp.037"/>').append('<br><fmt:message key="cstm.rsp.039"/>');
		scoreMinus();
	}
	if(user == 2 && com == 1){ // win
		$('#notiArea${PORTLET_ID}').text('<fmt:message key="cstm.rsp.035"/>').append('<br><fmt:message key="cstm.rsp.039"/>');
		scorePlus();	
	}
	if(user == 3 && com == 1){ // lose
		$('#notiArea${PORTLET_ID}').text('<fmt:message key="cstm.rsp.037"/>').append('<br><fmt:message key="cstm.rsp.039"/>');
		scoreMinus();
	}
	if(user == 3 && com == 2){ // win
		$('#notiArea${PORTLET_ID}').text('<fmt:message key="cstm.rsp.035"/>').append('<br><fmt:message key="cstm.rsp.039"/>');
		scorePlus();
	}
	if(user == com){ //비김
		$('#notiArea${PORTLET_ID}').text('<fmt:message key="cstm.rsp.036"/>').append('<br><fmt:message key="cstm.rsp.039"/>');
	}
	$('.nextBtn${PORTLET_ID}').css('visibility' , 'visible');
	countPlus();
	return ;
}

//초기리셋
function reset(){
	$('#user${PORTLET_ID}').attr('src',"");
	$('#com${PORTLET_ID}').attr('src',"");
	$('#notiArea${PORTLET_ID}').text('<fmt:message key="cstm.rsp.033"/>');
	$('#score${PORTLET_ID}').text(0);
	$('#count${PORTLET_ID}').text(0);
	score = 0;
	count = 0;
	$('.nextBtn${PORTLET_ID}').css('visibility' , 'hidden');
}

//다음버튼 클릭시 리셋
function next(){
	$('#user${PORTLET_ID}').attr('src',"");
	$('#com${PORTLET_ID}').attr('src',"");
	$('#notiArea${PORTLET_ID}').text('<fmt:message key="cstm.rsp.033"/>');
}

//점수 더하기
function scorePlus(){
	score += 1;
	$('#score${PORTLET_ID}').text(score);
}

//점수 빼기
function scoreMinus(){
	if(score > 0){
		score -= 1;
		$('#score${PORTLET_ID}').text(score);
		console.log(score);
		console.log(count);
		if(score = 0 && count > 0){
			$('.nextBtn${PORTLET_ID}').css('visibility' , 'hidden');
			$('#notiArea${PORTLET_ID}').text('<fmt:message key="cstm.rsp.042"/>');
		} 
	}
	
}

//횟수 더하기
function countPlus(){
	count += 1;
	$('#count${PORTLET_ID}').text(count);
}

</script>
<%@ include file="/sys/jsv/template/template.body.jsp" %>
<div id="comArea${PORTLET_ID}" class="comArea${PORTLET_ID}"><strong>computer</strong><hr><img class="com${PORTLET_ID}" id="com${PORTLET_ID}"></div>
<div id="userArea${PORTLET_ID}" class="userArea${PORTLET_ID}"><strong>me</strong>&nbsp;<hr><img class="user${PORTLET_ID}" id="user${PORTLET_ID}"></div>
<hr>
<div id="notiArea${PORTLET_ID}" class="notiArea${PORTLET_ID}"></div>
<button class="notiBtn${PORTLET_ID}">reset</button>
<button class="nextBtn${PORTLET_ID}"><fmt:message key="cstm.rsp.040"/></button>

<div id="scoreArea${PORTLET_ID}">score : <span id="score${PORTLET_ID}"></span></div>
<div id="countArea${PORTLET_ID}"><fmt:message key="cstm.rsp.041"/> : <span id="count${PORTLET_ID}"></span></div>
<%@ include file="/sys/jsv/template/template.tail.jsp" %>