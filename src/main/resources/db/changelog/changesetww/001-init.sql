create table if not exists "group"(
    id serial primary key,
    name text not null unique
);

create table if not exists people(
    id serial primary key,
    first_name text not null,
    last_name text not null,
    pather_name text not null,
    group_id bigint references "group"(id) on delete cascade,
    type varchar(32) not null
);

create table if not exists subject(
    id serial primary key,
    name text
);

create table if not exists mark(
    id serial primary key,
    student_id bigint references people(id) on delete cascade,
    subject_id bigint references subject(id) on delete cascade,
    teacher_id bigint references people(id) on delete cascade,
    value integer not null,
    year integer not null,

    check (value >= 2 and mark.value <= 5),
    check ( year >= 0 )
)