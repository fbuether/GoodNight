-- creates the table for logins, which is linked by a foreign key to
-- the users table. Users loses the columns for provider_id and _key.

-- !Ups

alter table users drop column login_provider_id;
alter table users drop column login_provider_key;

create table login (
  id UUID not null,
  user_id UUID not null,
  provider_id text not null,
  provider_key text not null,
  primary key(id),
  constraint login_fk_users_user
    foreign key(user_id) references users(id)
    on delete cascade
    on update cascade,
  constraint login_unique_provider unique(provider_id, provider_key)
);



-- !Downs

alter table users add column login_provider_id text not null;
alter table users add column login_provider_key text not null;

drop table login;
