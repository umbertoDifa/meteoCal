#import.sql file
DELETE FROM EVENT_IN_CALENDAR;
DELETE FROM INVITATION;
DELETE FROM NOTIFICATION;
DELETE FROM PUBLIC_EVENT;
DELETE FROM PRIVATE_EVENT;
DELETE FROM CALENDAR;
DELETE FROM EVENT;
DELETE FROM USER;


insert into USER (ID,EMAIL,NAME,PASSWORD,SURNAME,GENDER)
	values
	(1,"a@a","a","a","a",NULL),
	(2,"b@b","b","b","b",NULL),
	(3,"c@c","c","c","c",NULL),
	(4,"umbo@asp","umbo","umbo","difa","M"),
	(5,"vale@figa","vale","vale","cer","F"),
	(6,"fra@ang","fra","fra","ang","M"),
	(7,"admin@admin","admin","admin","admin", NULL);

insert into CALENDAR (TITLE, ISDEFAULT, ISPUBLIC, OWNER_ID) 
    values 
    ("Deafult", 1, 0, 1),
    ("Deafult", 1, 0, 2),
    ("Deafult", 1, 0, 3),
    ("Deafult", 1, 0, 4),
    ("Deafult", 1, 0, 5),
    ("Deafult", 1, 0, 6),
    ("Deafult", 1, 0, 7),
    ("Public_Cal", 0, 1, 1),
    ("Public_Cal", 0, 1, 2),
    ("Public_Cal", 0, 1, 3),
    ("Public_Cal", 0, 1, 4),
    ("Public_Cal", 0, 1, 5),
    ("Public_Cal", 0, 1, 6),
    ("Public_Cal", 0, 1, 7);


insert into EVENT (ID, DESCRIPTION, ENDDATETIME, ISOUTDOOR, LOCATION, STARTDATETIME, TITLE, OWNER_ID, IMGPATH, TYPE)
	values
	(1, "Evento Privato di amdin", '2014-12-22 01:30', 1 , "a casa mia", '2014-12-22 00:30', "Private Event of admin", 7, "/img/Event/1.jpg", "PRIVATE"),
	(2, "Evento Privato di fra", '2015-01-02 13:00', 1 , "a casa mia", '2015-01-02 12:00', "Private Event of fra", 6, "/img/Event/2.jpg", "PRIVATE"),
	(3, "Evento Privato di vale", '2015-01-04 13:00', 1 , "a casa mia", '2015-01-04 12:00', "Private Event of vale", 5, "/img/Event/3.jpg", "PRIVATE"),
	(4, "Evento Privato di umbo", '2015-02-04 15:00', 1 , "a casa mia", '2015-02-02 13:00', "Private Event of umbo", 4, "/img/Event/4.jpg", "PRIVATE"),
	(5, "Evento Privato di c", '2015-01-02 13:00', 1 , "a casa mia", '2015-01-02 12:00', "Private Event of c", 3, "/img/Event/5.jpg", "PRIVATE"),
	(6, "Evento Privato di b", '2015-01-03 11:00', 1 , "a casa mia", '2015-01-03 09:30', "Private Event of b", 2, "/img/Event/6.jpg", "PRIVATE"),
	(7, "Evento Privato di a", '2015-01-04 22:00', 1 , "a casa mia", '2015-01-04 20:00', "Private Event of a", 1, "/img/Event/7.jpg", "PRIVATE");

insert into PRIVATE_EVENT (ID)
values
(1),(2),(3),(4),(5),(6),(7);

insert into EVENT (ID, DESCRIPTION, ENDDATETIME, ISOUTDOOR, LOCATION, STARTDATETIME, TITLE, OWNER_ID, IMGPATH, TYPE)
	values
	(8, "Evento Pubblico di a", '2014-12-22 01:30', 1 , "a casa mia", '2014-12-22 00:30', "Public Event of a", 1, "/img/Event/8.jpg", "PUBLIC"),
	(9, "Evento Pubblico di b", '2015-01-02 13:00', 1 , "a casa mia", '2015-01-02 12:00', "Public Event of b", 2, "/img/Event/9.jpg", "PUBLIC"),
	(10, "Evento Pubblico di c", '2015-01-04 13:00', 1 , "a casa mia", '2015-01-04 12:00', "Public Event of c", 3, "/img/Event/10.jpg", "PUBLIC"),
	(11, "Evento Pubblico di umbo", '2015-01-04 15:00', 1 , "a casa mia", '2015-01-02 13:00', "Public Event of umbo", 4, "/img/Event/11.jpg", "PUBLIC"),
	(12, "Evento Pubblico di vale", '2015-01-02 13:00', 1 , "a casa mia", '2015-01-02 12:00', "Public Event of vale", 5, "/img/Event/12.jpg", "PUBLIC"),
	(13, "Evento Pubblico di fra", '2015-01-03 11:00', 1 , "a casa mia", '2015-01-03 09:30', "Public Event of fra", 6, "/img/Event/13.jpg", "PUBLIC"),
	(14, "Evento Pubblico di admin", '2015-01-04 22:00', 1 , "a casa mia", '2015-01-04 20:00', "Public Event of admin", 7, "/img/Event/14.jpg", "PUBLIC");

insert into PUBLIC_EVENT (ID)
values
(8),(9),(10),(11),(12),(13),(14);

insert into EVENT_IN_CALENDAR (eventsInCalendar_ID, TITLE, OWNER_ID)
	values
	(1, "Deafult", 7),
	(2, "Deafult", 6),
	(3, "Deafult", 5),
	(4, "Deafult", 4),
	(5, "Deafult", 3),
	(6, "Deafult", 2),
	(7, "Deafult", 1),
	(8, "Public_Cal", 1),
	(9, "Public_Cal", 2),
	(10, "Public_Cal", 3),
	(11, "Public_Cal", 4),
	(12, "Public_Cal", 5),
	(12, "Public_Cal", 6),
	(14, "Public_Cal", 7);

insert into INVITATION (INVITEE_ID, EVENT_ID, ANSWER)
        values
        (1,8,"YES");

insert into NOTIFICATION (ID,TYPE, RECIPIENT_ID, RELATEDEVENT_ID)
    values 
    (1,1,1,1);

UPDATE SEQUENCE
SET SEQ_COUNT = 14
WHERE SEQ_NAME = 'EVENT_SEQ';

UPDATE SEQUENCE
SET SEQ_COUNT = 7
WHERE SEQ_NAME = 'USER_SEQ';

      