-- Create a new DB and account for the Hotel Booking System (HBS)
create database hbsdb;
create user hbsadm with password 'mdasbh!';
grant all privileges on database hbsdb to hbsadm;