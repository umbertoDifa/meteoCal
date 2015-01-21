#import.sql file
-- disabilito i controlli sulle foreign key per cancellare senza problemi le tabelle

SET foreign_key_checks = 0; 

DELETE FROM USER;
DELETE FROM EVENT_IN_CALENDAR;
DELETE FROM INVITATION;
DELETE FROM NOTIFICATION;
DELETE FROM PUBLIC_EVENT;
DELETE FROM PRIVATE_EVENT;
DELETE FROM CALENDAR;
DELETE FROM EVENT;
DELETE FROM PUBLIC_JOIN;
DELETE FROM WEATHER_FORECAST;

-- e poi li riabilito
SET foreign_key_checks = 1;


insert into USER (ID,EMAIL,NAME,PASSWORD,SURNAME,GENDER)
	values
	
	(1,"umberto.difabrizio@gmail.com","umberto","S8gCjTiPz7Sl4tcxfRQN65t0VSdnS0mtX/Epbr1ysm8=$mxZletu36s2wvJ/mEkALO+vAJ9NJAakViHisVBYEX14=","di fabrizio","M"),
	(2,"v.ceriani92@gmail.com","valentina","M6Mvs07YcBeVfL5FAklM2YgtAAo06uYPkDcH3brEAiw=$GBNh7ArSM8SWBzenn/vnFfwEfUZu/T/2UZBthEmM+iU=","ceriani","F"),
	(3,"angelo.francesco.mobile@gmail.com","francesco","FxPoix2AwVINboc/xiR4EhUX1XgG3wKSj9KqQiKrS+4=$7X6rvTmIfdwyE7hYR6nwfBV+eu3h+fC7mEvmYrWsoz8=","angelo","M");

insert into CALENDAR (TITLE, ISDEFAULT, ISPUBLIC, OWNER_ID) 
    values 
    ("Default", 1, 0, 1),
    ("Default", 1, 0, 2),
    ("Default", 1, 0, 3),
      ("Public_Cal", 0, 1, 1),
    ("Public_Cal", 0, 1, 2),
    ("Public_Cal", 0, 1, 3);


insert into EVENT (ID, DESCRIPTION, ENDDATETIME, ISOUTDOOR, LOCATION, STARTDATETIME, TITLE, OWNER_ID, TYPE)
	values
	
	(2, "Evento Privato di fra", '2015-01-29 13:00', 1 , "a casa mia", '2015-01-02 12:00', "Private Event of fra", 3, "PRIVATE"),
	(3, "Evento Privato di vale", '2015-01-04 13:00', 1 , "a casa mia", '2015-01-04 12:00', "Private Event of vale", 2, "PRIVATE"),
	(4, "Evento Privato di umbo", '2015-02-04 15:00', 1 , "a casa mia", '2015-02-02 13:00', "Private Event of umbo", 1, "PRIVATE"),
	(5, "Evento Privato di c", '2015-01-02 13:00', 1 , "a casa mia", '2015-01-02 12:00', "Private Event of c", 3, "PRIVATE"),
	(6, "Evento Privato di b", '2015-01-25 11:00', 1 , "a casa mia", '2015-01-22 09:30', "Private Event of b", 2, "PRIVATE"),
	(7, "Evento Privato di a", '2015-01-26 22:00', 1 , "a casa mia", '2015-01-20 20:00', "Private Event of a", 1, "PRIVATE");

insert into PRIVATE_EVENT (ID)
values
(2),(3),(4),(5),(6),(7);

insert into EVENT (ID, DESCRIPTION, ENDDATETIME, ISOUTDOOR, LOCATION, STARTDATETIME, TITLE, OWNER_ID, TYPE)
	values
	(8, "Evento Pubblico di a", '2015-02-22 01:30', 1 , "a casa mia", '2015-02-22 00:30', "Public Event of a", 1, "PUBLIC"),
	(9, "Evento Pubblico di b", '2015-02-02 13:00', 1 , "a casa mia", '2015-02-02 12:00', "Public Event of b", 2,  "PUBLIC"),
	(10, "Evento Pubblico di c", '2015-02-04 13:00', 1 , "a casa mia", '2015-02-04 12:00', "Public Event of c", 3,  "PUBLIC"),
	(11, "Evento Pubblico di umbo", '2015-02-04 15:00', 1 , "a casa mia", '2015-02-02 13:00', "Public Event of umbo", 1,"PUBLIC"),
	(12, "Evento Pubblico di vale", '2015-02-02 13:00', 1 , "a casa mia", '2015-02-02 12:00', "Public Event of vale", 2, "PUBLIC"),
	(13, "Evento Pubblico di fra", '2015-02-03 11:00', 1 , "a casa mia", '2015-02-03 09:30', "Public Event of fra", 3, "PUBLIC");

insert into PUBLIC_EVENT (ID)
values
(8),(9),(10),(11),(12),(13);

insert into EVENT_IN_CALENDAR (eventsInCalendar_ID, TITLE, OWNER_ID)
	values
	
	(2, "Default", 3),
	(3, "Default", 2),
	(4, "Default", 1),
	(5, "Default", 3),
	(6, "Default", 2),
	(7, "Default", 1),
	(8, "Public_Cal", 1),
	(9, "Public_Cal", 2),
	(10, "Public_Cal", 3),
	(11, "Public_Cal", 1),
	(12, "Public_Cal", 2),
	(12, "Public_Cal", 3);

insert into INVITATION (INVITEE_ID, EVENT_ID, ANSWER)
        values
        (3,8,"YES"),        
        (2,8,"YES");


UPDATE SEQUENCE
SET SEQ_COUNT = 14
WHERE SEQ_NAME = 'EVENT_SEQ';

UPDATE SEQUENCE
SET SEQ_COUNT = 7
WHERE SEQ_NAME = 'USER_SEQ';

      