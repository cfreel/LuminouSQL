# LuminouSQL


## Summary
This is a work-in-progress attempt to create a usable DB browser that works at command line interfaces.  It utilizes 
Java's JDBC interface for DB drivers and the excellent [Lanterna framework](https://github.com/mabe02/lanterna).

## Purpose
Having spent more time than I care to reflect on at a terminal window troubleshooting production problems,
I finally decided to build the util I wanted so many times over the years: something that could work with the DB 
with no more setup than a single jar file and something to point to the JDBC driver the application already has
deployed with it.

A further goal is to have a DB browser that is more keyboard-friendly than the ones I've worked with.  
I'm initially pushing most features to be accessed through the menus, but I think it might be pretty quick 
once you make the adjustment to that style of working.  I'll look for better ways of organizing navigation 
to features as time goes on and I road test it a bit.



## Running
Built with Maven 3.9.2 and using only Lanterna 3.2.1.  Seems to work for Java 8, 17 and 21 on Linux from some
very minimal trials.  Since I can't be the only one who still has applications on old version of Java out there, 
I will prioritize keeping compatibility to Java 8.  If Lanterna doesn't have a need for newer, then I don't think 
I should.


## Choices
Hey, we all have to make some; if you think I'm way off base on any, feel free to let me know.  That said...

I will start with dark mode, then add an option to change that as time permits.  I think most operations should
work just with a valid JDBC driver, but I'll test MySQL/MariaDB, Postgres, then DB2 (I'll give you one guess why 
that's ahead of things like Oracle and SQLServer... :^) )

## License
LGPL, which follow's Lanterna's license.

## Contributions
Gratefully accepted, including functionality requests; my first time running an OS project, so we can just use 
GitHub issues for now.

Be aware that my time may be limited and fixes/enhancements are best effort; thanks for understanding.

## Wish List
Some easy, some hard...

* Save to File
* Bulk load via a SQL script
* Copy tables from one DB to another (ideally cross vendor)
* Create scripts for creating tables structures and populating
* Support for SQLite
* Mongo?
* Redis?
