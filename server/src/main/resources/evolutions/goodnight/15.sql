-- Adjust table story to new data model.

-- !Ups

alter table story
drop column start_location;

alter table story
drop constraint story_fk_users_user;

alter table story
alter column creator type text;

update story
set creator = (select name from "user" limit 1);

alter table story
add constraint story_fk_creator_user_name
  foreign key(creator) references "user"(name)
  on delete cascade
  on update cascade;


-- !Downs

alter table story
add column start_location UUID null;

alter table story
drop constraint story_fk_creator_user_name;

update story
set creator = (select id from "user" limit 1);

alter table story
alter column creator type UUID using creator::uuid;

alter table story
add constraint story_fk_users_user
  foreign key(creator) references "user"(id)
  on delete cascade
  on update cascade;
