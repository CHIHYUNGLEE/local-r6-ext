<%@page import="com.kcube.cst.kcube.CheckKMSDBFile"%>
<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<%
CheckKMSDBFile obj= new CheckKMSDBFile();  

/*해야할 테이블.
obj.start("GP_ITEM_FILE"); // 56380
*/


/*컬럼이름이 달라서 맨나중에...
obj.start("WB_MODULE_FILE"); //109
obj.start("CA_EVENT_FILE"); //50
obj.start("SP_PTL_FILE"); //28
obj.start("PJ_ITEM_FILE"); //15
obj.start("GP_COP_FILE"); //7
obj.start("FR_FILE"); //6
obj.start("DM_ORDER_FILE"); //2
obj.start("JIRA_PLAN_REPORT_FILE"); //1
obj.start("PJ_ITEM_MOD_FILE"); //1
obj.start("PJ_ITEM_TASK_FILE"); //1
*/

obj.close();

/*완료
obj.start("TASK_PLAN_FILE"); // 9921
obj.start("BD_ITEM_FILE"); //5161
obj.start("KM_ITEM_FILE"); //4891
obj.start("TASK_ITEM_FILE"); //2226
obj.start("TK_ITEM_FILE"); //520
obj.start("AL_ITEM_FILE"); //207
obj.start("WEBZINE_ITEM_FILE"); //197
obj.start("CHAT_ITEM_FILE"); //169
obj.start("WIKIBOOK_ITEM_FILE"); //63
obj.start("SOP_BD_ITEM_FILE"); //55
obj.start("QNA_ITEM_FILE"); //51
obj.start("GUIDE_FILE"); //46
obj.start("FR_ITEM_FILE"); //41
obj.start("WIKI_ITEM_FILE"); //35
obj.start("BLDR_ITEM_FILE"); //22
obj.start("ST_QUERY_FILE"); //21
obj.start("BOOK_ITEM_FILE"); //15
obj.start("SUP_ORG_FILE"); //12
obj.start("ENQT_FILE"); //11
obj.start("PN_ITEM_FILE"); //10
obj.start("BM_ITEM_FILE"); //10
obj.start("MI_ITEM_FILE"); //9
obj.start("SW_ITEM_FILE"); //7
obj.start("JIRA_PLAN_FILE"); // 5
obj.start("SOP_ITEM_FILE"); //5
obj.start("GP_FR_ITEM_FILE"); //4
obj.start("EDU_ITEM_FILE"); //3
obj.start("WORK_ITEM_FILE"); //2
obj.start("PJTEVAL_ITEM_FILE"); //2
obj.start("DP_ITEM_FILE"); //2
obj.start("BT_ITEM_FILE"); //2
obj.start("HN_MEETING_FILE"); //2
obj.start("WEBZINE_NOTICE_FILE"); //1
obj.start("JIRA_ITEM_FILE"); //1
obj.start("GUIDE_RELATE_FILE"); //1
*/

/*진행안함.
// obj.start("SUP_ITEM_FILE"); // 13848
// obj.start("LICN_ITEM_FILE"); //532
*/
%>