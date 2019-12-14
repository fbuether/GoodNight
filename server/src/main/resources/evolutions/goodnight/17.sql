-- Adopt player table to new structure.

-- !Ups

alter table player
drop constraint player_fk_users_user;

alter table player
drop constraint player_fk_story_story;

alter table player
rename column user_id to "user";

alter table player
alter column "user" type text;

alter table player
alter column story type text;

alter table player
rename column player_name to "name";

alter table player
drop column location;

update player
set "user" = "user"."name"
from "user"
where uuid(player."user") = "user".id;

alter table player
add constraint player_fk_user_user_name
  foreign key("user") references "user"("name")
  on delete cascade
  on update cascade;

update player
set story = story.urlname
from story
where uuid(player.story) = story.id;

alter table player
add constraint player_fk_story_story_urlname
  foreign key(story) references story(urlname)
  on delete cascade
  on update cascade;


-- !Downs

alter table player
drop constraint player_fk_user_user_name;

alter table player
drop constraint player_fk_story_story_urlname;

alter table player
add column location UUID null;

update player
set story = story.id
from story
where player.story = story.urlname;

alter table player
alter column story type UUID using story::uuid;

alter table player
rename column "name" to player_name;

update player
set "user" = "user".id
from "user"
where player."user" = "user"."name";

alter table player
alter column "user" type UUID using "user"::uuid;

alter table player
rename column "user" to user_id;

alter table player
add constraint player_fk_users_user
    foreign key(user_id) references "user"(id)
    on delete cascade
    on update cascade;

alter table player
add constraint player_fk_story_story
    foreign key(story) references story(id)
    on delete cascade
    on update cascade;
