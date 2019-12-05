-- add additional logical unique key to choices.

-- !Ups

update choice
set title = pos, urlname = pos
where urlname = '';

alter table choice add constraint choice_unique_scene_urlname
  unique(scene, urlname);

-- !Downs

alter table choice drop constraint choice_unique_scene_urlname;
