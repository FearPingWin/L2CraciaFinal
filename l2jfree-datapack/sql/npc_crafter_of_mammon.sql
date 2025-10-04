START TRANSACTION;

INSERT INTO npc (
  id, idTemplate, name, serverSideName, title, serverSideTitle,
  class, collision_radius, collision_height, level, sex, type, attackrange,
  hp, mp, hpreg, mpreg, str, con, dex, `int`, wit, men, exp, sp,
  patk, pdef, matk, mdef, atkspd, aggro, matkspd, rhand, lhand, armor,
  walkspd, runspd, faction_id, faction_range, isUndead, absorb_level, absorb_type,
  ss, bss, ss_rate, AI, drop_herbs
)
SELECT
  999,
  src.idTemplate,
  'Crafter of Mammon',
  1,
  'Shared Crafter',
  1,
  src.class, src.collision_radius, src.collision_height, src.level, src.sex, src.type, src.attackrange,
  src.hp, src.mp, src.hpreg, src.mpreg, src.str, src.con, src.dex, src.`int`, src.wit, src.men, src.exp, src.sp,
  src.patk, src.pdef, src.matk, src.mdef, src.atkspd, src.aggro, src.matkspd, src.rhand, src.lhand, src.armor,
  src.walkspd, src.runspd, src.faction_id, src.faction_range, src.isUndead, src.absorb_level, src.absorb_type,
  src.ss, src.bss, src.ss_rate, src.AI, src.drop_herbs
FROM npc AS src
WHERE src.id = 31126
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  serverSideName = VALUES(serverSideName),
  title = VALUES(title),
  serverSideTitle = VALUES(serverSideTitle);

DELETE FROM spawnlist WHERE npc_templateid = 999;

INSERT INTO spawnlist (
  location, count, npc_templateid, locx, locy, locz, randomx, randomy, heading, respawn_delay, loc_id, periodOfDay
) VALUES (
  'Giran_Square', 1, 999, 82210, 148610, -3472, 0, 0, 0, 60, 0, 0
);

CREATE TABLE IF NOT EXISTS npc_crafter_recipes (
  npc_id       INT          NOT NULL,
  recipe_id    INT          NOT NULL,
  is_learned   TINYINT(1)   NOT NULL DEFAULT 0,
  learned_at   TIMESTAMP    NULL DEFAULT NULL,
  fee_currency INT          NOT NULL DEFAULT 57,   -- адена по умолчанию
  fee_amount   BIGINT       NOT NULL DEFAULT 0,    -- фиксированная цена за крафт
  category     VARCHAR(20)  DEFAULT NULL,          -- например: shots, mats, armor_d, weapon_d, armor_c, weapon_c, ... s80, s84
  enabled      TINYINT(1)   NOT NULL DEFAULT 1,    -- можно быстро выключить рецепт
  notes        VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (npc_id, recipe_id),
  KEY idx_npc_cat (npc_id, category)
) DEFAULT CHARSET=utf8;

INSERT INTO npc_crafter_recipes
(npc_id, recipe_id, is_learned, learned_at, fee_currency, fee_amount, category, enabled, notes) VALUES
(999,480,0,NULL,57,0,'shots',1,NULL),
(999,481,0,NULL,57,0,'shots',1,NULL),
(999,482,0,NULL,57,0,'shots',1,NULL),
(999,483,0,NULL,57,0,'shots',1,NULL),
(999,484,0,NULL,57,0,'shots',1,NULL),
(999,317,0,NULL,57,0,'shots',1,NULL),
(999,318,0,NULL,57,0,'shots',1,NULL),
(999,319,0,NULL,57,0,'shots',1,NULL),
(999,320,0,NULL,57,0,'shots',1,NULL),
(999,321,0,NULL,57,0,'shots',1,NULL),
(999,323,0,NULL,57,0,'shots',1,NULL),
(999,324,0,NULL,57,0,'shots',1,NULL),
(999,325,0,NULL,57,0,'shots',1,NULL),
(999,326,0,NULL,57,0,'shots',1,NULL),
(999,327,0,NULL,57,0,'shots',1,NULL),
(999,474,0,NULL,57,0,'mats',1,NULL), --maestro holder
(999,31,0,NULL,57,0,'mats',1,NULL), -- coarsed bone powder
(999,26,0,NULL,57,0,'mats',1,NULL), -- cord
(999,41,0,NULL,57,0,'mats',1,NULL), -- crafted lether
(999,38,0,NULL,57,0,'mats',1,NULL), -- mitril allay
(999,30,0,NULL,57,0,'mats',1,NULL), -- steel
(999,36,0,NULL,57,0,'mats',1,NULL) -- syntetic cookes
ON DUPLICATE KEY UPDATE category='shots', enabled=1;
COMMIT;
