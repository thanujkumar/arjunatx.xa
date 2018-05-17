create user testdb1 identified by testdb default tablespace app_data temporary tablespace temp quota unlimited on app_data;

grant create session, create table, create view, create sequence, create procedure, create type, create trigger, create synonym to testdb1;

#-- Another db

create user testdb2 identified by testdb default tablespace app_data temporary tablespace temp quota unlimited on app_data;

grant create session, create table, create view, create sequence, create procedure, create type, create trigger, create synonym to testdb2;

#-- Table 

   CREATE TABLE "TESTDB1"."TEST_TX1" 
   ("ID" VARCHAR2(20 BYTE) NOT NULL ENABLE, 
	"NAME" VARCHAR2(20 BYTE) NOT NULL ENABLE, 
	"AGE" NUMBER NOT NULL ENABLE
   ) SEGMENT CREATION DEFERRED 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  TABLESPACE "APP_DATA" ;
  
    CREATE TABLE "TESTDB2"."TEST_TX1" 
   ("ID" VARCHAR2(20 BYTE) NOT NULL ENABLE, 
	"NAME" VARCHAR2(20 BYTE) NOT NULL ENABLE, 
	"AGE" NUMBER NOT NULL ENABLE
   ) SEGMENT CREATION DEFERRED 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  TABLESPACE "APP_DATA" ;