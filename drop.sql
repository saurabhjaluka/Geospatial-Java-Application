delete from student;
delete from announcement_system;
delete from building;
delete from mdsys.user_sdo_geom_metadata where table_name like 'BUILDING';
delete from mdsys.user_sdo_geom_metadata where table_name like 'ANNOUNCEMENT_SYSTEM';
delete from mdsys.user_sdo_geom_metadata where table_name like 'STUDENT';
drop INDEX building_index;
drop INDEX announcementsystem_index;
drop INDEX announcementsystem_centerindex;
drop INDEX student_index;


drop table student;
drop table announcement_system;
drop table building;

commit;