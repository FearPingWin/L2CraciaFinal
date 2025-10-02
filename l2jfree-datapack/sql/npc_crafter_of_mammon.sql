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

COMMIT;
