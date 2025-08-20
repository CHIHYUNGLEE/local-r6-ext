WHENEVER SQLERROR EXIT FAILURE ;
/*
 * 업무가이드 
 */
CREATE TABLE GUIDE 
(
    CLASSID			      NUMBER (8), --FK (WB_CLASS.CLASSID)                    
    MODULEID		      NUMBER (8), --FK (WB_MODULE.MODULEID)                  
    SPID			      NUMBER (8),                                            
    APPID			      NUMBER (8),                                            
    TENANTID		      NUMBER (4), --FK (TENANT.TENANTID)                     
    ITEMID			      NUMBER (8) NOT NULL, --PK                              
    RGST_DATE		      DATE DEFAULT SYSDATE,                                  
    LAST_UPDT		      DATE DEFAULT SYSDATE,                                  
    STATUS                NUMBER (4) DEFAULT - 1 NOT NULL,                       
    ISVISB                NUMBER (1) DEFAULT 0 NOT NULL,                         
    TITLE                 VARCHAR2 (1000),                                       
    CHARGE_USERID         NUMBER (8),                                            
    CHARGE_NAME           VARCHAR2 (1000),                                       
    CHARGE_DISP           VARCHAR2 (1000),                                       
    AUTH_USERID           NUMBER (8),                                            
    AUTH_NAME             VARCHAR2 (1000),                                       
    AUTH_DISP             VARCHAR2 (1000),                                       
    RGST_USERID           NUMBER (8),                                            
    RGST_NAME             VARCHAR2 (1000),                                       
    RGST_DISP             VARCHAR2 (1000),                                       
    SCRT_LEVEL		      NUMBER (4),                                        
    READ_CNT		      NUMBER (8) DEFAULT 0 NOT NULL,                   
    OPN_CNT		      	  NUMBER (8) DEFAULT 0 NOT NULL,                   
    GID			 	      NUMBER (8),                                      
    VRSN_NUM              NUMBER (8) DEFAULT 1 NOT NULL,                         
    VRSN_USERID           NUMBER (8),                                            
    VRSN_NAME             VARCHAR2 (1000),                                       
    VRSN_DISP             VARCHAR2 (1000),                                       
    INST_DATE             DATE,                                                  
    DELT_DATE             DATE,                                                  
    TABLE_CONTENT         CLOB,                                                  
    CONTENT			      CLOB,                                            
    UPDATE_CONTENT        VARCHAR2 (4000),                                       
    RELATE_CONTENT        CLOB,                                                  
    FILE_CONTENT          CLOB,                                                  
    KNOW_CONTENT          CLOB,                                                  
    SORT                  NUMBER(4),                                             
    CSTM_FIELD1		      VARCHAR2 (4000),                                   
    CSTM_FIELD2		      VARCHAR2 (4000),                                   
    CSTM_FIELD3		      VARCHAR2 (4000),                                   
    CSTM_FIELD4		      VARCHAR2 (4000),                                   
    CSTM_FIELD5		      VARCHAR2 (4000),                                   
    CSTM_FIELD6		      VARCHAR2 (4000),
    KMID                  NUMBER (8),
    FIST_DATE			  DATE DEFAULT SYSDATE,
    KM_SORT 			  NUMBER (8),
    FLAG_CODE			  NUMBER (16) DEFAULT 0 NOT NULL,
   	QNA_CNT		      	  NUMBER (8) DEFAULT 0 NOT NULL,
	FAQ_CNT		      	  NUMBER (8) DEFAULT 0 NOT NULL,
	ISLASTVERSION		  NUMBER (1) DEFAULT 0 NOT NULL,
	ISLASTMAJOR     	  NUMBER (1) DEFAULT 0 NOT NULL,
	ISMAJORVERSION  	  NUMBER (1) DEFAULT 0 NOT NULL,
	START_DATE      	  DATE,
	END_DATE       	 	  DATE,
	APPR_CODE 			  NUMBER (4) DEFAULT 0 NOT NULL,
	PRCS_CODE		   	  NUMBER (4) DEFAULT 0 NOT NULL,
	VERSION_LABEL   	  VARCHAR2 (10),
	LAST_REQID 			  NUMBER(8),
	CHARGE_CNT 			  NUMBER(8),
	REWRITE_DATE 		  DATE DEFAULT SYSDATE,
	EDITID 				  NUMBER (8),
	TEMP_CONTENT          VARCHAR2 (4000)
)
TABLESPACE R6_DAT
LOGGING ;


/**
 * 업무가이드 맵
 */
CREATE TABLE GUIDE_MAP 
(
    ITEMID           NUMBER (8) NOT NULL, --FK (GUIDE.ITEMID)
    KMID             NUMBER (8),
    LEVEL1           NUMBER (8),
    LEVEL2           NUMBER (8),
    LEVEL3           NUMBER (8),
    LEVEL4           NUMBER (8),
    SEQ_ORDER        NUMBER (8) DEFAULT 0 NOT NULL,
    ISVISB           NUMBER (1) DEFAULT 0 NOT NULL,
    ISCOMP           NUMBER (1) DEFAULT 0 NOT NULL
)
TABLESPACE R6_DAT
LOGGING ;


/**
 * 업무가이드 공통콘텐츠
 */
CREATE TABLE GUIDE_RELATE 
(
    RELTID 			NUMBER (8) NOT NULL, --PK        
    ITEMID 			NUMBER (8), --FK (GUIDE.ITEMID)  
    TITLE           VARCHAR2 (1000),                 
    TRNS_SRC        VARCHAR2 (64),                   
    TRNS_KEY        VARCHAR2 (64),                  
    URL             VARCHAR2 (2000),                
    EID             VARCHAR2 (2000),                
    USERID          NUMBER (8) NULL,            
    USER_NAME       VARCHAR2 (1000),                
    RFRN_QUERY      VARCHAR2 (500),                 
    CSTM_FIELD1		VARCHAR2 (4000),             
    CSTM_FIELD2		VARCHAR2 (4000),             
    CSTM_FIELD3		VARCHAR2 (4000),             
    STATUS          NUMBER (4) DEFAULT - 1 NOT NULL  ,
    CONTENT 		CLOB,
    USER_DISP 		VARCHAR2(1000),
    RGST_DATE 		DATE DEFAULT SYSDATE,
    LAST_UPDT 		DATE DEFAULT SYSDATE,
    CLASSID 	   NUMBER(8),
	MODUlEID 	   NUMBER(8),
	SPID 		   NUMBER(8),
	APPID 		   NUMBER(8)
)
TABLESPACE R6_DAT
LOGGING ;




/**
 * 업무가이드 조회로그
 */
CREATE TABLE GUIDE_READ
(
    ITEMID 			NUMBER (8) NOT NULL, --FK (GUIDE.ITEMID)
    USERID    		NUMBER (8) NOT NULL,                        
    USER_NAME 		VARCHAR2 (1000) ,                                   
    USER_DISP 		VARCHAR2 (1000) ,                               
    CMD_CODE  		NUMBER (8) DEFAULT -1 NOT NULL ,                    
    INST_DATE 		DATE DEFAULT SYSDATE NOT NULL ,                 
    STAT_FLAG 		NUMBER (1) DEFAULT 0 NOT NULL                   
)
TABLESPACE R6_DAT
LOGGING ;


/**
 * 업무가이드 보안
 */
CREATE TABLE GUIDE_SCRT
(
    SEQ_ORDER 		NUMBER (8) DEFAULT 0 NOT NULL , 
    ITEMID    		NUMBER (8) NOT NULL ,	--FK (GUIDE.ITEMID)
    TITLE     		VARCHAR2 (1000) ,
    ISVISB    		NUMBER (1) DEFAULT 0 NOT NULL ,
    ISCOMP    		NUMBER (1) DEFAULT 0 NOT NULL ,
    XID       		NUMBER (17)
)
TABLESPACE R6_DAT
LOGGING ;



/**
 * 업무가이드 관련지식
 */
CREATE TABLE GUIDE_RFRN 
(
    RFRNID           NUMBER (8) NOT NULL,	--PK
    ITEMID           NUMBER (8) NOT NULL,	--FK (GUIDE.ITEMID)
    TITLE            VARCHAR2 (1000),
    USERID           NUMBER (8),
    USER_NAME        VARCHAR2 (1000),
    RFRN_QUERY       VARCHAR2 (500),
    MODULEID		 NUMBER (8), --FK (WB_MODULE.MODULEID)
    CLASSID			 NUMBER (8), --FK (WB_CLASS.CLASSID)
    SPID             NUMBER (8),
    APPID            NUMBER (8),
    EXTR_APPID 		 NUMBER (8)
)
TABLESPACE R6_DAT
LOGGING ;


/**
 * 업무가이드 의견
 */
CREATE TABLE GUIDE_OPN
(
    OPNID            NUMBER (8) NOT NULL,	--PK
    GID              NUMBER (8) NOT NULL,
    ITEMID           NUMBER (8) NOT NULL,	--FK (GUIDE.ITEMID)
    USERID           NUMBER (8),
    USER_NAME        VARCHAR2 (1000),
    USER_DISP        VARCHAR2 (1000),
    OPN_CODE         NUMBER (4) DEFAULT 0 NOT NULL,
    RGST_DATE        DATE DEFAULT SYSDATE,
    CONTENT          VARCHAR2 (4000),
    ISMOBILE         NUMBER (1) DEFAULT 0 NOT NULL,
    RGST_USERID 	 NUMBER(8)
)
TABLESPACE R6_DAT
LOGGING ;


/**
 * 업무가이드 활동이력
 */
CREATE TABLE GUIDE_HISTORY
(
    HISTORYID		NUMBER(8)	NOT NULL,	--PK            
    CLASSID			NUMBER (8), --FK (WB_CLASS.CLASSID)                     
    MODULEID		NUMBER (8), --FK (WB_MODULE.MODULEID)                   
    SPID 			NUMBER (8),                         
    APPID			NUMBER (8),                         
    ITEMID			NUMBER(8),		--FK (GUIDE.ITEMID) 
    INST_DATE		DATE DEFAULT  SYSDATE NULL,             
    USERID			NUMBER(8)	NOT NULL,               
    USER_NAME		VARCHAR2(1000),	                            
    USER_DISP		VARCHAR2(1000),	                        
    TITLE			VARCHAR2(1000),	                    
    EVENT			VARCHAR2(32),	                    
    VRSN_NUM        NUMBER (8) DEFAULT 1 NOT NULL,
   	REF_DISP		VARCHAR2(1000),	
	REF_NAME		VARCHAR2(1000),	
	REF_USERID		NUMBER(8),
	REF_TITLE		VARCHAR2(1000),
	VERSION_LABEL   VARCHAR2(10)
)
TABLESPACE R6_DAT
LOGGING ;


/**
 * 업무가이드 태그
 */
CREATE TABLE GUIDE_TAG
(
    ITEMID           NUMBER (8) NOT NULL, --FK (GUIDE.ITEMID)
    TAG              VARCHAR2 (100),
    ITEM_DATE        DATE,
    SEQ_ORDER        NUMBER (8) DEFAULT 0 NOT NULL,
    ISVISB           NUMBER (1) DEFAULT 0 NOT NULL
)
TABLESPACE R6_DAT
LOGGING ;


/**
 * 업무가이드 첨부파일
 */
CREATE TABLE GUIDE_FILE
(
    FILEID           NUMBER (8) NOT NULL,
    ITEMID           NUMBER (8) NOT NULL, --FK (GUIDE.ITEMID)
    FILE_NAME        VARCHAR2 (500),
    FILE_SIZE        NUMBER (16) DEFAULT 0 NOT NULL,
    DNLD_CNT         NUMBER (8) DEFAULT 0 NOT NULL,
    SAVE_CODE        NUMBER (4) DEFAULT - 1 NOT NULL,
    SAVE_PATH        VARCHAR2 (500),
    FILE_CODE        NUMBER (4) NULL
)
TABLESPACE R6_DAT
LOGGING ;

/**
 * 업무가이드 첨부파일
 */
CREATE TABLE GUIDE_RELATE_FILE
(
    FILEID           NUMBER (8) NOT NULL,
    ITEMID           NUMBER (8) NOT NULL, --FK (GUIDE_RELATE.RELTID)
    FILE_NAME        VARCHAR2 (500),
    FILE_SIZE        NUMBER (16) DEFAULT 0 NOT NULL,
    DNLD_CNT         NUMBER (8) DEFAULT 0 NOT NULL,
    SAVE_CODE        NUMBER (4) DEFAULT - 1 NOT NULL,
    SAVE_PATH        VARCHAR2 (500),
    FILE_CODE        NUMBER (4) NULL
)
TABLESPACE R6_DAT
LOGGING ;


CREATE TABLE GUIDE_APPR
(
	ITEMID           NUMBER (15) NOT NULL,
	SEQ_ORDER        NUMBER (8) DEFAULT 0 NOT NULL,
	USERID           NUMBER (8) NOT NULL,
	USER_NAME        VARCHAR2 (1000),
	USER_DISP        VARCHAR2 (1000)
)
TABLESPACE R6_DAT
LOGGING ;

CREATE TABLE GUIDE_RVWR
(
	ITEMID           NUMBER (15) NOT NULL,
	SEQ_ORDER        NUMBER (8) DEFAULT 0 NOT NULL,
	USERID           NUMBER (8) NOT NULL,
	USER_NAME        VARCHAR2 (1000),
	USER_DISP        VARCHAR2 (1000)
)
TABLESPACE R6_DAT
LOGGING ;

CREATE TABLE GUIDE_REQ 
(
	REQID            NUMBER (8) NOT NULL,
	ITEMID           NUMBER (15) NOT NULL,
	USERID           NUMBER (8) NOT NULL,
	USER_NAME        VARCHAR2 (1000),
	USER_DISP        VARCHAR2 (1000),
	TITLE            VARCHAR2 (1000),
	REQ_CODE         NUMBER (4),
	RES_CODE         NUMBER (4),
	REQ_DATE         DATE,
	VERSION_LABEL    VARCHAR2 (10),
	EXPR_DATE        DATE,
	RES_DATE         DATE,
	ISDEL            NUMBER (1) DEFAULT 0 NOT NULL,
	RCVR_CNT         NUMBER (8),
	READ_CNT         NUMBER (8),
	GID 			 NUMBER (8),
	OPN_CNT 		 NUMBER(8)
)
TABLESPACE R6_DAT
LOGGING ;

CREATE TABLE GUIDE_RES 
(
	REQID            NUMBER (8),
	USERID           NUMBER (8) NOT NULL,
	USER_NAME        VARCHAR2 (1000),
	USER_DISP        VARCHAR2 (1000),
	RES_CODE         NUMBER (4),
	RES_DATE         DATE,
	PRCS_CODE        NUMBER (4) DEFAULT 0 NOT NULL,
	CONTENT          VARCHAR2 (4000),
	SEQ_ORDER        NUMBER (8) DEFAULT 0 NOT NULL
)
TABLESPACE R6_DAT
LOGGING ;

/**
 * 업무가이드 담당자 (부서 추가를 위해 SCRT 테이블과 컬럼 똑같이 수정)
 */
CREATE TABLE GUIDE_CHARGE
(
    SEQ_ORDER 		NUMBER (8) DEFAULT 0 NOT NULL , 
    ITEMID    		NUMBER (8) NOT NULL ,	--FK (GUIDE.ITEMID)
    TITLE     		VARCHAR2 (1000) ,
    ISVISB    		NUMBER (1) DEFAULT 0 NOT NULL ,
    ISCOMP    		NUMBER (1) DEFAULT 0 NOT NULL ,
    XID       		NUMBER (17)
)
TABLESPACE R6_DAT
LOGGING ;

/**
 * 업무가이드 최근조회
 */
CREATE TABLE GUIDE_RECENT(
  ITEMID	 NUMBER(15) NOT NULL,
  USERID 	 NUMBER(8),
  INST_DATE  DATE,
  ACTION_TYPE NUMBER(4) NOT NULL,
  GID		 NUMBER (8),
  VERSION_LABEL    VARCHAR2(10)
)
TABLESPACE R6_DAT
LOGGING ;

/**
 * 업무가이드 템플릿
 */
CREATE TABLE GUIDE_TEMPLATE
(
	TEMPLATEID	   NUMBER (8) NOT NULL,
	ITEMID         NUMBER (8),
	KMID           NUMBER (8),
	TITLE          VARCHAR2 (1000),
	CONTENT		   CLOB,
	CLASSID 	   NUMBER(8),
	MODUlEID 	   NUMBER(8),
	SPID 		   NUMBER(8),
	APPID 		   NUMBER(8),
	LEVEL1			NUMBER (8),
    LEVEL2			NUMBER (8),
    LEVEL3			NUMBER (8),
    LEVEL4			NUMBER (8),
    TABLE_CONTENT	CLOB,
	FILE_CONTENT	CLOB,
	KNOW_CONTENT	CLOB,
	INST_DATE       DATE DEFAULT SYSDATE
)
TABLESPACE R6_DAT
LOGGING ;

/**
 * 템플릿 첨부파일
 */
CREATE TABLE GUIDE_TEMPLATE_FILE
(
    FILEID           NUMBER (8) NOT NULL,
    TEMPLATEID       NUMBER (8) NOT NULL, --FK (GUIDE_TEMPLATE.TEMPLATEID)
    FILE_NAME        VARCHAR2 (500),
    FILE_SIZE        NUMBER (16) DEFAULT 0 NOT NULL,
    DNLD_CNT         NUMBER (8) DEFAULT 0 NOT NULL,
    SAVE_CODE        NUMBER (4) DEFAULT - 1 NOT NULL,
    SAVE_PATH        VARCHAR2 (500),
    FILE_CODE        NUMBER (4) NULL
)
TABLESPACE R6_DAT
LOGGING ;

/**
 * 템플릿 관련지식
 */
CREATE TABLE GUIDE_TEMPLATE_RFRN 
(
    RFRNID           NUMBER (8) NOT NULL,	--PK
    TEMPLATEID       NUMBER (8) NOT NULL,	--FK (GUIDE_TEMPLATE.TEMPLATEID)
    TITLE            VARCHAR2 (1000),
    USERID           NUMBER (8),
    USER_NAME        VARCHAR2 (1000),
    RFRN_QUERY       VARCHAR2 (500),
    MODULEID		 NUMBER (8), --FK (WB_MODULE.MODULEID)
    CLASSID			 NUMBER (8), --FK (WB_CLASS.CLASSID)
    SPID             NUMBER (8),
    APPID            NUMBER (8),
    EXTR_APPID 		 NUMBER (8)
)
TABLESPACE R6_DAT
LOGGING ;

/**
 * 업무가이드 맵 조회용 테이블
 * 통합검색 등 외부에 맵 정보 제공용 테이블
 */
CREATE TABLE GUIDE_TABLE 
(
    TABLEID 		NUMBER (8) NOT NULL, --PK        
    ITEMID 			NUMBER (8), --FK (GUIDE.ITEMID)  
    TITLE           VARCHAR2 (1000),                 
    UUID            VARCHAR2 (64),                   
    P_UUID          VARCHAR2 (64),                  
    CONTENT 		CLOB,
    LAST_UPDT 		DATE DEFAULT SYSDATE,
    SEQ				NUMBER (4),
    MODULEID      	NUMBER(8),    
	CLASSID       	NUMBER(8),
	APPID         	NUMBER(8),
	SPID          	NUMBER(8),
	CID			  	VARCHAR2(50),
	TRNS_KEY		VARCHAR2(50),
	PATH         	VARCHAR2 (2000)
)
TABLESPACE R6_DAT
LOGGING ;