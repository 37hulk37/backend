create type people_type as enum ('Teacher', 'Student');
create cast (varchar as people_type) with inout as implicit;

create type account_type as enum ('User', 'Admin');
create cast (varchar as account_type) with inout as implicit;

create table if not exists account(
    id bigint primary key,
    username varchar(128) unique not null,
    password varchar(256) not null,
    type account_type not null
);

create table if not exists "group"(
    id bigserial primary key,
    name text not null unique
);

create table if not exists people(
    id bigserial primary key,
    account_id bigint references account(id) on delete cascade,
    first_name text not null,
    last_name text not null,
    pather_name text not null,
    group_id bigint references "group"(id) on delete cascade,
    type varchar(32) not null
);

create table if not exists subject(
    id bigserial primary key,
    name text not null
);

create table if not exists mark(
    id bigserial primary key,
    student_id bigint references people(id) on delete cascade,
    subject_id bigint references subject(id) on delete cascade,
    teacher_id bigint references people(id) on delete cascade,
    value integer not null,
    year integer not null,

    check (value >= 2 and mark.value <= 5),
    check ( year >= 0 )
);