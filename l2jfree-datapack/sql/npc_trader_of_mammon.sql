START TRANSACTION;

-- 1) Шаблон НПЦ: создаём/обновляем запись в npc, копируя поля у Mammon Merchant (id=31113)
INSERT INTO npc (
  id, idTemplate, name, serverSideName, title, serverSideTitle,
  class, collision_radius, collision_height, level, sex, type, attackrange,
  hp, mp, hpreg, mpreg, str, con, dex, `int`, wit, men, exp, sp,
  patk, pdef, matk, mdef, atkspd, aggro, matkspd, rhand, lhand, armor,
  walkspd, runspd, faction_id, faction_range, isUndead, absorb_level, absorb_type,
  ss, bss, ss_rate, AI, drop_herbs
)
SELECT
  998,                                  -- <-- новый ID нашего НПЦ
  src.idTemplate,
  'Trader of Mamon',                    -- name
  1,                                    -- serverSideName (показывать name с сервера)
  'Game Shop',                          -- title
  1,                                    -- serverSideTitle (показывать title с сервера)
  src.class, src.collision_radius, src.collision_height, src.level, src.sex, src.type, src.attackrange,
  src.hp, src.mp, src.hpreg, src.mpreg, src.str, src.con, src.dex, src.`int`, src.wit, src.men, src.exp, src.sp,
  src.patk, src.pdef, src.matk, src.mdef, src.atkspd, src.aggro, src.matkspd, src.rhand, src.lhand, src.armor,
  src.walkspd, src.runspd, src.faction_id, src.faction_range, src.isUndead, src.absorb_level, src.absorb_type,
  src.ss, src.bss, src.ss_rate, src.AI, src.drop_herbs
FROM npc AS src
WHERE src.id = 31113  -- Mammon Merchant
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  serverSideName = VALUES(serverSideName),
  title = VALUES(title),
  serverSideTitle = VALUES(serverSideTitle);

-- 2) Чистим старые спавны (если были) для этого шаблона
DELETE FROM spawnlist WHERE npc_templateid = 998;

-- 3) Добавляем спавн на площади Гирана
INSERT INTO spawnlist (
  location, count, npc_templateid, locx, locy, locz, randomx, randomy, heading, respawn_delay, loc_id, periodOfDay
) VALUES (
  'Giran_Square', 1, 998, 82217, 148647, -3472, 0, 0, 0, 60, 0, 0
);

COMMIT;
