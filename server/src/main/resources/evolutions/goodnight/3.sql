-- creates the table for storing the association of login data
-- to authentication info for password auth.

-- !Ups

create table login_auth (
  id UUID not null,
  provider_id text not null,
  provider_key text not null,
  hasher text not null,
  password text not null,
  salt text null,
  primary key(id),
  constraint auth_login_unique_provider unique(provider_id, provider_key)
);


-- !Downs

drop table login_auth;
