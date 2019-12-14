-- Adopt scene table to new structure.

-- !Ups

alter table scene
drop constraint scene_fk_story_story;

alter table scene
alter column story type text;

update scene
set story = story.urlname
from story
where uuid(scene.story) = story.id;

alter table scene
add constraint scene_fk_story_story_urlname
  foreign key(story) references story(urlname)
  on delete cascade
  on update cascade;

alter table scene
rename column title to "name";

alter table scene
drop column location;

alter table scene
drop column mandatory;

-- !Downs

alter table scene
add column mandatory boolean not null default 'false';

alter table scene
add column location UUID;

alter table scene
rename column "name" to title;

alter table scene
drop constraint scene_fk_story_story_urlname;

update scene
set story = story.id
from story
where scene.story = story.urlname;

alter table scene
alter column story type UUID using story::uuid;

alter table scene
add constraint scene_fk_story_story
  foreign key(story) references story(id)
  on delete cascade
  on update cascade;

