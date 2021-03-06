CREATE EXTENSION if not exists pgcrypto;

DROP TRIGGER IF EXISTS insert_default_characters_to_new_account ON account;

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
    ct                                  character_type%rowtype;
    new_already_inserted_account_id   bigint;
BEGIN
    new_already_inserted_account_id = NEW.account_id;
    -- for each loop in selected array of rows
    -- select takes all characters types
    FOR ct IN
        SELECT * FROM character_type
        LOOP
            -- insert every new character of that type to new account
            INSERT INTO character (character_type_id, account_id, is_enabled)
            VALUES (ct.character_type_id, new_already_inserted_account_id, ct.is_enabled_at_begin);
        END LOOP;

    RETURN NEW;
END;
$$;

create trigger insert_default_characters_to_new_account
    after insert
    on account
    for each row
execute procedure insert_default_characters_to_new_account();
