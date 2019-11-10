-- creates the table for players.

-- !Ups

create table player (
  id UUID not null,
  user_id UUID not null,
  story UUID not null,
  player_name text not null,
  location UUID null,
  primary key(id),
  constraint player_fk_users_user
    foreign key(user_id) references users(id)
    on delete cascade
    on update cascade,
  constraint player_fk_story_story
    foreign key(story) references story(id)
    on delete cascade
    on update cascade,
  constraint player_fk_location_location
    foreign key(location) references location(id)
    on delete cascade
    on update cascade,
  constraint player_unique_user_story unique(user_id, story)
);

-- !Downs

drop table player;
