-- New foreign key from activity to player, to facilitate cascaded deletes.

-- !Ups

alter table activity
add constraint activity_fk_user_story_player_user_story
  foreign key("user", story) references player("user", story)
  on delete cascade
  on update cascade;


-- !Downs

alter table activity
drop constraint activity_fk_user_story_player_user_story;
