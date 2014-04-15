DROP TABLE IF EXISTS TRIGGER_SCHEDULE_ATTRIBUTE;
DROP TABLE IF EXISTS TRIGGER_SCHEDULE;

CREATE TABLE TRIGGER_SCHEDULE
(
  ID       				INT AUTO_INCREMENT NOT NULL,
  PROCESSOR	  			VARCHAR(128),
  GROUP_NAME	    	VARCHAR(64),
  SCHEDULE_NAME   		VARCHAR(64),
  CRON_EXPRESSION   	VARCHAR(64),
  START_TIME			DATETIME,
  END_TIME				DATETIME,
  START_DELAY       	INT,
  END_DELAY				INT,
  DELAY_UNIT			VARCHAR(16),
  REPEAT_COUNT			INT,
  REPEAT_TIME	    	INT,
  REPEAT_UNIT       	VARCHAR(16),
  MISFIRE_INSTRUCTION	INT,
  NAMESPACE				VARCHAR(64),
  LOCK_INSTANCE			VARCHAR(64),
  MASTER_TIME			DATETIME,
  UPDATED_AT			DATETIME,
  GMT_CREATE			DATETIME,
  GMT_MODIFIED			DATETIME,
  PRIMARY KEY (ID)
);

CREATE TABLE TRIGGER_SCHEDULE_ATTRIBUTE
(
  SCHEDULE_ID   INT                         NOT NULL,
  NAME          VARCHAR(64)               	NOT NULL,
  VALUE         VARCHAR(1024)
);

ALTER TABLE TRIGGER_SCHEDULE_ATTRIBUTE
   ADD CONSTRAINT FK_TRIGGER_SCHEDULE_ID FOREIGN KEY (SCHEDULE_ID)
      REFERENCES TRIGGER_SCHEDULE (ID);
