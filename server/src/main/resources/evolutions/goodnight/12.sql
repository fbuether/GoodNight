-- choices are going to have titles, too!

-- !Ups

alter table choice add column title text null;
update choice set title = '';
alter table choice alter column title set not null;

alter table choice add column urlname text null;
update choice set urlname = '';
alter table choice alter column urlname set not null;

-- !Downs

alter table choice drop column title;
alter table choice drop column urlname;
