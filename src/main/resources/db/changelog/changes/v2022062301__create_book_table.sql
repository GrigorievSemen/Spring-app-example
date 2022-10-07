create table myschema.book
(
    id         BIGINT      not null,
    person_id  BIGINT      not null,
    title      varchar(50) not null,
    author     varchar(50) not null,
    page_count integer     not null,
    constraint pk_myschema_book_id primary key (id)
);

comment on table myschema.book is 'Справочник используется для хранения баджей';
comment on column myschema.book.id is 'Идентификатор книги';
comment on column myschema.book.person_id is 'Идентификатор пользователя';
comment on column myschema.book.title is 'Заголовок';
comment on column myschema.book.author is 'Автор';
comment on column myschema.book.page_count is 'Количество страниц';

