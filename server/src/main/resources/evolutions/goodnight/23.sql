-- Stories may be public.

-- !Ups

alter table story
add column "public" boolean null;

update story
set "public" = false;

alter table story
alter column "public" set not null;

-- !Downs

alter table story
drop column "public";


