-- Qualities have a sort, but it's easier as string.

-- !Ups

alter table quality
rename column sort to sortold;

alter table quality
add column sort text null;

update quality
set sort = sortold;

alter table quality
alter column sort set not null;

alter table quality
drop column sortold;

drop type sort;

-- !Downs


alter table quality
rename column sort to sortnew;

create type sort as enum('bool', 'int');

alter table quality
add column sort sort;

update quality
set sort = 'int'
where sortnew = 'int';

update quality
set sort = 'bool'
where sort is null;

alter table quality
alter column sort set not null;

alter table quality
drop column sortnew;
