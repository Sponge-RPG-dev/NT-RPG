--@id:init-db
--@author:NeumimTo
--@date:04.09.2018 01:00
--@note:Initial setup
CREATE TABLE rpg_character_attribute (
    attribute_id bigint NOT NULL,
    level integer NOT NULL,
    name varchar(255),
    character_id bigint NOT NULL
);

CREATE TABLE rpg_character_base (
    character_id bigint NOT NULL,
    created timestamp without time zone,
    updated timestamp without time zone,
    attribute_points integer,
    can_reset_skills boolean,
    character_cooldowns text,
    guild_id bigint,
    health_scale double precision,
    info varchar(255),
    inventory_equip_slot_order text,
    last_known_player_name varchar(16),
    last_reset_time timestamp without time zone,
    marked_for_removal boolean,
    messagetype varchar(255),
    name varchar(40),
    primary_class varchar(255),
    race varchar(255),
    used_attribute_points integer,
    uuid varchar(40),
    version bigint,
    world varchar(255),
    x integer,
    y integer,
    z integer
);

CREATE TABLE rpg_character_class (
    class_id bigint NOT NULL,
    experiences double precision,
    name varchar(255),
    skillpoints integer,
    used_skil_points integer,
    character_id bigint
);

CREATE TABLE rpg_character_skill (
    skill_id bigint NOT NULL,
    catalog_id varchar(255),
    level integer NOT NULL,
    character_id bigint,
    class_id bigint
);

ALTER TABLE rpg_character_attribute
    ADD CONSTRAINT rpg_character_attribute_pkey PRIMARY KEY (attribute_id);

ALTER TABLE rpg_character_base
    ADD CONSTRAINT rpg_character_base_pkey PRIMARY KEY (character_id);

ALTER TABLE rpg_character_class
    ADD CONSTRAINT rpg_character_class_pkey PRIMARY KEY (class_id);

ALTER TABLE rpg_character_skill
    ADD CONSTRAINT rpg_character_skill_pkey PRIMARY KEY (skill_id);

CREATE INDEX idx_rpg_character_base_uuid ON rpg_character_base USING btree (uuid);

ALTER TABLE rpg_character_class
    ADD CONSTRAINT fk_cbase_rpg_character_base_character_id
    FOREIGN KEY (character_id) REFERENCES rpg_character_base(character_id);


ALTER TABLE rpg_character_skill
    ADD CONSTRAINT fk_cskill_rpg_character_base_character_id FOREIGN KEY (character_id) REFERENCES rpg_character_base(character_id);

ALTER TABLE rpg_character_skill
    ADD CONSTRAINT fk_cskill_rpg_character_class_class_id FOREIGN KEY (class_id) REFERENCES rpg_character_class(class_id);

ALTER TABLE rpg_character_attribute
    ADD CONSTRAINT fk_catt_rpg_character_base_character_id FOREIGN KEY (character_id) REFERENCES rpg_character_base(character_id);
