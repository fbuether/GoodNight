-- New table for player state.

-- !Ups

create table state (
  id UUID not null,
  "user" text not null,
  story text not null,
  quality text not null,
  value text not null,
  primary key(id),
  constraint state_fk_user_story_player_user_story
    foreign key("user", story) references player("user", story)
    on delete cascade
    on update cascade
);


-- !Downs

drop table state;
