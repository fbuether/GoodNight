-- extend story table with a column for urlname, and add a index on that.

-- !Ups

alter table story add column urlname text null;

update story
set urlname = name;

alter table story alter column urlname set not null;

create unique index story_index_urlname on story (urlname);

-- !Downs

drop index if exists story_index_urlname;

alter table story drop column if exists urlname;
