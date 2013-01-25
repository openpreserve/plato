insert into IdpRole (id, roleName) values (0, 'admin');
insert into IdpRole (id, roleName) values (1, 'authenticated');
update hibernate_sequence set next_val=2;