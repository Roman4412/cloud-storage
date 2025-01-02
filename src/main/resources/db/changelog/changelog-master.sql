--liquibase formatted sql

--create table users
--changeset rpustovalov:1
create table if not exists users
(
    id       bigserial primary key,
    username varchar(255) not null,
    password varchar(255),

    constraint users_unique_username_idx unique (username)
);


--create table s3objects
--changeset rpustovalov:2
create table if not exists s3objects
(
    id           bigserial primary key,
    size         bigint,
    user_id      bigint       not null,
    type         varchar(31)  not null,
    content_type varchar(255),
    name         varchar(255) not null,
    object_key   varchar(255) not null,
    parent       varchar(255),

    foreign key (user_id) references users (id),
    constraint s3objects_unique_object_key_idx unique (object_key)
);