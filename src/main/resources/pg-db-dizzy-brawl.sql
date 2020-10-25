CREATE EXTENSION if not exists pgcrypto;

create table if not exists account
(
    account_uuid uuid default gen_random_uuid() not null
        constraint account_pk
            primary key,
    username     varchar                        not null,
    password     varchar                        not null,
    email        varchar                        not null
);

create unique index if not exists account_account_id_uindex
    on account (account_uuid);

create unique index if not exists account_email_uindex
    on account (email);

create unique index if not exists account_username_uindex
    on account (username);

create table if not exists character_type
(
    character_type_id   serial                not null
        constraint character_type_pk
            primary key,
    name                varchar               not null,
    is_enabled_at_begin boolean default false not null
);

create unique index if not exists character_type_character_type_id_uindex
    on character_type (character_type_id);

create unique index if not exists character_type_name_uindex
    on character_type (name);

create table if not exists character
(
    character_uuid    uuid default gen_random_uuid() not null
        constraint character_pk
            primary key,
    character_type_id integer                        not null
        constraint character_character_type_character_type_id_fk
            references character_type
            on update cascade on delete cascade,
    account_uuid      uuid                           not null
        constraint character_account_account_uuid_fk
            references account
            on update cascade on delete cascade,
    is_enabled        boolean                        not null
);

create unique index if not exists character_character_uuid_uindex
    on character (character_uuid);

create unique index if not exists character_character_type_id_account_uuid_uindex
    on character (character_type_id, account_uuid);

create table if not exists task
(
    task_uuid       uuid      default gen_random_uuid()            not null
        constraint task_pk
            primary key,
    account_uuid    uuid                                           not null
        constraint task_account_account_uuid_fk
            references account
            on update cascade on delete cascade,
    task_type_id    integer                                        not null,
    current_state   integer                                        not null,
    goal_state      integer,
    generated_date  timestamp default timezone('utc'::text, now()) not null,
    active_interval integer                                        not null
);

create unique index if not exists task_task_uuid_uindex
    on task (task_uuid);

create or replace function insert_default_characters_to_new_account() returns trigger
    language plpgsql
as
$$
    -- function takes all character types
    -- and insert them to new registered account
    -- also checking - is that character enabled
    -- at begin for default new account
DECLARE
    -- variable that have character_type indexes
    -- ct (character type)
    ct                                character_type%rowtype;
    new_already_inserted_account_uuid uuid;
BEGIN
    new_already_inserted_account_uuid = NEW.account_uuid;
    -- for each loop in selected array of rows
    -- select takes all characters types
    FOR ct IN
        SELECT * FROM character_type
        LOOP
            -- insert every new character of that type to new account
            INSERT INTO character (character_type_id, account_uuid, is_enabled)
            VALUES (ct.character_type_id, new_already_inserted_account_uuid, ct.is_enabled_at_begin);
        END LOOP;

    RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS insert_default_characters_to_new_account ON account;

create trigger insert_default_characters_to_new_account
    after insert
    on account
    for each row
execute procedure insert_default_characters_to_new_account();

