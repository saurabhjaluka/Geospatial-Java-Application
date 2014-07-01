create table building(
id varchar(20),
name varchar(30),
no_points number(3),
shape SDO_GEOMETRY);

create table announcement_system(
id varchar(20),
shape SDO_GEOMETRY,
center SDO_GEOMETRY);

create table student(
id varchar(20),
location SDO_GEOMETRY);

INSERT INTO user_sdo_geom_metadata(TABLE_NAME,COLUMN_NAME,DIMINFO,SRID) VALUES ('building','shape',SDO_DIM_ARRAY(SDO_DIM_ELEMENT('X', 0, 820, 0.005),SDO_DIM_ELEMENT('Y', 0, 580, 0.005)),NULL);

INSERT INTO user_sdo_geom_metadata(TABLE_NAME,COLUMN_NAME,DIMINFO,SRID) VALUES ('announcement_system','shape',SDO_DIM_ARRAY(SDO_DIM_ELEMENT('X', 0, 820, 0.005),SDO_DIM_ELEMENT('Y', 0, 580, 0.005)),NULL);

INSERT INTO user_sdo_geom_metadata(TABLE_NAME,COLUMN_NAME,DIMINFO,SRID) VALUES ('announcement_system','center',SDO_DIM_ARRAY(SDO_DIM_ELEMENT('X', 0, 820, 0.005),SDO_DIM_ELEMENT('Y', 0, 580, 0.005)),NULL);


INSERT INTO user_sdo_geom_metadata(TABLE_NAME,COLUMN_NAME,DIMINFO,SRID) VALUES ('student','location',SDO_DIM_ARRAY(SDO_DIM_ELEMENT('X', 0, 820, 0.005),SDO_DIM_ELEMENT('Y', 0, 580, 0.005)),NULL);

CREATE INDEX building_index
   ON building(shape)
   INDEXTYPE IS MDSYS.SPATIAL_INDEX;
   
CREATE INDEX announcementsystem_index
   ON announcement_system(shape)
   INDEXTYPE IS MDSYS.SPATIAL_INDEX;

CREATE INDEX announcementsystem_centerindex
   ON announcement_system(center)
   INDEXTYPE IS MDSYS.SPATIAL_INDEX;
   
CREATE INDEX student_index
   ON student(location)
   INDEXTYPE IS MDSYS.SPATIAL_INDEX;

commit;