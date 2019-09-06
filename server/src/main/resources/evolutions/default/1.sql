
-- !Ups

create table users (
  id UUID not null,
  name text not null,
  login_provider_id text not null,
  login_provider_key text not null,
  primary key(id)
);

-- !Downs

drop table user;
