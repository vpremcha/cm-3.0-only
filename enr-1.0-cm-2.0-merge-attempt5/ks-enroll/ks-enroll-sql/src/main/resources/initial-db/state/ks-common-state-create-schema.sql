-----------------------------------------------------------------------------
-- DROP all the tables
-----------------------------------------------------------------------------
DECLARE temp NUMBER;
BEGIN
  SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KSEN_STATE';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KSEN_STATE CASCADE CONSTRAINTS PURGE'; END IF;
END;
/
DECLARE temp NUMBER;
BEGIN
  SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KSEN_STATE_ATTR';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KSEN_STATE_ATTR CASCADE CONSTRAINTS PURGE'; END IF;
END;
/
DECLARE temp NUMBER;
BEGIN
  SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KSEN_STATE_LIFECYCLE';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KSEN_STATE_LIFECYCLE CASCADE CONSTRAINTS PURGE'; END IF;
END;
/
DECLARE temp NUMBER;
BEGIN
  SELECT COUNT(*) INTO temp FROM user_tables WHERE table_name = 'KSEN_STATE_LIFECYCLE_ATTR';
	IF temp > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KSEN_STATE_LIFECYCLE_ATTR CASCADE CONSTRAINTS PURGE'; END IF;
END;
/
CREATE TABLE KSEN_STATE
(
	ID                   VARCHAR2(255) NOT NULL ,
	OBJ_ID               VARCHAR2(36) NULL ,
	NAME                 VARCHAR2(255) NOT NULL ,
	DESCR_PLAIN          VARCHAR2(4000) NULL ,
	DESCR_FORMATTED      VARCHAR2(4000) NULL ,
	LIFECYCLE_KEY        VARCHAR2(255) NOT NULL ,
	EFF_DT               TIMESTAMP(6) NULL ,
	EXPIR_DT             TIMESTAMP(6) NULL ,
	VER_NBR              NUMBER(19) NOT NULL ,
	CREATETIME           TIMESTAMP(6) NOT NULL ,
	CREATEID             VARCHAR2(255) NOT NULL ,
	UPDATETIME           TIMESTAMP(6) NULL ,
	UPDATEID             VARCHAR2(255) NULL 
)
/


CREATE TABLE KSEN_STATE_ATTR
(
	ID                   VARCHAR2(255) NOT NULL ,
	OBJ_ID               VARCHAR2(36) NULL ,
	ATTR_KEY             VARCHAR2(255) NULL ,
	ATTR_VALUE           VARCHAR2(4000) NULL ,
	OWNER_ID             VARCHAR2(255) NULL 
)
/


CREATE TABLE KSEN_STATE_LIFECYCLE
(
	ID                   VARCHAR2(255) NOT NULL ,
	OBJ_ID               VARCHAR2(36) NULL ,
	NAME                 VARCHAR2(255 BYTE) NOT NULL ,
	DESCR_PLAIN          VARCHAR2(4000) NULL ,
	DESCR_FORMATTED      VARCHAR2(4000) NULL ,
	REF_OBJECT_URI       VARCHAR2(255) NULL ,
	VER_NBR              NUMBER(19) NOT NULL ,
	CREATETIME           TIMESTAMP(6) NOT NULL ,
	CREATEID             VARCHAR2(255) NOT NULL ,
	UPDATETIME           TIMESTAMP(6) NULL ,
	UPDATEID             VARCHAR2(255) NULL 
)
/
CREATE TABLE KSEN_STATE_LIFECYCLE_ATTR
(
	OBJ_ID               VARCHAR2(36) NULL ,
	ATTR_KEY             VARCHAR2(255) NULL ,
	ATTR_VALUE           VARCHAR2(4000) NULL ,
	OWNER_ID             VARCHAR2(255) NULL ,
	ID                   VARCHAR2(255) NOT NULL 
)
/

DECLARE TEMP NUMBER;
BEGIN
  SELECT COUNT(*) INTO TEMP FROM USER_TABLES WHERE TABLE_NAME = 'KSEN_STATE_CHG';
	IF TEMP > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KSEN_STATE_CHG CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

-----------------------------------------------------------------------------
-- State Change related tables
-----------------------------------------------------------------------------

CREATE TABLE KSEN_STATE_CHG
(
	ID                   VARCHAR2(255) NOT NULL ,
	OBJ_ID               VARCHAR2(36) NULL ,
	STATE_CHG_TYPE       VARCHAR2(255) NOT NULL ,
	STATE_CHG_STATE      VARCHAR2(255) NOT NULL ,
	EFF_DT               TIMESTAMP(6) NULL ,
	EXPIR_DT             TIMESTAMP(6) NULL ,
	VER_NBR              NUMBER(19) NOT NULL ,
	CREATETIME           TIMESTAMP(6) NOT NULL ,
	CREATEID             VARCHAR2(255) NOT NULL ,
	UPDATETIME           TIMESTAMP(6) NULL ,
	UPDATEID             VARCHAR2(255) NULL ,
	FROM_STATE_ID        VARCHAR2(255) NOT NULL ,
	TO_STATE_ID          VARCHAR2(255) NOT NULL
)
/


DECLARE TEMP NUMBER;
BEGIN
  SELECT COUNT(*) INTO TEMP FROM USER_TABLES WHERE TABLE_NAME = 'KSEN_STATE_CHG_ATTR';
	IF TEMP > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KSEN_STATE_CHG_ATTR CASCADE CONSTRAINTS PURGE'; END IF;
END;
/


CREATE TABLE KSEN_STATE_CHG_ATTR
(
	ID                   VARCHAR2(255) NOT NULL ,
	OBJ_ID               VARCHAR2(36) NULL ,
	ATTR_KEY             VARCHAR2(255) NULL ,
	ATTR_VALUE           VARCHAR2(4000) NULL ,
	OWNER_ID             VARCHAR2(255) NULL
)
/

DECLARE TEMP NUMBER;
BEGIN
  SELECT COUNT(*) INTO TEMP FROM USER_TABLES WHERE TABLE_NAME = 'KSEN_STATE_CHG_CNSTRNT';
	IF TEMP > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KSEN_STATE_CHG_CNSTRNT CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KSEN_STATE_CHG_CNSTRNT
(
	STATE_CHG_ID         VARCHAR2(255) NOT NULL ,
	STATE_CNSTRNT_ID     VARCHAR2(255) NOT NULL
)
/

DECLARE TEMP NUMBER;
BEGIN
  SELECT COUNT(*) INTO TEMP FROM USER_TABLES WHERE TABLE_NAME = 'KSEN_STATE_CHG_PROPAGT';
	IF TEMP > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KSEN_STATE_CHG_PROPAGT CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KSEN_STATE_CHG_PROPAGT
(
	STATE_CHG_ID         VARCHAR2(255) NOT NULL ,
	STATE_PROPAGT_ID     VARCHAR2(255) NOT NULL
)
/

DECLARE TEMP NUMBER;
BEGIN
  SELECT COUNT(*) INTO TEMP FROM USER_TABLES WHERE TABLE_NAME = 'KSEN_STATE_PROPAGT';
	IF TEMP > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KSEN_STATE_PROPAGT CASCADE CONSTRAINTS PURGE'; END IF;
END;
/


CREATE TABLE KSEN_STATE_PROPAGT
(
	ID                   VARCHAR2(255) NOT NULL ,
	OBJ_ID               VARCHAR2(36) NULL ,
	STATE_PROPAGT_TYPE   VARCHAR2(255) NOT NULL ,
	STATE_PROPAGT_STATE  VARCHAR2(255) NOT NULL ,
	VER_NBR              NUMBER(19) NOT NULL ,
	CREATETIME           TIMESTAMP(6) NOT NULL ,
	CREATEID             VARCHAR2(255) NOT NULL ,
	UPDATETIME           TIMESTAMP(6) NULL ,
	UPDATEID             VARCHAR2(255) NULL ,
	TARGET_STATE_CHG_ID  VARCHAR2(255) NOT NULL
)
/

DECLARE TEMP NUMBER;
BEGIN
  SELECT COUNT(*) INTO TEMP FROM USER_TABLES WHERE TABLE_NAME = 'KSEN_STATE_PROPAGT_ATTR';
	IF TEMP > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KSEN_STATE_PROPAGT_ATTR CASCADE CONSTRAINTS PURGE'; END IF;
END;
/


CREATE TABLE KSEN_STATE_PROPAGT_ATTR
(
	ID                   VARCHAR2(255) NOT NULL ,
	OBJ_ID               VARCHAR2(36) NULL ,
	ATTR_KEY             VARCHAR2(255) NULL ,
	ATTR_VALUE           VARCHAR2(4000) NULL ,
	OWNER_ID             VARCHAR2(255) NULL
)
/


DECLARE TEMP NUMBER;
BEGIN
  SELECT COUNT(*) INTO TEMP FROM USER_TABLES WHERE TABLE_NAME = 'KSEN_STATE_PROPAGT_CNSTRNT';
	IF TEMP > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KSEN_STATE_PROPAGT_CNSTRNT CASCADE CONSTRAINTS PURGE'; END IF;
END;
/


CREATE TABLE KSEN_STATE_PROPAGT_CNSTRNT
(
	STATE_PROPAGT_ID     VARCHAR2(255) NOT NULL ,
	STATE_CNSTRNT_ID     VARCHAR2(255) NOT NULL
)
/

DECLARE TEMP NUMBER;
BEGIN
  SELECT COUNT(*) INTO TEMP FROM USER_TABLES WHERE TABLE_NAME = 'KSEN_STATE_CNSTRNT';
	IF TEMP > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KSEN_STATE_CNSTRNT CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KSEN_STATE_CNSTRNT
(
	ID                   VARCHAR2(255) NOT NULL ,
	OBJ_ID               VARCHAR2(36) NULL ,
	STATE_CNSTRNT_TYPE   VARCHAR2(255) NOT NULL ,
	STATE_CNSTRNT_STATE  VARCHAR2(255) NOT NULL ,
	STATE_CNSTRNT_OPERATOR VARCHAR2(255) NOT NULL ,
	AGENDA_ID            VARCHAR2(255) NULL ,
	VER_NBR              NUMBER(19) NOT NULL ,
	CREATETIME           TIMESTAMP(6) NOT NULL ,
	CREATEID             VARCHAR2(255) NOT NULL ,
	UPDATETIME           TIMESTAMP(6) NULL ,
	UPDATEID             VARCHAR2(255) NULL
)
/


ALTER TABLE KSEN_STATE_CNSTRNT
	ADD CONSTRAINT  KSEN_STATE_CNSTRNT_P PRIMARY KEY (ID)
/

DECLARE TEMP NUMBER;
BEGIN
  SELECT COUNT(*) INTO TEMP FROM USER_TABLES WHERE TABLE_NAME = 'KSEN_STATE_CNSTRNT_ATTR';
	IF TEMP > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KSEN_STATE_CNSTRNT_ATTR CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KSEN_STATE_CNSTRNT_ATTR
(
	ID                   VARCHAR2(255) NOT NULL ,
	OBJ_ID               VARCHAR2(36) NULL ,
	ATTR_KEY             VARCHAR2(255) NULL ,
	ATTR_VALUE           VARCHAR2(4000) NULL ,
	OWNER_ID             VARCHAR2(255) NULL
)
/

ALTER TABLE KSEN_STATE_CNSTRNT_ATTR
	ADD CONSTRAINT  KSEN_STATE_CNSTRNT_ATTR_P PRIMARY KEY (ID)
/

DECLARE TEMP NUMBER;
BEGIN
  SELECT COUNT(*) INTO TEMP FROM USER_TABLES WHERE TABLE_NAME = 'KSEN_STATE_CNSTRNT_ROS';
	IF TEMP > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE KSEN_STATE_CNSTRNT_ROS CASCADE CONSTRAINTS PURGE'; END IF;
END;
/

CREATE TABLE KSEN_STATE_CNSTRNT_ROS
(
	STATE_CNSTRNT_ID     VARCHAR2(255) NOT NULL ,
	REL_OBJ_STATE_ID     VARCHAR2(255) NOT NULL
)
/


ALTER TABLE KSEN_STATE_CNSTRNT_ROS
	ADD CONSTRAINT  KSEN_STATE_CNSTRNT_ROS_P PRIMARY KEY (STATE_CNSTRNT_ID,REL_OBJ_STATE_ID)
/

