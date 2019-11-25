-- reduce scene fields, create choices.

-- !Ups

alter table scene drop column image;

create table choice (
  id UUID not null,
  scene UUID not null,
  pos int not null,
  text text not null,
  primary key(id),
  constraint choice_fk_scene_scene
    foreign key(scene) references scene(id)
    on delete cascade
    on update cascade
);


-- !Downs

alter table scene add column image text null;
update scene set image = '';
alter table scene alter column image set not null;


drop table choice;
