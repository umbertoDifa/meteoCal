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
	
	(3,"umberto.difabrizio@gmail.com","umberto","S8gCjTiPz7Sl4tcxfRQN65t0VSdnS0mtX/Epbr1ysm8=$mxZletu36s2wvJ/mEkALO+vAJ9NJAakViHisVBYEX14=","di fabrizio","M"),
	(2,"v.ceriani92@gmail.com","valentina","M6Mvs07YcBeVfL5FAklM2YgtAAo06uYPkDcH3brEAiw=$GBNh7ArSM8SWBzenn/vnFfwEfUZu/T/2UZBthEmM+iU=","ceriani","F"),
	(1,"angelo.francesco.mobile@gmail.com","francesco","FxPoix2AwVINboc/xiR4EhUX1XgG3wKSj9KqQiKrS+4=$7X6rvTmIfdwyE7hYR6nwfBV+eu3h+fC7mEvmYrWsoz8=","angelo","M");

insert into CALENDAR (TITLE, ISDEFAULT, ISPUBLIC, OWNER_ID) 
    values 
    ("Default", 1, 0, 1),
    ("Default", 1, 0, 2),
    ("Default", 1, 0, 3),
      ("Public_Cal", 0, 1, 1),
    ("Public_Cal", 0, 1, 2),
    ("Public_Cal", 0, 1, 3);

insert into EVENT (ID, TITLE,STARTDATETIME, ENDDATETIME, ISOUTDOOR, LOCATION, LATITUDE, LONGITUDE, HASLOCATION, DESCRIPTION, OWNER_ID, TYPE)
	values

	
	(1,  "Trip to Dubai",'2015-02-20 12:00','2015-02-27 13:00', 1 , "Dubai,Emirati Arabi Uniti,Dubai", 25.2048493 , 55.2707828, 1, NULL, 2, "PRIVATE"),
	(2, "Birthday Party!", '2015-01-27 21:00', '2015-01-28 02:00', 1 , "Milano,Italia,Lombardia",45.4654219, 9.1859243, 1, "Hello everybody! you are all invited to my birthday party! Please confirm your presence ", 1, "PRIVATE"),
	(3, "Conference Call",  '2015-02-02 13:00', '2015-02-02 15:00', 0 , "Office", 0.0,0.0, 0,"Conference call with customers", 3, "PRIVATE"),
	(4, "Conference",'2015-01-02 12:00', '2015-01-02 13:00', 0 , "office",  0.0, 0.0, 0, "Conference with Managers", 1, "PRIVATE"),
	(5, "Conference Call", '2015-01-25 09:30', '2015-01-25 11:00', 0 , " ", 0.0, 0.0, 0, "Conference call with customers", 2, "PRIVATE"),
	(6, "Deadline Project ",'2015-01-25 23:59', '2015-01-26 00:00', 0 , " ", 0.0, 0.0, 0,  " Upload project and documentation", 3, "PRIVATE"),
        (7, "Bon Jovi Concert",'2015-02-23 17:30', '2015-02-23 23:30', 1 , "Milano,Italia,Lombardia",45.4654219, 9.1859243, 1, 'SETLIST: 

That is What the Water Made Me
You Give Love a Bad Name
Raise Your Hands 
Runaway 
Lost Highway 
Born to Be My Baby 
It''s My Life 
Because We Can 
We Weren''t Born to Follow 
Someday I''ll Be Saturday Night 
Love''s the Only Rule 
Wanted Dead or Alive 
Have a Nice Day 
Livin'' on a Prayer 
Never Say Goodbye 
Always ', 2 ,"PUBLIC"),
        (8, "Pillow fight", '2015-02-02 13:00', '2015-02-02 16:00',  1 , "Milano,Italia,Lombardia",45.4654219, 9.1859243, 1, "Dear Dudes and Dudettes, 
you are all welcome to actively participate in this international event. check out the FB event to confirm your participation! 
Pillow Fight Milano in Pajama! All over the world, groups like us organize free, fun, all ages, non-commercial public events. 
At 3pm there will be Pillow Fights in many major cities of the world. Check out the main website below and scroll to your town to see if there is one there.  
Here the International link.. take a look on the Milano page: http://www.pillowfightday.com/ ", 1,  "PUBLIC"),
        (9, "FUORISALONE 2015 & SALONE DEL MOBILE " , '2015-04-14 09:00', '2015-04-19 23:59', 0 , "Milano,Italia,Lombardia",45.4654219, 9.1859243, 1, "Fuorisalone or design week today is the most important event in the world related to the topic of design, the term is used to define the set of events and exhibitions that animate the entire city of Milan in the period of April in correspondence of the Salone del Mobile that takes place in exhibition center in Rho", 3,  "PUBLIC"); 
     

insert into PRIVATE_EVENT (ID)
values
(1),(2),(3),(4),(5),(6);

insert into PUBLIC_EVENT (ID)
values
(7), (8),(9);

insert into EVENT_IN_CALENDAR (eventsInCalendar_ID, TITLE, OWNER_ID)
     values
     
     (1, "Default", 2),
     (2, "Default", 1),
     (3, "Default", 3),
     (4, "Default", 1),
     (5, "Default", 2),
     (6, "Default", 3),
     (7, "Public_Cal", 2),
     (8, "Public_Cal", 1),
     (9, "Public_Cal", 3);


insert into INVITATION (INVITEE_ID, EVENT_ID, ANSWER)
        values
        (3,2,"NA"),        
        (2,2,"NA");

insert into NOTIFICATION (ID, TITLE, MESSAGE, `TYPE`, ISREAD, RECIPIENT_ID, RELATEDEVENT_ID)
    values 
    (1, "Invitation for Birthday Party!", "You have received an invitation for event Birthday Party! from angelo.francesco.mobile@gmail.com. Check it out!", "INVITATION", 0, 2,2),
    (2, "Invitation for Birthday Party!", "You have received an invitation for event Birthday Party! from angelo.francesco.mobile@gmail.com. Check it out!", "INVITATION", 0, 3,2);


UPDATE SEQUENCE
SET SEQ_COUNT = 9
WHERE SEQ_NAME = 'EVENT_SEQ';

UPDATE SEQUENCE
SET SEQ_COUNT = 3
WHERE SEQ_NAME = 'USER_SEQ';

UPDATE SEQUENCE
SET SEQ_COUNT = 2
WHERE SEQ_NAME = 'NOTIF_SEQ';
