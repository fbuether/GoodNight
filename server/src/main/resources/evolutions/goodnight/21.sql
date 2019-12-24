-- Fix foreign key from activity to scene.

-- !Ups

alter table activity
drop constraint activity_fk_story_scene_scene_story_urlname;

alter table activity
add constraint activity_fk_story_scene_scene_story_urlname
  foreign key(story, scene) references scene(story, urlname)
  on delete cascade
  on update cascade;


-- !Downs

alter table activity
drop constraint activity_fk_story_scene_scene_story_urlname;

alter table activity
add constraint activity_fk_story_scene_scene_story_urlname
  foreign key(scene, story) references scene(story, urlname)
  on delete cascade
  on update cascade;
