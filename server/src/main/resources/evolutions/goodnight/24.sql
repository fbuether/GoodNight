-- Qualities have a sort now.

-- !Ups

create type sort as enum('bool', 'int');

alter table quality
add column sort sort;

update quality
set sort = 'bool';

alter table quality
alter column sort set not null;

-- !Downs

alter table quality
drop column sort;

drop type sort;
