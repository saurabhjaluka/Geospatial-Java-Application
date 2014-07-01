Name : Saurabh Jaluka
USC ID : 1212768133
USERNAME : jaluka

List of submitted files:

1.hw2.zip
 ->populate.java
   How to run: (change directory path in cmd to the folder where it contains classes111.jar,populate.java and all .xy files)
	-> First create the database using createdb.sql
	-> javac -cp classes111.jar;. populate.java
	-> java -cp classes111.jar;. populate buildings.xy students.xy announcementSystems.xy

 ->hw2.java
 ->database.java
 ->MainPanel.java
 ->createdb.sql
 ->drop.sql
 ->readme.txt

How to run: (change directory path in cmd to the folder where it contains classes111.jar,sdoapi.zip,HW2.java,database.java,MainPanel.java,map.jpg)
		(Note that folder should contain map.jpg)
	->javac -Xlint:unchecked -cp classes111.jar;sdoapi.zip;. *.java
	->java -cp classes111.jar;sdoapi.zip;. HW2


Description:

Tables:
	student - It has ID (primary key)varchar,location (sdo_geometry)
	building - ID(primary key)varchar, name (varchar), no_of_points (number),shape (sdo_geometry)
	announcement_system - ID (Primary key)varchar, Shape(sdo_geometry),center(sdo_geometry)

HW2.java - It contains the main function where I have made an object of frame and set its title to my name and ID. I have added the MainPanel object on this frame.
MainPanel.java - 
	->It contains the GUI part, and all the action event of the input. 
	->It has a imagePanel which contains the image in a label. 
	->Each input has a particular action event. Which process its function. Like I want to clear the image when I check on the radiobutton, so it has a 
	  imagePanel.repaint() method which does that.
	->The submitQuery event is called on the Submit button click. Here it checks for the validation whether checkbox or radio button is seleted or not. Then it 
	  will look for the particular radioButton (Query type) , eg. if it is whole Region then it will set the query="select location from table" and then execute
	  it and draw the following feature. Similarly all the cases are handled there.
	->It has a paintComponentX methods that execute the query passed to it, get the shape of the feature and draws it on the imagePanel.
	->It has a imagePanel mouse click event, which is called user wants to select a point on image or draw a region.
Database.java-
	->It contains getConnection function that establishes database connection.
	->It has a function that takes a query string executes it and returns a resultset object.
	->It has a getID function that returns the value of ID attribute of the requested query. And it will always have one row, as the query has no. of result as
	  one in the spatial query.
Populate.java->
	(Note table should be created first to execute this file.)
	->This file takes 3 arguments from the conmmand line (buildings.xy students.xy announcementSystems.xy)
	->It reads the input file based on the delimiter and then inserts the data into respective tables.
createsql.db -
	->It contains the create table query, inserts the meta information into the user_sdo_geom_metadata table and create the index on sdo_geometry object.
Drop.db -
	->Delete data from the table, delete meta information from user_sdo_geom_metadata and delete created indexes for the sdo_geometry object, and finally drop
	  the tables.
