-- creates the table for storing the association of login data
-- to authentication info for password auth.

-- !Ups

create table story (
  id UUID not null,
  creator UUID not null,
  name text not null,
  image text not null,
  description text not null,
  start_location UUID null,
  primary key(id),
  constraint story_fk_users_user
    foreign key(creator) references users(id)
    on delete cascade
    on update cascade
);

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

create table scene (
  id UUID not null,
  story UUID not null,
  raw text not null,
  title text not null,
  image text not null,
  location UUID not null,
  text text not null,
  mandatory boolean not null,
  primary key(id),
  constraint scene_fk_story_story
    foreign key(story) references story(id)
    on delete cascade
    on update cascade,
  constraint scene_fk_location_location
    foreign key(location) references location(id)
    on delete cascade
    on update cascade
);


-- !Downs

drop table story;

drop table location;

drop table scene;
