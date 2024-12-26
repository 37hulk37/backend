create type retryable_task_state as enum('Created', 'InProgress', 'Completed', 'Failed');
create cast (varchar as retryable_task_state) with inout as implicit;

create table retryable_task(
    id bigserial primary key,
    payload jsonb not null,
    type text not null,
    state retryable_task_state not null,
    retry_at timestamp not null
);