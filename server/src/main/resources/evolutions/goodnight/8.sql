-- creates the table for bearer tokens.

-- !Ups

create table bearer_token (
  id text not null,
  provider text not null,
  key text not null,
  last_used bigint not null,
  expiration bigint not null,
  timeout bigint null,
  primary key(id)
);

-- !Downs

drop table bearer_token;
