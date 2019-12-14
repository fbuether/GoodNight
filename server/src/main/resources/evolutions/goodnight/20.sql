-- New table for quality and activity.

-- !Ups

create table activity (
  id UUID not null,
  story text not null,
  "user" text not null,
  number int not null,
  scene text not null,
  random int array not null,
  primary key(id),
  constraint activity_unique_story_user_number
    unique(story, "user", number),
  constraint activity_fk_story_story_urlname
    foreign key(story) references story(urlname)
    on delete cascade
    on update cascade,
  constraint activity_fk_user_user_name
    foreign key("user") references "user"("name")
    on delete cascade
    on update cascade,
  constraint activity_fk_story_scene_scene_story_urlname
    foreign key(scene, story) references scene(story, urlname)
    on delete cascade
    on update cascade
);

create table quality (
  id UUID not null,
  story text not null,
  raw text not null,
  name text not null,
  urlname text not null,
  image text not null,
  description text not null,
  primary key(id),
  constraint quality_unique_story_name
    unique(story, name),
  constraint quality_fk_story_story_urlname
    foreign key(story) references story(urlname)
    on delete cascade
    on update cascade
);


-- !Downs

drop table activity;

drop table quality;
