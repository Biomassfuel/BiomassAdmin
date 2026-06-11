-- ----------------------------
-- Public read permissions for the room reservation module.
-- Use after older space_reservation_20260609.sql / space_public_reservation_menu_20260611.sql deployments.
-- ----------------------------

update sys_menu
   set perms = 'space:reservationItem:publicList',
       update_by = 'admin',
       update_time = sysdate(),
       remark = 'public readonly entry for room reservation'
 where menu_id in (2011, 2016);

update sys_menu
   set perms = 'space:reservationItem:publicList',
       update_by = 'admin',
       update_time = sysdate(),
       remark = 'public room options for reservation pages'
 where menu_id = 2054;

update sys_menu
   set perms = 'space:reservationItem:publicList',
       update_by = 'admin',
       update_time = sysdate(),
       remark = 'public time period options for reservation and occupancy pages'
 where menu_id = 2055;

update sys_menu
   set perms = 'space:reservationItem:publicList',
       update_by = 'admin',
       update_time = sysdate(),
       remark = 'public room detail for occupancy page'
 where menu_id = 2056;

delete from sys_role_menu
 where role_id in (select role_id from sys_role where role_key = 'space_teacher')
   and menu_id between 2000 and 2099
   and menu_id not in (2010, 2011, 2012, 2013, 2014, 2015, 2016, 2017, 2046, 2048, 2054, 2055, 2056, 2057, 2061);

delete from sys_role_menu
 where role_id in (select role_id from sys_role where role_key = 'space_student')
   and menu_id between 2000 and 2099
   and menu_id not in (2010, 2011, 2016, 2017, 2054, 2055, 2056, 2057, 2061);

insert into sys_role_menu(role_id, menu_id)
select r.role_id, m.menu_id
  from sys_role r
  join sys_menu m on m.menu_id in (2010, 2011, 2012, 2013, 2014, 2015, 2016, 2017, 2046, 2048, 2054, 2055, 2056, 2057, 2061)
 where r.role_key = 'space_teacher'
   and not exists (
     select 1 from sys_role_menu srm where srm.role_id = r.role_id and srm.menu_id = m.menu_id
   );

insert into sys_role_menu(role_id, menu_id)
select r.role_id, m.menu_id
  from sys_role r
  join sys_menu m on m.menu_id in (2010, 2011, 2016, 2017, 2054, 2055, 2056, 2057, 2061)
 where r.role_key = 'space_student'
   and not exists (
     select 1 from sys_role_menu srm where srm.role_id = r.role_id and srm.menu_id = m.menu_id
   );
