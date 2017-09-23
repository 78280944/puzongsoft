SET SESSION FOREIGN_KEY_CHECKS=0;

/* Drop Indexes */

DROP INDEX ACCOUNTID ON LOTTERY_ORDER;
DROP INDEX ROUNDID ON LOTTERY_ORDER;
DROP INDEX ORDERID ON LOTTERY_ORDER_DETAIL;
DROP INDEX LOTTERYTYPE ON LOTTERY_ROUND;
DROP INDEX ROUNDID ON LOTTERY_ROUND_ITEM;
DROP INDEX NAME_GROUP ON TASK_SCHEDULE_JOB;
DROP INDEX ACCOUNTID ON TRADE_INFO;



/* Drop Tables */

DROP TABLE LOTTERY_ORDER_DETAIL;
DROP TABLE LOTTERY_ORDER;
DROP TABLE TRADE_INFO;
DROP TABLE ACCOUNT_DETAIL;
DROP TABLE ACCOUNT_INFO;
DROP TABLE ACCOUNT_RECORD;
DROP TABLE CODE_CATALOG;
DROP TABLE CODE_LIBRARY;
DROP TABLE LOTTERY_GAME;
DROP TABLE LOTTERY_GAME_ORDER;
DROP TABLE LOTTERY_GAME_ROUND;
DROP TABLE LOTTERY_ITEM;
DROP TABLE LOTTERY_ROOM;
DROP TABLE LOTTERY_ROOM_DETAIL;
DROP TABLE LOTTERY_ROUND_ITEM;
DROP TABLE LOTTERY_ROUND;
DROP TABLE LOTTERY_SERVICE;
DROP TABLE NOTICE_INFO;
DROP TABLE OFFACCOUNT_INFO;
DROP TABLE SUBACCOUNT_INFO;
DROP TABLE TASK_SCHEDULE_JOB;
DROP TABLE T_S_TIMETASK;




/* Create Tables */

CREATE TABLE ACCOUNT_DETAIL
(
	ACCOUNTID INT NOT NULL AUTO_INCREMENT COMMENT 'ACCOUNTID',
	USERID INT NOT NULL COMMENT 'USERID',
	USERNAME VARCHAR(20) NOT NULL UNIQUE COMMENT 'USERNAME',
	LIMITED DOUBLE(18,2) DEFAULT 0.00 COMMENT 'LIMITED',
	RATIO DOUBLE(18,4) DEFAULT 0.0000 COMMENT 'RATIO',
	PERCENTAGE DOUBLE(18,4) DEFAULT 0.0000 COMMENT 'PERCENTAGE',
	STATE CHAR(1) DEFAULT '1' COMMENT 'STATE',
	SUPUSERNAME VARCHAR(20) COMMENT 'SUPUSERNAME',
	LEVEL VARCHAR(20) COMMENT 'LEVEL',
	OFFTYPE CHAR(1) COMMENT 'OFFTYPE',
	MONEY DECIMAL(19,2) DEFAULT 0.00 COMMENT 'MONEY',
	ATTRIBUTE1 VARCHAR(20) COMMENT 'ATTRIBUTE1',
	ATTRIBUTE2 VARCHAR(20) COMMENT 'ATTRIBUTE2',
	BUDGET VARCHAR(100) COMMENT 'BUDGET',
	PRIMARY KEY (ACCOUNTID)
) COMMENT = 'account_detail';


CREATE TABLE ACCOUNT_INFO
(
	ACCOUNTID INT NOT NULL AUTO_INCREMENT COMMENT '用户流水号',
	USERNAME VARCHAR(20) DEFAULT '<EMPTY STRING>' NOT NULL COMMENT '用户名',
	AUSERNAME VARCHAR(20) DEFAULT '<EMPTY STRING>' NOT NULL COMMENT '别名（昵称）',
	PASSWORD VARCHAR(50) DEFAULT '<EMPTY STRING>' NOT NULL COMMENT '密码',
	LIMITED DOUBLE(18,2) DEFAULT 0.00 COMMENT '限额',
	RATIO DOUBLE(18,4) DEFAULT 0.00 COMMENT '洗码比',
	IP VARCHAR(20) DEFAULT '<EMPTY STRING>' COMMENT 'IP地址',
	INPUTDATE DATETIME COMMENT '新增时间',
	UPDATEIP VARCHAR(20) DEFAULT '<EMPTY STRING>' COMMENT '修改IP',
	UPDATEDATE DATETIME COMMENT '修改时间',
	STATE CHAR NOT NULL COMMENT '状态',
	SUPUSERID VARCHAR(20) DEFAULT '<EMPTY STRING>' NOT NULL COMMENT '上级编号',
	LEVEL VARCHAR(20) DEFAULT '<EMPTY STRING>' COMMENT '级别',
	PHONE VARCHAR(20) COMMENT '电话',
	WEBCHAT VARCHAR(30) COMMENT '微信',
	USERMONEY DECIMAL(19,2) COMMENT '账户余额',
	OFFTYPE VARCHAR(10) COMMENT '账户状态',
	PERCENTAGE DOUBLE(18,4) COMMENT '代理占比',
	QUERY VARCHAR(100) COMMENT '权限',
	BUDGET VARCHAR(100) COMMENT 'BUDGET',
	CODE VARCHAR(6) COMMENT '邀请码',
	SFCODE VARCHAR(30) COMMENT '安全码',
	BANKID VARCHAR(100) COMMENT '银行用户名',
	BANKNAME VARCHAR(100) COMMENT '银行名称',
	BANKADDRESS VARCHAR(100) COMMENT '开户行',
	BANKACCOUNT VARCHAR(100) COMMENT '银行账号',
	PRIMARY KEY (ACCOUNTID)
) COMMENT = 'account_info' DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;


CREATE TABLE ACCOUNT_RECORD
(
	RECORDID INT(11) NOT NULL UNIQUE COMMENT '记录流水号',
	ACCOUNTID INT(11) NOT NULL COMMENT '用户流水号',
	INPUTTIME DATETIME COMMENT '登录时间',
	IP VARCHAR(30) COMMENT 'IP',
	OUTTIME DATETIME COMMENT '登出时间',
	LEVEL VARCHAR(4) COMMENT '级别',
	OFFTYPE VARCHAR(4) COMMENT '账户类型'
) COMMENT = 'account_record';


CREATE TABLE CODE_CATALOG
(
	CODENO VARCHAR(20) NOT NULL COMMENT '代码编号',
	CODENAME VARCHAR(30) COMMENT '代码名称',
	SORTNO VARCHAR(20) COMMENT '索引号',
	REMARK VARCHAR(100) COMMENT '注释说明'
) COMMENT = 'code_catalog';


CREATE TABLE CODE_LIBRARY
(
	CODENO VARCHAR(20) NOT NULL COMMENT 'codeno',
	ITEMNO VARCHAR(20) NOT NULL COMMENT '码值编号',
	ITEMNAME VARCHAR(50) NOT NULL COMMENT '码值名称',
	ISUSE VARCHAR(4) NOT NULL COMMENT '是否有效'
) COMMENT = 'code_library';


CREATE TABLE LOTTERY_GAME
(
	SID INT(11) NOT NULL UNIQUE COMMENT '流水号',
	GAMETYPE VARCHAR(4) COMMENT '游戏类型',
	GAMENAME VARCHAR(100) COMMENT '游戏名称',
	GAMETERM VARCHAR(11) COMMENT '游戏期数',
	GAMELOBBYNO VARCHAR(20) COMMENT '游戏厅编号',
	GAMELOBBYNAME VARCHAR(50) COMMENT '游戏厅名称',
	GAMESTARTTIME DATETIME COMMENT '游戏开始时间',
	GAMEOVERTIME DATETIME COMMENT '游戏结束时间'
) COMMENT = 'lottery_game';


CREATE TABLE LOTTERY_GAME_ORDER
(
	LGMID INT(11) NOT NULL UNIQUE COMMENT '投注id',
	SID INT(11) COMMENT '游戏id',
	ACCOUNTID INT(11) COMMENT '用户id',
	RMID INT(11) COMMENT '房间ID',
	LTDID INT(11) COMMENT '台号id',
	NOID INT(4) COMMENT '台号数',
	PLAYORIDLE VARCHAR(4) COMMENT '庄闲',
	LOTTERYTERM VARCHAR(11) COMMENT '游戏期数',
	ORDERAMOUNT DECIMAL(18,2) COMMENT '投注金额',
	ORDERTIME DATETIME COMMENT '投注时间',
	OPENTIME DATETIME COMMENT '开奖时间',
	RESULT DECIMAL(18,2) COMMENT '战绩',
	LASTAMOUNT DECIMAL(18,2) COMMENT '输赢'
) COMMENT = 'lottery_game_order';


CREATE TABLE LOTTERY_GAME_ROUND
(
	LGRID INT(11) NOT NULL COMMENT '游戏id',
	SID INT(11) COMMENT '游戏类别',
	LOTTERYTERM VARCHAR(11) COMMENT '游戏期次',
	LOTTERYRESULT VARCHAR(50) COMMENT '游戏结果',
	STARTTIME DATETIME COMMENT '游戏开始时间',
	OVERTIME DATETIME COMMENT '结束时间',
	OPENTIME DATETIME COMMENT '开奖时间',
	CLOSETIME DATETIME COMMENT '封盘时间',
	ACTOPENTIME DATETIME COMMENT '实际开盘时间',
	ACTCLOSETIME DATETIME COMMENT '实际封盘时间'
) COMMENT = 'lottery_game_round';


CREATE TABLE LOTTERY_ITEM
(
	-- 流水号
	ITEMID INT NOT NULL UNIQUE AUTO_INCREMENT COMMENT 'ItemId',
	-- 游戏类型：01玉米籽
	LOTTERYTYPE VARCHAR(10) NOT NULL COMMENT 'LotteryType',
	-- 投注项编号
	ITEMNO VARCHAR(10) NOT NULL COMMENT 'ItemNo',
	-- 投注项名称
	ITEMNAME VARCHAR(50) COMMENT 'ItemName',
	-- 投注项类型：01广西快乐十分，02番摊玉米籽
	ITEMTYPE VARCHAR(10) COMMENT 'ItemType',
	-- 类别，如特码、色、单双、大小等
	ITEMGROUP VARCHAR(20) COMMENT 'ItemGroup',
	-- 游戏界面显示的赔率
	ITEMSCALE DOUBLE(18,4) NOT NULL COMMENT 'ItemScale',
	-- 派彩赔率
	ITEMODDS DOUBLE(18,4) COMMENT 'ItemOdds',
	-- 抽水比例
	ITEMBONUS DOUBLE(18,4) COMMENT 'ItemBonus',
	-- 下注项中文名称
	ITEMNAMECN VARCHAR(50) COMMENT 'ItemNameCN',
	PRIMARY KEY (ITEMID)
) COMMENT = 'lottery_item';


CREATE TABLE LOTTERY_ORDER
(
	-- 投注单ID
	ORDERID INT NOT NULL AUTO_INCREMENT COMMENT '投注单ID',
	-- 游戏ID
	ROUNDID INT NOT NULL COMMENT '游戏ID',
	-- 帐户ID
	ACCOUNTID INT NOT NULL COMMENT '帐户ID',
	-- 投注金额
	ORDERAMOUNT DOUBLE(18,2) COMMENT '投注金额',
	-- 代理提成
	COMMISIONAMOUNT DOUBLE(18,2) COMMENT '代理提成',
	ORDERTIME DATETIME NOT NULL COMMENT 'ORDERTIME',
	-- 中奖金额
	PRIZEAMOUNT DOUBLE(18,2) COMMENT '中奖金额',
	-- 实际到手奖金
	ACTUALAMOUNT DOUBLE(18,2) COMMENT '实际到手奖金',
	-- 抽水返利
	RETURNAMOUNT DOUBLE(18,2) COMMENT '抽水返利',
	SYSTEMAMOUNT DOUBLE(18,2) COMMENT 'SYSTEMAMOUNT',
	-- 中奖时间
	PRIZETIME DATETIME COMMENT '中奖时间',
	-- 帐号余额
	ACCOUNTAMOUNT DECIMAL(19,2) COMMENT '帐号余额',
	PRIMARY KEY (ORDERID)
) COMMENT = 'lottery_order';


CREATE TABLE LOTTERY_ORDER_DETAIL
(
	-- 投注单ID
	ORDERID INT NOT NULL COMMENT '投注单ID',
	-- 投注项编号
	ITEMNO VARCHAR(10) NOT NULL COMMENT '投注项编号',
	-- 赔率
	ITEMSCALE DOUBLE(18,4) COMMENT '赔率',
	-- 抽水比例
	ITEMBONUS DOUBLE(18,4) COMMENT '抽水比例',
	-- 投注金额
	DETAILAMOUNT DOUBLE(18,2) COMMENT '投注金额',
	-- 中奖金额
	PRIZEAMOUNT DOUBLE(18,2) COMMENT '中奖金额',
	-- 实际到手奖金
	ACTUALAMOUNT DOUBLE(18,2) COMMENT '实际到手奖金',
	-- 抽水返利
	RETURNAMOUNT DOUBLE(18,2) COMMENT '抽水返利',
	PRIMARY KEY (ORDERID, ITEMNO)
) COMMENT = 'lottery_order_detail';


CREATE TABLE LOTTERY_ROOM
(
	RMID INT(11) COMMENT '流水号',
	SID INT(11) COMMENT '游戏流水号',
	ROOMID INT(11) COMMENT '房间号',
	ROOMNUMBER INT(4) COMMENT '房间人数'
) COMMENT = 'lottery_room';


CREATE TABLE LOTTERY_ROOM_DETAIL
(
	LTDID INT(11) COMMENT '流水号',
	RMID INT(11) COMMENT '房间号',
	SID INT(11) COMMENT '游戏ID',
	LOTTERYTERM VARCHAR(11) COMMENT '游戏期数',
	GAMESTARTTIME DATETIME COMMENT '游戏开始时间',
	GAMEOVERTIME DATETIME COMMENT '游戏结束时间',
	NO1 VARCHAR(20) COMMENT '01台',
	NO2 VARCHAR(20) COMMENT '02台',
	NO3 VARCHAR(20) COMMENT '03台',
	NO4 VARCHAR(20) COMMENT '04台',
	NO5 VARCHAR(20) COMMENT '05台',
	NO6 VARCHAR(20) COMMENT '06台',
	NO7 VARCHAR(20) COMMENT '07台',
	NO8 VARCHAR(20) COMMENT '08台',
	NO9 VARCHAR(20) COMMENT '09台',
	NO10 VARCHAR(20) COMMENT '010台',
	NO11 VARCHAR(20) COMMENT '011台',
	NO12 VARCHAR(20) COMMENT '012台',
	NO13 VARCHAR(20) COMMENT '013台',
	NO14 VARCHAR(20) COMMENT '014台',
	NO15 VARCHAR(20) COMMENT '015台',
	NO16 VARCHAR(20) COMMENT '016台',
	NO17 VARCHAR(20) COMMENT '017台',
	NO18 VARCHAR(20) COMMENT '018台',
	NO19 VARCHAR(20) COMMENT '019台',
	NO20 VARCHAR(20) COMMENT '020台',
	NO21 VARCHAR(20) COMMENT '021台',
	NO22 VARCHAR(20) COMMENT '022台',
	NO23 VARCHAR(20) COMMENT '023台',
	NO24 VARCHAR(20) COMMENT '024台',
	NO25 VARCHAR(20) COMMENT '025台',
	NO26 VARCHAR(20) COMMENT '026台',
	NO27 VARCHAR(20) COMMENT '027台',
	NO28 VARCHAR(20) COMMENT '028台',
	NO29 VARCHAR(20) COMMENT '029台',
	NO30 VARCHAR(20) COMMENT '030台'
) COMMENT = 'lottery_room_detail';


CREATE TABLE LOTTERY_ROUND
(
	-- 游戏ID
	ROUNDID INT NOT NULL AUTO_INCREMENT COMMENT '游戏ID',
	-- 游戏类型：01玉米籽
	LOTTERYTYPE VARCHAR(10) NOT NULL COMMENT '游戏类型：01玉米籽',
	-- 期次
	LOTTERYTERM VARCHAR(20) NOT NULL COMMENT '期次',
	-- 开出之号码
	RESULTSTR VARCHAR(100) COMMENT '开出之号码',
	-- 游戏开始时间
	STARTTIME DATETIME COMMENT '游戏开始时间',
	-- 游戏开奖时间
	ENDTIME DATETIME COMMENT '游戏开奖时间',
	-- 游戏状态：Open开盘中，Close已封盘，End已结束
	ROUNDSTATUS VARCHAR(10) NOT NULL COMMENT '游戏状态：Open开盘中，Close已封盘，End已结束',
	OPENTIME DATETIME COMMENT 'OPENTIME',
	CLOSETIME DATETIME COMMENT 'CLOSETIME',
	ORIGINRESULT VARCHAR(100) COMMENT 'ORIGINRESULT',
	ACTUALOPENTIME VARCHAR(100) COMMENT 'ACTUALOPENTIME',
	ACTUALCLOSETIME VARCHAR(100) COMMENT 'ACTUALCLOSETIME',
	PRIMARY KEY (ROUNDID)
) COMMENT = 'lottery_round';


CREATE TABLE LOTTERY_ROUND_ITEM
(
	-- 游戏ID
	ROUNDID INT NOT NULL COMMENT '游戏ID',
	-- 投注项编号
	ITEMNO VARCHAR(10) NOT NULL COMMENT '投注项编号',
	-- 游戏界面显示的赔率
	ITEMSCALE DOUBLE(18,4) COMMENT '游戏界面显示的赔率',
	-- 更新时间
	UPDATETIME DATETIME COMMENT '更新时间',
	PRIMARY KEY (ROUNDID, ITEMNO)
) COMMENT = 'lottery_round_item';


CREATE TABLE LOTTERY_SERVICE
(
	SID INT(11) NOT NULL UNIQUE COMMENT '流水号',
	LOGINSERVICE VARCHAR(4) COMMENT '登录服务',
	REGISTERSERCICE VARCHAR(4) COMMENT '注册服务',
	ADDEDSERVICE VARCHAR(4) COMMENT '增值服务',
	PLAYSERVICE VARCHAR(4) COMMENT '试玩服务',
	AREMARKSERCIE VARCHAR(200) COMMENT 'aremarksercie',
	DREMARKSERCIE VARCHAR(200) COMMENT 'dremarksercie',
	PRIMARY KEY (SID)
) COMMENT = 'lottery_service';


CREATE TABLE NOTICE_INFO
(
	NOTICEID INT NOT NULL AUTO_INCREMENT COMMENT 'Noticeid',
	TITLE VARCHAR(200) COMMENT 'Title',
	NOTICE VARCHAR(2000) COMMENT 'Notice',
	STYPE CHAR(1) COMMENT 'Stype',
	UPDATEIP VARCHAR(20) COMMENT 'UpdateIP',
	UPDATEDATE DATETIME COMMENT 'UpdateDate',
	STATE CHAR(1) COMMENT 'State',
	SUPUSERNAME VARCHAR(20) COMMENT 'SupUserName',
	OFFTYPE CHAR(1) COMMENT 'OffType',
	ATTRIBUTE1 VARCHAR(20) COMMENT 'Attribute1',
	ATTRIBUTE2 VARCHAR(20) COMMENT 'Attribute2',
	PRIMARY KEY (NOTICEID)
) COMMENT = 'notice_info';


CREATE TABLE OFFACCOUNT_INFO
(
	ACCOUNTID INT NOT NULL AUTO_INCREMENT COMMENT '用户流水号',
	USERNAME VARCHAR(20) DEFAULT '<EMPTY STRING>' NOT NULL COMMENT '用户名',
	AUSERNAME VARCHAR(20) DEFAULT '<EMPTY STRING>' NOT NULL COMMENT '别名（昵称）',
	PASSWORD VARCHAR(50) DEFAULT '<EMPTY STRING>' NOT NULL COMMENT '密码',
	LIMITED DOUBLE(18,2) DEFAULT 0.00 COMMENT 'Limited',
	RATIO DOUBLE(18,4) DEFAULT 0.00 COMMENT '洗码比',
	PERCENTAGE DOUBLE(18,4) DEFAULT 0.00 COMMENT 'Percentage',
	QUERY VARCHAR(50) DEFAULT '<EMPTY STRING>' COMMENT 'Query',
	MANAGE VARCHAR(50) DEFAULT '<EMPTY STRING>' COMMENT 'Manage',
	IP VARCHAR(20) DEFAULT '<EMPTY STRING>' COMMENT 'IP地址',
	INPUTDATE DATETIME COMMENT '新增时间',
	UPDATEIP VARCHAR(20) DEFAULT '<EMPTY STRING>' COMMENT '修改IP',
	UPDATEDATE DATETIME COMMENT '修改时间',
	STATE CHAR DEFAULT '1' COMMENT '状态',
	SUPUSERID VARCHAR(20) DEFAULT '<EMPTY STRING>' NOT NULL COMMENT '上级编号',
	LEVEL VARCHAR(20) DEFAULT '<EMPTY STRING>' COMMENT '级别',
	OFFTYPE CHAR DEFAULT '<EMPTY STRING>' COMMENT 'OffType',
	-- 风险限额
	RISKAMOUNT VARCHAR(100) COMMENT 'RiskAmount',
	ATTRIBUTE2 VARCHAR(20) COMMENT 'Attribute2',
	PRIMARY KEY (ACCOUNTID)
) COMMENT = 'offaccount_info' DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;


CREATE TABLE SUBACCOUNT_INFO
(
	SERIALNO INT NOT NULL AUTO_INCREMENT COMMENT 'Serialno',
	USERNAME VARCHAR(20) NOT NULL COMMENT 'UserName',
	AUSERNAME VARCHAR(20) NOT NULL COMMENT 'AuserName',
	PASSWORD VARCHAR(50) NOT NULL COMMENT 'Password',
	QUERY VARCHAR(20) COMMENT 'Query',
	MANAGE VARCHAR(20) COMMENT 'Manage',
	IP VARCHAR(20) COMMENT 'IP',
	INPUTDATE DATETIME COMMENT 'InputDate',
	UPDATEIP VARCHAR(20) COMMENT 'UpdateIP',
	UPDATEDATE DATETIME COMMENT 'UpdateDate',
	STATE CHAR(1) NOT NULL COMMENT 'State',
	SUPUSERNAME VARCHAR(20) COMMENT 'SupUserName',
	LEVEL VARCHAR(20) COMMENT 'Level',
	ATTRIBUTE1 VARCHAR(20) COMMENT 'Attribute1',
	ATTRIBUTE2 VARCHAR(20) COMMENT 'Attribute2',
	PRIMARY KEY (SERIALNO)
) COMMENT = 'subaccount_info';


CREATE TABLE TASK_SCHEDULE_JOB
(
	JOB_ID BIGINT NOT NULL AUTO_INCREMENT COMMENT 'job_id',
	CREATE_TIME TIMESTAMP COMMENT 'create_time',
	UPDATE_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'update_time',
	JOB_NAME VARCHAR(255) COMMENT 'job_name',
	JOB_GROUP VARCHAR(255) COMMENT 'job_group',
	JOB_STATUS VARCHAR(255) COMMENT 'job_status',
	CRON_EXPRESSION VARCHAR(255) NOT NULL COMMENT 'cron_expression',
	DESCRIPTION VARCHAR(255) COMMENT 'description',
	BEAN_CLASS VARCHAR(255) COMMENT 'bean_class',
	-- 1
	IS_CONCURRENT VARCHAR(255) COMMENT '1',
	SPRING_ID VARCHAR(255) COMMENT 'spring_id',
	METHOD_NAME VARCHAR(255) NOT NULL COMMENT 'method_name',
	PRIMARY KEY (JOB_ID)
) COMMENT = 'task_schedule_job';


CREATE TABLE TRADE_INFO
(
	-- 流水号
	TRADEID INT NOT NULL AUTO_INCREMENT COMMENT '流水号',
	-- 帐户ID
	ACCOUNTID INT NOT NULL COMMENT '帐户ID',
	-- 业务类型：Inout出入金；Trade交易；
	TRADETYPE VARCHAR(10) NOT NULL COMMENT '业务类型：Inout出入金；Trade交易；',
	RELATIVEID INT COMMENT 'RELATIVEID',
	-- 业务关联类型:In入金；Out出金；Commision提成；Pay付款；Order投注；Prize奖金；Return洗码
	RELATIVETYPE VARCHAR(10) NOT NULL COMMENT '业务关联类型:In入金；Out出金；Commision提成；Pay付款；Order投注；Prize奖金；Return洗码',
	-- 交易金额
	TRADEAMOUNT DOUBLE(18,2) NOT NULL COMMENT '交易金额',
	-- 帐号余额
	ACCOUNTAMOUNT DECIMAL(19,2) COMMENT '帐号余额',
	-- 录入时间
	INPUTTIME DATETIME COMMENT '录入时间',
	REMARK VARCHAR(100) COMMENT 'REMARK',
	PRIMARY KEY (TRADEID)
) COMMENT = 'trade_info';


CREATE TABLE T_S_TIMETASK
(
	ID VARCHAR(32) NOT NULL COMMENT 'ID',
	CREATE_BY VARCHAR(32) COMMENT 'CREATE_BY',
	CREATE_DATE DATETIME COMMENT 'CREATE_DATE',
	CREATE_NAME VARCHAR(32) COMMENT 'CREATE_NAME',
	CRON_EXPRESSION VARCHAR(100) NOT NULL COMMENT 'CRON_EXPRESSION',
	IS_EFFECT VARCHAR(1) NOT NULL COMMENT 'IS_EFFECT',
	IS_START VARCHAR(1) NOT NULL COMMENT 'IS_START',
	TASK_DESCRIBE VARCHAR(50) NOT NULL COMMENT 'TASK_DESCRIBE',
	TASK_ID VARCHAR(100) NOT NULL COMMENT 'TASK_ID',
	START_TIME DATETIME COMMENT 'START_TIME',
	UPDATE_BY VARCHAR(32) COMMENT 'UPDATE_BY',
	UPDATE_DATE DATETIME COMMENT 'UPDATE_DATE',
	UPDATE_NAME VARCHAR(32) COMMENT 'UPDATE_NAME',
	PRIMARY KEY (ID)
) COMMENT = 't_s_timetask';



/* Create Foreign Keys */

ALTER TABLE LOTTERY_ORDER
	ADD CONSTRAINT LOTTERY_ORDER_IBFK_1 FOREIGN KEY (ACCOUNTID)
	REFERENCES ACCOUNT_DETAIL (ACCOUNTID)
	ON UPDATE NO ACTION
	ON DELETE NO ACTION
;


ALTER TABLE TRADE_INFO
	ADD CONSTRAINT TRADE_INFO_IBFK_1 FOREIGN KEY (ACCOUNTID)
	REFERENCES ACCOUNT_DETAIL (ACCOUNTID)
	ON UPDATE NO ACTION
	ON DELETE NO ACTION
;


ALTER TABLE LOTTERY_ORDER_DETAIL
	ADD CONSTRAINT LOTTERY_ORDER_DETAIL_IBFK_1 FOREIGN KEY (ORDERID)
	REFERENCES LOTTERY_ORDER (ORDERID)
	ON UPDATE NO ACTION
	ON DELETE NO ACTION
;


ALTER TABLE LOTTERY_ORDER
	ADD CONSTRAINT LOTTERY_ORDER_IBFK_2 FOREIGN KEY (ROUNDID)
	REFERENCES LOTTERY_ROUND (ROUNDID)
	ON UPDATE NO ACTION
	ON DELETE NO ACTION
;


ALTER TABLE LOTTERY_ROUND_ITEM
	ADD CONSTRAINT LOTTERY_ROUND_ITEM_IBFK_1 FOREIGN KEY (ROUNDID)
	REFERENCES LOTTERY_ROUND (ROUNDID)
	ON UPDATE NO ACTION
	ON DELETE NO ACTION
;



/* Create Indexes */

CREATE INDEX ACCOUNTID USING BTREE ON LOTTERY_ORDER (ACCOUNTID ASC);
CREATE INDEX ROUNDID USING BTREE ON LOTTERY_ORDER (ROUNDID ASC);
CREATE UNIQUE INDEX ORDERID USING BTREE ON LOTTERY_ORDER_DETAIL (ORDERID ASC, ITEMNO ASC);
CREATE UNIQUE INDEX LOTTERYTYPE USING BTREE ON LOTTERY_ROUND (LOTTERYTYPE ASC, LOTTERYTERM ASC);
CREATE UNIQUE INDEX ROUNDID USING BTREE ON LOTTERY_ROUND_ITEM (ROUNDID ASC, ITEMNO ASC);
CREATE UNIQUE INDEX NAME_GROUP USING BTREE ON TASK_SCHEDULE_JOB (JOB_NAME ASC, JOB_GROUP ASC);
CREATE INDEX ACCOUNTID USING BTREE ON TRADE_INFO (ACCOUNTID ASC);



