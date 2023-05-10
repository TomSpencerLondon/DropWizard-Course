--  Lock Database
UPDATE DATABASECHANGELOGLOCK SET `LOCKED` = 1, LOCKEDBY = 'tom-ubuntu (192.168.49.1)', LOCKGRANTED = NOW() WHERE ID = 1 AND `LOCKED` = 0;

--  *********************************************************************
--  Rollback 1 Change(s) Script
--  *********************************************************************
--  Change Log: migrations.xml
--  Ran at: 10/05/2023, 15:13
--  Against: root@localhost@jdbc:mysql://localhost:3306/DropBookmarks
--  Liquibase version: 4.19.0
--  *********************************************************************

--  Rolling Back ChangeSet: migrations.xml::3::tom
DELETE FROM users WHERE id=1;

DELETE FROM DATABASECHANGELOG WHERE ID = '3' AND AUTHOR = 'tom' AND FILENAME = 'migrations.xml';

--  Release Database Lock
UPDATE DATABASECHANGELOGLOCK SET `LOCKED` = 0, LOCKEDBY = NULL, LOCKGRANTED = NULL WHERE ID = 1;

