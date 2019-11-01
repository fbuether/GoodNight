-- locations for scenes might not need to be required. also, urlnames.

-- !Ups

alter table scene alter column location drop not null;

alter table scene add column urlname text null;

update scene
set urlname = '';

alter table scene alter column urlname set not null;

-- !Downs

delete from scene where location is null;

alter table scene alter column location set not null;

alter table scene drop column urlname;
