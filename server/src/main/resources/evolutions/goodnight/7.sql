-- extend scene table to also have a unique urlname per story, at least.

-- !Ups

create unique index scene_index_story_urlname on scene (story, urlname);

-- !Downs

drop index if exists scene_index_story_urlname;
