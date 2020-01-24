-- New table for references inbetween of scenes

-- !Ups

create table sceneref (
  id UUID not null,
  story text not null,
  "from" text not null,
  "kind" int not null,
  "to" text not null,
  primary key(id),
  constraint sceneref_fk_story_story_urlname
    foreign key(story) references story(urlname)
    on delete cascade
    on update cascade,
  constraint sceneref_fk_story_from_scene_story_urlname
    foreign key(story, "from") references scene(story, urlname)
    on delete cascade
    on update cascade
);


-- !Downs

drop table sceneref;
