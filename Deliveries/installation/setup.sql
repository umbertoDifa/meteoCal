create database glassfishdb;
create user 'glassfish'@'localhost' identified by 'glassfish';
grant all on glassfishdb.* to 'glassfish'@'localhost';