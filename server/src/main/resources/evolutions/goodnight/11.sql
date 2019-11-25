-- add logical unique key to choices.

-- !Ups

alter table choice add constraint choice_unique_scene_pos
  unique(scene, pos);

-- !Downs

alter table choice drop constraint choice_unique_scene_pos;
