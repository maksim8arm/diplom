create schema test;
create table test1
(
    id       serial primary key,
    login     varchar(255),
    email     varchar(255),
    password     varchar(255),
    token      varchar(255)
);

insert into test1 (login, email, password, token)
values ('tom','q@q','123', '');