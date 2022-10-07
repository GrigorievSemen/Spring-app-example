CREATE SCHEMA myschema;

create table myschema.person
(
    id        BIGINT      not null,
    full_name varchar(50) not null,
    title     varchar(50) not null,
    age       integer     not null,
    constraint pk_myschema_person_id primary key (id)
);

CREATE UNIQUE INDEX idx_myschema_title on myschema.person (title);

comment on table myschema.person is 'Справочник используется для хранения баджей';
comment on column myschema.person.id is 'Идентификатор пользователя';
comment on column myschema.person.full_name is 'Полное имя';
comment on column myschema.person.title is 'Должность';
comment on column myschema.person.age is 'Возраст';
