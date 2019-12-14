-- rename table users to user

-- !Ups

alter table users
rename to "user";

alter table "user"
add constraint user_unique_name
  unique(name);

-- !Downs

alter table "user"
drop constraint user_unique_name;

alter table "user"
rename to users;

