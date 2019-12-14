-- Remove old tables.

-- !Ups

alter table choice
drop constraint choice_unique_scene_urlname;

alter table choice
drop constraint choice_unique_scene_pos;

drop table choice;


alter table scene
drop constraint scene_fk_location_location;

alter table player
drop constraint player_fk_location_location;

drop table location;

-- !Downs


create table choice (
  id UUID not null,
  scene UUID not null,
  pos int not null,
  text text not null,
  title text not null,
  urlname text not null,
  primary key(id),
  constraint choice_fk_scene_scene
    foreign key(scene) references scene(id)
    on delete cascade
    on update cascade
);

alter table choice add constraint choice_unique_scene_pos
  unique(scene, pos);

alter table choice add constraint choice_unique_scene_urlname
  unique(scene, urlname);


create table location (
  id UUID not null,
  story UUID not null,
  name text not null,
  primary key(id),
  constraint location_fk_story_story
    foreign key(story) references story(id)
    on delete cascade
    on update cascade
);

alter table scene
add constraint scene_fk_location_location
  foreign key(location) references location(id)
  on delete cascade
  on update cascade;


alter table player
add constraint player_fk_location_location
  foreign key(location) references location(id)
  on delete cascade
  on update cascade;
