-- ---------------------------
-- Table structure for `zone`
-- ---------------------------
DROP TABLE IF EXISTS `zone`;
CREATE TABLE `zone` (
  `id` INT NOT NULL default 0,
  `type` varchar(25) NOT NULL,
  `name` varchar(40) NOT NULL,
  `x1` INT NOT NULL default 0,
  `y1` INT NOT NULL default 0,
  `x2` INT NOT NULL default 0,
  `y2` INT NOT NULL default 0,
  `z` INT NOT NULL default 0,
  `z2` INT NOT NULL default 0,
  `taxById` INT NOT NULL default 0
) DEFAULT CHARSET=utf8;

-- -----------------------------------------
-- Arena Zones
-- -----------------------------------------

INSERT INTO `zone` VALUES (1, 'Arena', 'Giran Arena', 72498, 142271, 73495, 143255, -3774, 0, 0);
INSERT INTO `zone` VALUES (2, 'Arena', 'Gludin Arena', -88410, 142728, -87421, 141730, -3633, 0, 0);
INSERT INTO `zone` VALUES (3, 'Arena', 'Collusieum', 148014, 45304, 150976, 48020, -3410, 0, 0);
INSERT INTO `zone` VALUES (4, 'Arena', 'Monster Track', 11955, 183017, 12937, 184008, -3565, 0, 0);

-- -----------------------------------------
-- Arena Spawn Zones
-- -----------------------------------------

INSERT INTO `zone` VALUES (1, 'Arena Spawn', 'Giran Arena', 73890, 142656, 0, 0, -3778, 0, 0);
INSERT INTO `zone` VALUES (2, 'Arena Spawn', 'Gludin Arena', -86979, 142402, 0, 0, -3643, 0, 0);
INSERT INTO `zone` VALUES (3, 'Arena Spawn', 'Collusieum', 147451, 46728, 0, 0, -3410, 0, 0);
INSERT INTO `zone` VALUES (4, 'Arena Spawn', 'Monster Track', 12312, 182752, 0, 0, -3558, 0, 0);

-- -----------------------------------------
-- Castle Area Zones
-- -----------------------------------------

INSERT INTO `zone` VALUES (1, 'Castle Area', 'Gludio', -22615, 104510, -13313, 116950, 0, 0 ,5);
INSERT INTO `zone` VALUES (1, 'Castle Area', 'Gludio', -15500, 116500, -15000, 117000, 0, 0 ,5); -- Tyron (-15172,116723) registers sieges to Gludio
INSERT INTO `zone` VALUES (2, 'Castle Area', 'Dion', 17273, 152800, 26575, 165240, 0, 0 ,5);
INSERT INTO `zone` VALUES (2, 'Castle Area', 'Dion', 19000, 152750, 19500, 153250, 0, 0 ,5); -- Gibbson (19132,153027) registers sieges to Dion
INSERT INTO `zone` VALUES (3, 'Castle Area', 'Giran', 109026, 140571, 121358, 149919, 0, 0 ,5);
INSERT INTO `zone` VALUES (3, 'Castle Area', 'Giran', 107000, 145500, 107500, 146000, 0, 0 ,5); -- Holmes (107386,145807) registers sieges to Giran
INSERT INTO `zone` VALUES (4, 'Castle Area', 'Oren', 75102, 32666, 87434, 42014, 0, 0 ,5);
INSERT INTO `zone` VALUES (4, 'Castle Area', 'Oren', 75000, 40000, 75500, 40500, 0, 0 ,5); -- Sherwood (75275,40132) registers sieges to Oren
INSERT INTO `zone` VALUES (5, 'Castle Area', 'Aden', 142431, 362, 152282, 15197, 0, 0, 0);
INSERT INTO `zone` VALUES (5, 'Castle Area', 'Aden', 147250, 19250, 147750, 19750, 0, 0, 0); -- Ruford (147551,19577) registers sieges to Aden
INSERT INTO `zone` VALUES (6, 'Castle Area', 'Innadril', 111224, 241579, 120526, 254019, 0, 0 ,5);
INSERT INTO `zone` VALUES (6, 'Castle Area', 'Innadril', 117000, 241500, 117500, 242000, 0, 0 ,5); -- Raybell (117136,241763) registers sieges to Innadril
INSERT INTO `zone` VALUES (7, 'Castle Area', 'Goddard', 141457, -51510, 151949, -39107, 0, 0 ,8); -- needs an n-poly to curve around south wall
INSERT INTO `zone` VALUES (7, 'Castle Area', 'Goddard', 154000, -52000, 154500, -51000, 0, 0 ,8); -- Daven (154208,-51648) registers sieges to Goddard
INSERT INTO `zone` VALUES (8, 'Castle Area', 'Rune', 7000, -55500, 27000, -41716, 0, 0, 8); -- needs an n-poly to curve around west & east walls
INSERT INTO `zone` VALUES (8, 'Castle Area', 'Rune', 26750, -49250, 27250, -48750, 0, 0, 8);-- Sherman (27074,-48986) registers sieges to Rune
INSERT INTO `zone` VALUES (9, 'Castle Area', 'Schuttgart', 73000, -156600, 82560, -145920, 0, 0, 8); -- needs an n-poly to curve around south wall
INSERT INTO `zone` VALUES (9, 'Castle Area', 'Schuttgart', 75500, -145250, 76000, -144750, 0, 0, 8); -- Daguerre (75707,-144899) registers sieges to Schuttgart                                                                                                                                                                                                                                                  

-- -----------------------------------------
-- Castle HQ Zones
-- -----------------------------------------

INSERT INTO `zone` VALUES (1, 'Castle HQ', 'Gludio', -20400, 106800, -15700, 113750, 0, 0 ,5);
INSERT INTO `zone` VALUES (2, 'Castle HQ', 'Dion', 19650, 163000, 24350, 155950, 0, 0 ,5);
INSERT INTO `zone` VALUES (3, 'Castle HQ', 'Giran', 119200, 142750, 112200, 147450, 0, 0 ,5);
INSERT INTO `zone` VALUES (4, 'Castle HQ', 'Oren', 85300, 34900, 78100, 39600, 0, 0 ,5);
INSERT INTO `zone` VALUES (5, 'Castle HQ', 'Aden', 144600, 550, 150300, 8550, 0, 0, 0);
INSERT INTO `zone` VALUES (6, 'Castle HQ', 'Innadril', 111975, 241396, 120720, 253425, 0, 0 ,5);
INSERT INTO `zone` VALUES (7, 'Castle HQ', 'Goddard', 142931, -52434, 152986, -41716, 0, 0 ,8);
INSERT INTO `zone` VALUES (8, 'Castle HQ', 'Rune', 7000, -52500, 18493, -45900, -547, 0, 8);
INSERT INTO `zone` VALUES (9, 'Castle HQ', 'Schuttgart', 73694, -156000, 81286, -149400, -700, 1750, 8);

-- -----------------------------------------
-- Siege Battlefield Zones
-- -----------------------------------------

INSERT INTO `zone` VALUES (1, 'Siege Battlefield', 'Gludio', -22615, 104510, -13313, 116950, 0, 0, 0);                          
INSERT INTO `zone` VALUES (2, 'Siege Battlefield', 'Dion', 17273, 152800, 26575, 165240, 0, 0, 0);                              
INSERT INTO `zone` VALUES (3, 'Siege Battlefield', 'Giran', 109026, 140571, 121358, 149919, 0, 0, 0);                           
INSERT INTO `zone` VALUES (4, 'Siege Battlefield', 'Oren', 75102, 32666, 87434, 42014, 0, 0, 0);                                
INSERT INTO `zone` VALUES (5, 'Siege Battlefield', 'Aden', 142431, 362, 152282, 15197, 0, 0, 0);                                
INSERT INTO `zone` VALUES (6, 'Siege Battlefield', 'Innadril', 111224, 241579, 120526, 254019, 0, 0, 0);                        
INSERT INTO `zone` VALUES (7, 'Siege Battlefield', 'Goddard', 141457, -51510, 151949, -39107, 0, 0, 0); -- should be an n-poly  
INSERT INTO `zone` VALUES (8, 'Siege Battlefield', 'Rune', 7000, -55500, 27000, -41716, 0, 0, 0); -- need official info         
INSERT INTO `zone` VALUES (9, 'Siege Battlefield', 'Schuttgart', 73000, -156600, 82560, -145920, 0, 0, 0); -- need official info

-- -----------------------------------------
-- Castle Defender Spawn Zones
-- -----------------------------------------

INSERT INTO `zone` VALUES (1, 'Castle Defender Spawn', 'Gludio', -18105, 110303, 0, 0, -2146, 0, 0);
INSERT INTO `zone` VALUES (2, 'Castle Defender Spawn', 'Dion', 22080, 159450, 0, 0, -2441, 0, 0);
INSERT INTO `zone` VALUES (3, 'Castle Defender Spawn', 'Giran', 115621, 145097, 0, 0, -2214, 0, 0);
INSERT INTO `zone` VALUES (4, 'Castle Defender Spawn', 'Oren', 81707, 37208, 0, 0, -1941, 0, 0);
INSERT INTO `zone` VALUES (5, 'Castle Defender Spawn', 'Aden', 147456, 6048, 0, 0, 253, 0, 0);
INSERT INTO `zone` VALUES (6, 'Castle Defender Spawn', 'Innadril', 116025, 248229, 0, 0, -536, 0, 0);
INSERT INTO `zone` VALUES (7, 'Castle Defender Spawn', 'Goddard', 147408, -46448, 0, 0, -963, 0, 0);
INSERT INTO `zone` VALUES (8, 'Castle Defender Spawn', 'Rune', 11388, -49160, 0, 0, -537, 0, 0);
INSERT INTO `zone` VALUES (9, 'Castle Defender Spawn', 'Schuttgart', 77524, -152709, 0, 0, -545, 0, 0);

-- -----------------------------------------
-- Clan Hall Zones
-- -----------------------------------------

INSERT INTO `zone` VALUES (21, 'Clan Hall', 'Partisan Hideaway', 43151, 108377, 43648, 109399, -1980,0, 1);
INSERT INTO `zone` VALUES (22, 'Clan Hall', 'Gludio 1', -16400, 123275, -15551, 123850, -3101, 0, 1);
INSERT INTO `zone` VALUES (23, 'Clan Hall', 'Gludio 2', -15100, 125350, -14800, 125800, -3128, 0, 1);
INSERT INTO `zone` VALUES (24, 'Clan Hall', 'Gludio 3', -14050, 125050, -13700, 125700, -3128, 0, 1);
INSERT INTO `zone` VALUES (25, 'Clan Hall', 'Gludio 4', -12950, 123900, -12300, 124250, -3096, 0, 1);
INSERT INTO `zone` VALUES (26, 'Clan Hall', 'Gludin 1', -84350, 151950, -83800, 152350, -3123, 0, 1);
INSERT INTO `zone` VALUES (26, 'Clan Hall', 'Gludin 1', -84700, 151550, -84250, 152350, -3123, 0, 1);
INSERT INTO `zone` VALUES (27, 'Clan Hall', 'Gludin 2', -84200, 153050, -83550, 153600, -3159, 0, 1);
INSERT INTO `zone` VALUES (27, 'Clan Hall', 'Gludin 2', -84400, 153050, -83950, 154050, -3159, 0, 1);
INSERT INTO `zone` VALUES (28, 'Clan Hall', 'Gludin 3', -84100, 155300, -83500, 155700, -3160, 0, 1);
INSERT INTO `zone` VALUES (28, 'Clan Hall', 'Gludin 3', -84500, 154900, -83950, 155700, -3160, 0, 1);
INSERT INTO `zone` VALUES (29, 'Clan Hall', 'Gludin 4', -79700, 149400, -79250, 150300, -3038, 0, 1);
INSERT INTO `zone` VALUES (29, 'Clan Hall', 'Gludin 4', -80100, 149400, -79500, 149850, -3038, 0, 1);
INSERT INTO `zone` VALUES (30, 'Clan Hall', 'Gludin 5', -79700, 151350, -79300, 152250, -3041, 0, 1);
INSERT INTO `zone` VALUES (30, 'Clan Hall', 'Gludin 5', -80100, 151800, -79500, 152250, -3041, 0, 1);
INSERT INTO `zone` VALUES (31, 'Clan Hall', 'Dion 1', 17400, 144800, 18000, 145350, -3036, 0, 1);
INSERT INTO `zone` VALUES (32, 'Clan Hall', 'Dion 2', 18850, 143600, 18600, 143100, -3010, 0, 1);
INSERT INTO `zone` VALUES (33, 'Clan Hall', 'Dion 3', 19950, 146000, 20400, 146300, -3111, 0, 1);
INSERT INTO `zone` VALUES (35, 'Clan Hall', 'Bandits Stronghold', 80738, -15914, 79627, -15054, -1804, 0, 1);
INSERT INTO `zone` VALUES (36, 'Clan Hall', 'Aden 1', 148844, 22709, 149424, 23569, -2160, 0, 1);
INSERT INTO `zone` VALUES (37, 'Clan Hall', 'Aden 2', 150343, 23193, 150943, 24113, -2150, 0, 1);
INSERT INTO `zone` VALUES (38, 'Clan Hall', 'Aden 3', 145362, 24890, 145972, 25820, -2150, 0, 1);
INSERT INTO `zone` VALUES (39, 'Clan Hall', 'Aden 4', 150460, 26108, 151036, 26972, -2280, 0, 1);
INSERT INTO `zone` VALUES (40, 'Clan Hall', 'Aden 5', 143701, 26661, 144281, 27521, -2290, 0, 1);
INSERT INTO `zone` VALUES (41, 'Clan Hall', 'Aden 6', 143704, 27734, 144324, 28670, -2280, 0, 1);
INSERT INTO `zone` VALUES (42, 'Clan Hall', 'Giran 1', 78059, 147906, 79122, 148296, -3581, 0, 1);
INSERT INTO `zone` VALUES (43, 'Clan Hall', 'Giran 2', 81859, 144802, 82254, 145870, -3520, 0, 1);
INSERT INTO `zone` VALUES (44, 'Clan Hall', 'Giran 3', 83195, 144779, 83591, 145847, -3389, 0, 1);
INSERT INTO `zone` VALUES (45, 'Clan Hall', 'Giran 4', 80773, 151053, 81169, 152121, -3517, 0, 1);
INSERT INTO `zone` VALUES (46, 'Clan Hall', 'Giran 5', 81903, 151377, 82299, 152445, -3520, 0, 1);
INSERT INTO `zone` VALUES (47, 'Clan Hall', 'Goddard 1', 146399, -55682, 145652, -55386, -2765, 0, 1);
INSERT INTO `zone` VALUES (48, 'Clan Hall', 'Goddard 2', 147238, -56636, 146564, -57078, -2765, 0, 1);
INSERT INTO `zone` VALUES (49, 'Clan Hall', 'Goddard 3', 148479, -56473, 148479, -57275, -2765, 0, 1);
INSERT INTO `zone` VALUES (50, 'Clan Hall', 'Goddard 4', 149717, -55824, 149063, -55350, -2765, 0, 1);
INSERT INTO `zone` VALUES (51, 'Clan Hall', 'Rune 1', 37461, -50973, 38006, -50589, 896, 0, 8);
INSERT INTO `zone` VALUES (52, 'Clan Hall', 'Rune 2', 38401, -50516, 39054, -50404, 896, 0, 8);
INSERT INTO `zone` VALUES (53, 'Clan Hall', 'Rune 3', 39173, -50020, 39774, -49340, 896, 0, 8);
INSERT INTO `zone` VALUES (54, 'Clan Hall', 'Rune 4', 39426, -48619, 39820, -47871, 896, 0, 8);
INSERT INTO `zone` VALUES (55, 'Clan Hall', 'Rune 5', 39437, -47141, 39760, -46668, 896, 0, 8);
INSERT INTO `zone` VALUES (56, 'Clan Hall', 'Rune 6', 38433, -46322, 39062, -45731, 896, 0, 8);
INSERT INTO `zone` VALUES (57, 'Clan Hall', 'Rune 7', 37437, -45872, 38024, -45460, 896, 0, 8);
INSERT INTO `zone` VALUES (58, 'Clan Hall', 'Schuttgart 1', 85426, -143448, 86069, -142769, -1328, 0, 8);
INSERT INTO `zone` VALUES (59, 'Clan Hall', 'Schuttgart 2', 86162, -142094, 87003, -141727, -1328, 0, 8);
INSERT INTO `zone` VALUES (60, 'Clan Hall', 'Schuttgart 3', 88600, -142111, 87724, -141750, -1328, 0, 8);
INSERT INTO `zone` VALUES (61, 'Clan Hall', 'Schuttgart 4', 88500, -143500, 89500, -142880, -1328, 0, 8);
INSERT INTO `zone` VALUES (62, 'Clan Hall', 'Hot Springs Guild House', 141414, -124508, 140590, -124706, -1896, 0, 1);
INSERT INTO `zone` VALUES (63, 'Clan Hall', 'Beast Farm', 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `zone` VALUES (64, 'Clan Hall', 'Fortress of the Dead', 0, 0, 0, 0, 0, 0, 0);

-- -----------------------------------------
-- Peace Zones
-- -----------------------------------------

INSERT INTO `zone` VALUES (1, 'Peace', 'Giran Arena', 72249, 142018, 72498, 143510, -3774, 0, 0);
INSERT INTO `zone` VALUES (1, 'Peace', 'Giran Arena', 73495, 142018, 73738, 143510, -3774, 0, 0);
INSERT INTO `zone` VALUES (1, 'Peace', 'Giran Arena', 72498, 142018, 73495, 142271, -3774, 0, 0);
INSERT INTO `zone` VALUES (1, 'Peace', 'Giran Arena', 72498, 143255, 73495, 143510, -3774, 0, 0);
INSERT INTO `zone` VALUES (2, 'Peace', 'Gudin Arena', -88654, 141479, -88410, 142960, -3648, 0, 0);
INSERT INTO `zone` VALUES (2, 'Peace', 'Gudin Arena', -87421, 141479, -87172, 142960, -3648, 0, 0);
INSERT INTO `zone` VALUES (2, 'Peace', 'Gudin Arena', -88410, 141479, -87421, 141728, -3648, 0, 0);
INSERT INTO `zone` VALUES (2, 'Peace', 'Gudin Arena', -88410, 142715, -87421, 142960, -3648, 0, 0);
INSERT INTO `zone` VALUES (3, 'Peace', 'Collusieum', 147117, 46230, 148014, 47217, -3410, 0, 0);
INSERT INTO `zone` VALUES (3, 'Peace', 'Collusieum', 147771, 45304, 148014, 46230, -3410, 0, 0);
INSERT INTO `zone` VALUES (3, 'Peace', 'Collusieum', 147771, 47217, 148014, 48020, -3410, 0, 0);
INSERT INTO `zone` VALUES (3, 'Peace', 'Collusieum', 150976, 46228, 151885, 47217, -3410, 0, 0);
INSERT INTO `zone` VALUES (3, 'Peace', 'Collusieum', 150976, 45304, 151218, 46228, -3410, 0, 0);
INSERT INTO `zone` VALUES (3, 'Peace', 'Collusieum', 150976, 47217, 151218, 48020, -3410, 0, 0);
INSERT INTO `zone` VALUES (4, 'Peace', 'Monster Track', 11703, 181289, 14574, 183017, -3564, 0, 0);
INSERT INTO `zone` VALUES (4, 'Peace', 'Monster Track', 11703, 183017, 11955, 184260, -3564, 0, 0);
INSERT INTO `zone` VALUES (4, 'Peace', 'Monster Track', 11955, 184008, 12937, 184260, -3564, 0, 0);
INSERT INTO `zone` VALUES (4, 'Peace', 'Monster Track', 12937, 183017, 13192, 184260, -3564, 0, 0);

-- -----------------------------------------
-- Town Zones
-- -----------------------------------------

INSERT INTO `zone` VALUES (1, 'Town', 'DE Village', 6063, 19664, 17248, 14019, 0,0, 4);
INSERT INTO `zone` VALUES (2, 'Town', 'Talking Island', -87312, 240096, -81129, 246345, 0,0, 1);
INSERT INTO `zone` VALUES (3, 'Town', 'Elven Village', 48294, 52995, 42402, 46155, 0,0, 4);
INSERT INTO `zone` VALUES (4, 'Town', 'Orc Village', -42078, -109785, -47648, -117366, 0,0, 8);
INSERT INTO `zone` VALUES (5, 'Town', 'Gludin Village', -84892, 149075, -76820, 156125, 0,0, 1);
INSERT INTO `zone` VALUES (6, 'Town', 'Dwarven Village', 117395, -176766, 114650, -184347, 0,0, 8);
INSERT INTO `zone` VALUES (7, 'Town', 'Gludio Castle Town', -11853, 126610, -16652, 121003, 0,0, 1);
INSERT INTO `zone` VALUES (8, 'Town', 'Dion Castle Town', 15300, 141609, 21570, 147635, 0,0, 2);
INSERT INTO `zone` VALUES (9, 'Town', 'Giran Castle Town', 76995, 141424, 90565, 153614, 0,0, 3);
INSERT INTO `zone` VALUES (10, 'Town', 'Town of Oren', 76696, 57199, 84511, 50120, 0,0, 4);
INSERT INTO `zone` VALUES (11, 'Town', 'Hunter Village', 121308, 73941, 114667, 80383, 0,0, 5);
INSERT INTO `zone` VALUES (12, 'Town', 'Town of Aden', 142312, 32317, 152163, 19708, 0,0, 5);
INSERT INTO `zone` VALUES (13, 'Town', 'Goddard', 143444, -59854, 152043, -51601, 0,0, 7);
INSERT INTO `zone` VALUES (14, 'Town', 'Rune Castle Town', 47150, -44815, 32531, -52045, 0,0, 8);
INSERT INTO `zone` VALUES (15, 'Town', 'Heine', 103598, 216010, 118991, 225905, 0,0, 6);
INSERT INTO `zone` VALUES (16, 'Town', 'Floran Village', 0, 0, 0, 0, 0,0, 2);
INSERT INTO `zone` VALUES (17, 'Town', 'Schuttgart', 83881, -146500, 90908, -139486, 0, 0, 9);
INSERT INTO `zone` VALUES (18, 'Town', 'Ivory Tower', 0, 0, 0, 0, 0, 0, 4);
INSERT INTO `zone` VALUES (19, 'Town', 'Primeval Isle Wharf', 9458, -24206, 10773, -22441, 0, 0, 5);

-- -----------------------------------------
-- Town Spawn Zones
-- -----------------------------------------

INSERT INTO `zone` VALUES (1, 'Town Spawn', 'DE Village', 12181, 16675, 0, 0, -4580,0, 0);
INSERT INTO `zone` VALUES (2, 'Town Spawn', 'Talking Island', -84176, 243382, 0, 0, -3126,0, 0);
INSERT INTO `zone` VALUES (3, 'Town Spawn', 'Elven Village', 45525, 48376, 0, 0, -3059,0, 0);
INSERT INTO `zone` VALUES (4, 'Town Spawn', 'Orc Village', -45232, -113603, 0, 0, -224,0, 0);
INSERT INTO `zone` VALUES (5, 'Town Spawn', 'Gludin Village', -82856, 150901, 0, 0, -3128,0, 0);
INSERT INTO `zone` VALUES (6, 'Town Spawn', 'Dwarven Village', 115074, -178115, 0, 0, -880,0, 0);
INSERT INTO `zone` VALUES (7, 'Town Spawn', 'Gludio Castle Town', -14138, 122042, 0, 0, -2988,0, 0);
INSERT INTO `zone` VALUES (8, 'Town Spawn', 'Dion Castle Town', 18823, 145048, 0, 0, -3126,0, 0);
INSERT INTO `zone` VALUES (9, 'Town Spawn', 'Giran Castle Town', 81236, 148638, 0, 0, -3469, 0, 0);
INSERT INTO `zone` VALUES (10, 'Town Spawn', 'Town of Oren', 80853, 54653, 0, 0, -1524,0, 0);
INSERT INTO `zone` VALUES (11, 'Town Spawn', 'Hunter Village', 117163, 76511, 0, 0, -2712,0, 0);
INSERT INTO `zone` VALUES (12, 'Town Spawn', 'Town of Aden', 147391, 25967, 0, 0, -2012,0, 0);
INSERT INTO `zone` VALUES (13, 'Town Spawn', 'Goddard', 148558, -56030, 0, 0, -2781,0, 0);
INSERT INTO `zone` VALUES (14, 'Town Spawn', 'Rune Castle Town', 43894, -48330, 0, 0, -797, 0, 0);
INSERT INTO `zone` VALUES (15, 'Town Spawn', 'Heine', 111381, 219064, 0, 0, -3543,0, 0);
INSERT INTO `zone` VALUES (16, 'Town Spawn', 'Floran Village', 17817, 170079, 0, 0, -3530,0, 0);
INSERT INTO `zone` VALUES (17, 'Town Spawn', 'Schuttgart', 87331, -142842, 0, 0, -1317, 0, 0);
INSERT INTO `zone` VALUES (18, 'Town Spawn', 'Ivory Tower', 0, 0, 0, 0, 0, 0, 0);
INSERT INTO `zone` VALUES (19, 'Town Spawn', 'Primeval Isle Wharf', 10825, -24156, 0, 0, -3645, 0, 0);

-- -----------------------------------------
-- Underground Zones
-- -----------------------------------------

INSERT INTO `zone` VALUES (1, 'Underground', 'Ascetics Necropolis', 0, 0, 0, 0, -4844, 0, 0);
INSERT INTO `zone` VALUES (2, 'Underground', 'Elven Ruins', 43100, 246500, 49400, 249200, -6614, 0, 0);
INSERT INTO `zone` VALUES (3, 'Underground', 'School of Dark Arts', -49800, 56879, -35311, 43790, -6000, 0, 0);
INSERT INTO `zone` VALUES (4, 'Underground', 'School of Dark Arts', -47150, 41782, -54659, 53065, -6000, 0, 0);
INSERT INTO `zone` VALUES (5, 'Underground', 'School of Dark Arts', -38741, 55152, -55186, 62474, -6000, 0, 0);
INSERT INTO `zone` VALUES (6, 'Underground', 'Ants Nest', 917, 165166, -45452, 201937, -6000, 0, 0);
INSERT INTO `zone` VALUES (7, 'Underground', 'Elven Fortress', 6068, 88790, 36734, 69188, -6000, 0, 0);
INSERT INTO `zone` VALUES (8, 'Underground', 'Ivory Tower', 76563, 27040, 98577, 7238, -6000, 0, 0);
INSERT INTO `zone` VALUES (9, 'Underground', 'Hunter Village', 123457, 68112, 98542, 92245, -6000, 0, 0);
INSERT INTO `zone` VALUES (10, 'Underground', 'DE Village', 34061, 8905, -7877, 26384, -6000, 0, 0);
INSERT INTO `zone` VALUES (11, 'Underground', 'Ruins of Despair', -1231, 131977, -31364, 160147, -5000, 0, 0);
INSERT INTO `zone` VALUES (12, 'Underground', 'Gludin North Road', -77276, 134858, -69590, 120547, -5000, 0, 0);
INSERT INTO `zone` VALUES (13, 'Underground', '', 38116, 147264, -69590, 120547, -5000, 0, 0);

-- -----------------------------------------
-- Other Zones
-- -----------------------------------------

INSERT INTO `zone` VALUES (1, 'Water', 'Ascetics Necropolis', -56190, 78595, -55175, 79600, -3061, 0, 0);
INSERT INTO `zone` VALUES (169, 'Fishing', 'Giran North Entrance1', 82480, 143048, 83321, 141782, 0, 0, 0);
INSERT INTO `zone` VALUES (170, 'Fishing', 'Giran North Entrance2', 82109, 142550, 82211, 142149, 0, 0, 0);
INSERT INTO `zone` VALUES (171, 'Fishing', 'Giran North Entrance3', 82235, 141780, 82700, 142718, 0, 0, 0);
INSERT INTO `zone` VALUES (1, 'No Landing', 'ToI', 109448, 10233, 118547, 21446, 0, 0, 0);

INSERT INTO `zone` VALUES (1, 'Jail', 'GM Jail', -115600, -250700, -113500, -248200, 0, 0, 0);
INSERT INTO `zone` VALUES (1, 'Monster Derby Track', 'Monster Derby Track', 11600, 181200, 14600, 184500, -3565, 0, 0);

-- -----------------------------------------
-- Olympiad Stadia Zones
-- -----------------------------------------

INSERT INTO `zone` VALUES (1, 'Olympiad Stadia', 'Stadia 1', -19627, -19712, -22024, -22322, -3026, 0, 0);
INSERT INTO `zone` VALUES (2, 'Olympiad Stadia', 'Stadia 2', -119100, -223705, -121484, -226316, -3331, 0, 0);
INSERT INTO `zone` VALUES (3, 'Olympiad Stadia', 'Stadia 3', -103889, -210201, -101325, -207724, -3331, 0, 0);
INSERT INTO `zone` VALUES (4, 'Olympiad Stadia', 'Stadia 4', -119079, -206078, -121438, -208668, -3331, 0, 0);
INSERT INTO `zone` VALUES (5, 'Olympiad Stadia', 'Stadia 5', -88700, -226280, -86351, -223722, -3331, 0, 0);
INSERT INTO `zone` VALUES (6, 'Olympiad Stadia', 'Stadia 6', -80586, -211911, -82939, -214487, -3331, 0, 0);
INSERT INTO `zone` VALUES (7, 'Olympiad Stadia', 'Stadia 7', -88659, -208652, -86297, -206075, -3331, 0, 0);
INSERT INTO `zone` VALUES (8, 'Olympiad Stadia', 'Stadia 8', -95000, -219531, -92632, -216950, -3331, 0, 0);
INSERT INTO `zone` VALUES (9, 'Olympiad Stadia', 'Stadia 9', -75936, -217408, -78306, -220017, -3331, 0, 0);
INSERT INTO `zone` VALUES (10, 'Olympiad Stadia', 'Stadia 10', -68560, -207718, -70933, -210312, -3331, 0, 0);
INSERT INTO `zone` VALUES (11, 'Olympiad Stadia', 'Stadia 11', -78008, -202528, -75663, -199943, -3331, 0, 0);
INSERT INTO `zone` VALUES (12, 'Olympiad Stadia', 'Stadia 12', -108690, -217403, -111072, -220023, -3331, 0, 0);
INSERT INTO `zone` VALUES (13, 'Olympiad Stadia', 'Stadia 13', -127766, -219555, -125394, -216946, -3331, 0, 0);
INSERT INTO `zone` VALUES (14, 'Olympiad Stadia', 'Stadia 14', -108428, -199935, -110796, -202541, -3331, 0, 0);
INSERT INTO `zone` VALUES (15, 'Olympiad Stadia', 'Stadia 15', -88677, -241444, -86294, -238836, -3331, 0, 0);
INSERT INTO `zone` VALUES (16, 'Olympiad Stadia', 'Stadia 16', -82938, -247261, -80580, -244668, -3331, 0, 0);
INSERT INTO `zone` VALUES (17, 'Olympiad Stadia', 'Stadia 17', -75930, -250175, -78298, -252779, -3331, 0, 0);
INSERT INTO `zone` VALUES (18, 'Olympiad Stadia', 'Stadia 18', -70920, -243079, -68547, -240473, -3331, 0, 0);
INSERT INTO `zone` VALUES (19, 'Olympiad Stadia', 'Stadia 19', -75670, -232712, -78027, -235326, -3331, 0, 0);
INSERT INTO `zone` VALUES (20, 'Olympiad Stadia', 'Stadia 20', -92632, -249706, -94999, -252316, -3331, 0, 0);
INSERT INTO `zone` VALUES (21, 'Olympiad Stadia', 'Stadia 21', -88896, -258868, -86332, -256466, -3331, 0, 0);
INSERT INTO `zone` VALUES (22, 'Olympiad Stadia', 'Stadia 22', -113332, -211881, -115713, -214513, -3331, 0, 0);

-- -----------------------------------------
-- MotherTree Zones
-- -----------------------------------------

INSERT INTO `zone` VALUES (1, 'MotherTree', 'Shadow of the Mother Tree', 47600, 38290, 44483, 41745, -3491, 0, 0);
INSERT INTO `zone` VALUES (2, 'MotherTree', 'Elven Village', 46249, 50036, 44431, 49176, -3060, 0, 0);
INSERT INTO `zone` VALUES (3, 'MotherTree', 'East Elven Village', 20580, 51713, 21667, 50393, -3690, 0, 0);
INSERT INTO `zone` VALUES (4, 'MotherTree', 'Elven Fortress', 24993, 80655, 25454, 82314, -3165, 0, 0);
INSERT INTO `zone` VALUES (5, 'MotherTree', 'Iris Lake', 57052, 85445, 57937, 87057, -3658, 0, 0);

-- -----------------------------------------
-- Fishing Zones
-- -----------------------------------------

INSERT INTO `zone` VALUES ('1', 'Fishing', 'Water1', '-131072', '98304', '-98304', '131072', '-3780',0, '0');
INSERT INTO `zone` VALUES ('2', 'Fishing', 'Water2', '-131072', '131072', '-98304', '163840', '-3780',0, '0');
INSERT INTO `zone` VALUES ('3', 'Fishing', 'Water3', '-131072', '163840', '-98304', '196608', '-3780',0, '0');
INSERT INTO `zone` VALUES ('4', 'Fishing', 'Water4', '-131072', '196608', '-98304', '229376', '-3780',0, '0');
INSERT INTO `zone` VALUES ('5', 'Fishing', 'Water5', '-131072', '237205', '-98304', '262144', '-3780',0, '0');
INSERT INTO `zone` VALUES ('6', 'Fishing', 'Water6', '-98304', '65536', '-85504', '98304', '-3780',0, '0');
INSERT INTO `zone` VALUES ('7', 'Fishing', 'Water7', '-85504', '65536', '-65536', '80384', '-3780',0, '0');
INSERT INTO `zone` VALUES ('8', 'Fishing', 'Water8', '-85056', '86208', '-84416', '86848', '-3504',0, '0');
INSERT INTO `zone` VALUES ('9', 'Fishing', 'Water9', '-84416', '86208', '-82944', '86848', '-5248',0, '0');
INSERT INTO `zone` VALUES ('10', 'Fishing', 'Water10', '-98304', '121072', '-88304', '131072', '-3780',0, '0');
INSERT INTO `zone` VALUES ('11', 'Fishing', 'Water11', '-98304', '98304', '-96804', '99804', '-3780',0, '0');
INSERT INTO `zone` VALUES ('12', 'Fishing', 'Water12', '-68536', '128072', '-65536', '131072', '-3179',0, '0');
INSERT INTO `zone` VALUES ('13', 'Fishing', 'Water13', '-84032', '111040', '-83392', '111680', '-3248',0, '0');
INSERT INTO `zone` VALUES ('14', 'Fishing', 'Water14', '-83392', '111040', '-81920', '111680', '-4992',0, '0');
INSERT INTO `zone` VALUES ('15', 'Fishing', 'Water15', '-98304', '131072', '-78304', '163840', '-3780',0, '0');
INSERT INTO `zone` VALUES ('16', 'Fishing', 'Water16', '-69836', '131072', '-65536', '140072', '-3179',0, '0');
INSERT INTO `zone` VALUES ('17', 'Fishing', 'Water17', '-98304', '163840', '-65536', '196608', '-3780',0, '0');
INSERT INTO `zone` VALUES ('18', 'Fishing', 'Water18', '-98304', '196608', '-65536', '229376', '-3780',0, '0');
INSERT INTO `zone` VALUES ('19', 'Fishing', 'Water19', '-98304', '229376', '-65536', '262144', '-3780',0, '0');
INSERT INTO `zone` VALUES ('20', 'Fishing', 'Water20', '-65536', '-103304', '-32768', '-98304', '-3780',0, '0');
INSERT INTO `zone` VALUES ('21', 'Fishing', 'Water21', '-65536', '32768', '-52036', '65536', '-3780',0, '0');
INSERT INTO `zone` VALUES ('22', 'Fishing', 'Water22', '-52036', '32768', '-32768', '40768', '-3780',0, '0');
INSERT INTO `zone` VALUES ('23', 'Fishing', 'Water23', '-65536', '65536', '-55536', '75536', '-3780',0, '0');
INSERT INTO `zone` VALUES ('24', 'Fishing', 'Water24', '-55360', '78784', '-53888', '79424', '-4832',0, '0');
INSERT INTO `zone` VALUES ('25', 'Fishing', 'Water25', '-56000', '78784', '-55360', '79424', '-3088',0, '0');
INSERT INTO `zone` VALUES ('26', 'Fishing', 'Water26', '-65536', '122368', '-52480', '131072', '-3179',0, '0');
INSERT INTO `zone` VALUES ('27', 'Fishing', 'Water27', '-65536', '113088', '-57664', '121856', '-3659',0, '0');
INSERT INTO `zone` VALUES ('28', 'Fishing', 'Water28', '-65536', '131072', '-62464', '141056', '-3179',0, '0');
INSERT INTO `zone` VALUES ('29', 'Fishing', 'Water29', '-61764', '131072', '-55748', '132608', '-3179',0, '0');
INSERT INTO `zone` VALUES ('30', 'Fishing', 'Water30', '-65536', '191350', '-53536', '196608', '-3780',0, '0');
INSERT INTO `zone` VALUES ('31', 'Fishing', 'Water31', '-65535', '179446', '-62962', '191446', '-3780',0, '0');
INSERT INTO `zone` VALUES ('32', 'Fishing', 'Water32', '-54681', '180234', '-53049', '183018', '-4702',0, '0');
INSERT INTO `zone` VALUES ('33', 'Fishing', 'Water33', '-53616', '183018', '-52368', '184394', '-4702',0, '0');
INSERT INTO `zone` VALUES ('34', 'Fishing', 'Water34', '-41888', '206688', '-41248', '208160', '-5177',0, '0');
INSERT INTO `zone` VALUES ('35', 'Fishing', 'Water35', '-65536', '196608', '-53536', '229376', '-3780',0, '0');
INSERT INTO `zone` VALUES ('36', 'Fishing', 'Water36', '-53536', '220976', '-32768', '229376', '-3780',0, '0');
INSERT INTO `zone` VALUES ('37', 'Fishing', 'Water37', '-41024', '212576', '-32768', '220976', '-3780',0, '0');
INSERT INTO `zone` VALUES ('38', 'Fishing', 'Water38', '-53536', '212576', '-45280', '220976', '-3780',0, '0');
INSERT INTO `zone` VALUES ('39', 'Fishing', 'Water39', '-53536', '202208', '-47584', '209584', '-3780',0, '0');
INSERT INTO `zone` VALUES ('40', 'Fishing', 'Water40', '-45280', '212576', '-41024', '220976', '-3780',0, '0');
INSERT INTO `zone` VALUES ('41', 'Fishing', 'Water41', '-41888', '206048', '-41248', '206688', '-3434',0, '0');
INSERT INTO `zone` VALUES ('42', 'Fishing', 'Water42', '-65536', '229376', '-32768', '262144', '-3780',0, '0');
INSERT INTO `zone` VALUES ('43', 'Fishing', 'Water43', '-32768', '-101304', '-29768', '-98304', '-3780',0, '0');
INSERT INTO `zone` VALUES ('44', 'Fishing', 'Water44', '-23168', '13184', '-22528', '13824', '-3248',0, '0');
INSERT INTO `zone` VALUES ('45', 'Fishing', 'Water45', '-22528', '13184', '-21056', '13824', '-4991',0, '0');
INSERT INTO `zone` VALUES ('46', 'Fishing', 'Water46', '-12972', '57536', '-5972', '65536', '-3784',0, '0');
INSERT INTO `zone` VALUES ('47', 'Fishing', 'Water47', '-5840', '60646', '-3840', '62646', '-3788',0, '0');
INSERT INTO `zone` VALUES ('48', 'Fishing', 'Water48', '-25472', '77056', '-24832', '77696', '-3520',0, '0');
INSERT INTO `zone` VALUES ('49', 'Fishing', 'Water49', '-24832', '77056', '-23360', '77696', '-5263',0, '0');
INSERT INTO `zone` VALUES ('50', 'Fishing', 'Water50', '-32768', '209376', '-10000', '229376', '-3780',0, '0');
INSERT INTO `zone` VALUES ('51', 'Fishing', 'Water51', '4608', '65480', '32768', '70344', '-3794',0, '0');
INSERT INTO `zone` VALUES ('52', 'Fishing', 'Water52', '32768', '32768', '60672', '65536', '-3780',0, '0');
INSERT INTO `zone` VALUES ('53', 'Fishing', 'Water53', '32768', '65536', '63568', '90536', '-3780',0, '0');
INSERT INTO `zone` VALUES ('54', 'Fishing', 'Water54', '63236', '73140', '65536', '75440', '-3780',0, '0');
INSERT INTO `zone` VALUES ('55', 'Fishing', 'Water55', '43517', '116837', '52517', '126093', '-3780',0, '0');
INSERT INTO `zone` VALUES ('56', 'Fishing', 'Water56', '48773', '115910', '50237', '116709', '-2227',0, '0');
INSERT INTO `zone` VALUES ('57', 'Fishing', 'Water57', '46597', '111629', '49597', '115129', '-2259',0, '0');
INSERT INTO `zone` VALUES ('58', 'Fishing', 'Water58', '49172', '115129', '50136', '116869', '-2259',0, '0');
INSERT INTO `zone` VALUES ('59', 'Fishing', 'Water59', '48000', '115129', '49172', '116357', '-2259',0, '0');
INSERT INTO `zone` VALUES ('60', 'Fishing', 'Water60', '44928', '126976', '45568', '127616', '-3776',0, '0');
INSERT INTO `zone` VALUES ('61', 'Fishing', 'Water61', '44928', '125504', '45568', '126976', '-5504',0, '0');
INSERT INTO `zone` VALUES ('62', 'Fishing', 'Water62', '41917', '139480', '51917', '152770', '-3749',0, '0');
INSERT INTO `zone` VALUES ('63', 'Fishing', 'Water63', '39296', '143616', '39936', '144256', '-3728',0, '0');
INSERT INTO `zone` VALUES ('64', 'Fishing', 'Water64', '39936', '143616', '41408', '144256', '-5471',0, '0');
INSERT INTO `zone` VALUES ('65', 'Fishing', 'Water65', '32768', '181240', '65536', '196608', '-3780',0, '0');
INSERT INTO `zone` VALUES ('66', 'Fishing', 'Water66', '32768', '175600', '44032', '181240', '-3780',0, '0');
INSERT INTO `zone` VALUES ('67', 'Fishing', 'Water67', '57472', '175600', '65536', '181240', '-3780',0, '0');
INSERT INTO `zone` VALUES ('68', 'Fishing', 'Water68', '42496', '169984', '43136', '170624', '-3328',0, '0');
INSERT INTO `zone` VALUES ('69', 'Fishing', 'Water69', '43136', '169984', '44608', '170624', '-5071',0, '0');
INSERT INTO `zone` VALUES ('70', 'Fishing', 'Water70', '32768', '196608', '65536', '229376', '-3780',0, '0');
INSERT INTO `zone` VALUES ('71', 'Fishing', 'Water71', '65536', '4768', '75536', '32768', '-3780',0, '0');
INSERT INTO `zone` VALUES ('72', 'Fishing', 'Water72', '75536', '27768', '98304', '32768', '-3780',0, '0');
INSERT INTO `zone` VALUES ('73', 'Fishing', 'Water73', '84727', '32768', '91727', '53768', '-3780',0, '0');
INSERT INTO `zone` VALUES ('74', 'Fishing', 'Water74', '65536', '91304', '98304', '98304', '-3772',0, '0');
INSERT INTO `zone` VALUES ('75', 'Fishing', 'Water75', '65536', '71304', '70536', '91304', '-3772',0, '0');
INSERT INTO `zone` VALUES ('76', 'Fishing', 'Water76', '73984', '78080', '74624', '78720', '-3472',0, '0');
INSERT INTO `zone` VALUES ('77', 'Fishing', 'Water77', '74624', '78080', '76096', '78720', '-5215',0, '0');
INSERT INTO `zone` VALUES ('78', 'Fishing', 'Water78', '78304', '139840', '98304', '163840', '-3780',0, '0');
INSERT INTO `zone` VALUES ('79', 'Fishing', 'Water79', '65536', '163840', '74752', '196608', '-3780',0, '0');
INSERT INTO `zone` VALUES ('80', 'Fishing', 'Water80', '74752', '163840', '85215', '173976', '-3780',0, '0');
INSERT INTO `zone` VALUES ('81', 'Fishing', 'Water81', '74752', '173976', '83244', '178267', '-3780',0, '0');
INSERT INTO `zone` VALUES ('82', 'Fishing', 'Water82', '74752', '178267', '78031', '180291', '-3780',0, '0');
INSERT INTO `zone` VALUES ('83', 'Fishing', 'Water83', '91149', '163840', '98304', '166737', '-3780',0, '0');
INSERT INTO `zone` VALUES ('84', 'Fishing', 'Water84', '95485', '166737', '98304', '193105', '-3780',0, '0');
INSERT INTO `zone` VALUES ('85', 'Fishing', 'Water85', '92290', '173976', '95485', '185103', '-3780',0, '0');
INSERT INTO `zone` VALUES ('86', 'Fishing', 'Water86', '92290', '193105', '98304', '196608', '-3780',0, '0');
INSERT INTO `zone` VALUES ('87', 'Fishing', 'Water87', '93696', '224128', '98304', '229376', '-3780',0, '0');
INSERT INTO `zone` VALUES ('88', 'Fishing', 'Water88', '93056', '196608', '98304', '201216', '-3780',0, '0');
INSERT INTO `zone` VALUES ('89', 'Fishing', 'Water89', '65536', '196608', '76032', '200320', '-3780',0, '0');
INSERT INTO `zone` VALUES ('90', 'Fishing', 'Water90', '70024', '222217', '77862', '229376', '-3780',0, '0');
INSERT INTO `zone` VALUES ('91', 'Fishing', 'Water91', '65536', '200320', '70024', '229376', '-3780',0, '0');
INSERT INTO `zone` VALUES ('92', 'Fishing', 'Water92', '70024', '200320', '79174', '222217', '-3780',0, '0');
INSERT INTO `zone` VALUES ('93', 'Fishing', 'Water93', '77862', '224128', '93696', '229376', '-3780',0, '0');
INSERT INTO `zone` VALUES ('94', 'Fishing', 'Water94', '80192', '201216', '98304', '224128', '-3780',0, '0');
INSERT INTO `zone` VALUES ('95', 'Fishing', 'Water95', '81856', '196608', '93056', '201216', '-3780',0, '0');
INSERT INTO `zone` VALUES ('96', 'Fishing', 'Water96', '79360', '208897', '80000', '209537', '-3786',0, '0');
INSERT INTO `zone` VALUES ('97', 'Fishing', 'Water97', '80000', '208897', '81472', '209537', '-5529',0, '0');
INSERT INTO `zone` VALUES ('98', 'Fishing', 'Water98', '79174', '209727', '80192', '224128', '-3780',0, '0');
INSERT INTO `zone` VALUES ('99', 'Fishing', 'Water99', '79174', '201216', '80192', '208704', '-3780',0, '0');
INSERT INTO `zone` VALUES ('100', 'Fishing', 'Water100', '79010', '246661', '86498', '254711', '-10886',0, '0');
INSERT INTO `zone` VALUES ('101', 'Fishing', 'Water101', '84195', '239660', '86179', '242732', '-4203',0, '0');
INSERT INTO `zone` VALUES ('102', 'Fishing', 'Water102', '82979', '241472', '84195', '241920', '-6763',0, '0');
INSERT INTO `zone` VALUES ('103', 'Fishing', 'Water103', '80356', '245013', '84644', '245845', '-8898',0, '0');
INSERT INTO `zone` VALUES ('104', 'Fishing', 'Water104', '80303', '258157', '84591', '258989', '-8898',0, '0');
INSERT INTO `zone` VALUES ('105', 'Fishing', 'Water105', '65536', '229376', '98304', '262144', '-3780',0, '0');
INSERT INTO `zone` VALUES ('106', 'Fishing', 'Water106', '78624', '254711', '82336', '255593', '-10886',0, '0');
INSERT INTO `zone` VALUES ('107', 'Fishing', 'Water107', '82785', '254711', '86497', '255593', '-10886',0, '0');
INSERT INTO `zone` VALUES ('108', 'Fishing', 'Water108', '82336', '255351', '82785', '255593', '-10886',0, '0');
INSERT INTO `zone` VALUES ('109', 'Fishing', 'Water109', '98304', '-229376', '131072', '-196608', '-3780',0, '0');
INSERT INTO `zone` VALUES ('110', 'Fishing', 'Water110', '119040', '-196608', '131072', '-178624', '-3780',0, '0');
INSERT INTO `zone` VALUES ('111', 'Fishing', 'Water111', '98305', '27768', '120321', '32768', '-3780',0, '0');
INSERT INTO `zone` VALUES ('112', 'Fishing', 'Water112', '103091', '37760', '128091', '62760', '-4656',0, '0');
INSERT INTO `zone` VALUES ('113', 'Fishing', 'Water113', '110208', '84224', '110848', '84864', '-4864',0, '0');
INSERT INTO `zone` VALUES ('114', 'Fishing', 'Water114', '110848', '84224', '112320', '84864', '-6631',0, '0');
INSERT INTO `zone` VALUES ('115', 'Fishing', 'Water115', '98304', '138240', '131072', '163840', '-3780',0, '0');
INSERT INTO `zone` VALUES ('116', 'Fishing', 'Water116', '112096', '142624', '119232', '147604', '-2892',0, '0');
INSERT INTO `zone` VALUES ('117', 'Fishing', 'Water117', '117509', '136740', '122009', '138240', '-3780',0, '0');
INSERT INTO `zone` VALUES ('118', 'Fishing', 'Water118', '117509', '136740', '122009', '138240', '-3780',0, '0');
INSERT INTO `zone` VALUES ('119', 'Fishing', 'Water119', '117509', '136740', '122009', '138240', '-3780',0, '0');
INSERT INTO `zone` VALUES ('120', 'Fishing', 'Water120', '114560', '132480', '115200', '133120', '-3184',0, '0');
INSERT INTO `zone` VALUES ('121', 'Fishing', 'Water121', '115200', '132480', '116672', '133120', '-4928',0, '0');
INSERT INTO `zone` VALUES ('122', 'Fishing', 'Water122', '98304', '163840', '131072', '167840', '-3780',0, '0');
INSERT INTO `zone` VALUES ('123', 'Fishing', 'Water123', '98304', '167840', '102304', '196608', '-3780',0, '0');
INSERT INTO `zone` VALUES ('124', 'Fishing', 'Water124', '124072', '167840', '131072', '196608', '-3780',0, '0');
INSERT INTO `zone` VALUES ('125', 'Fishing', 'Water125', '102304', '191608', '124072', '196608', '-3780',0, '0');
INSERT INTO `zone` VALUES ('126', 'Fishing', 'Water126', '121672', '186199', '124072', '189151', '-3780',0, '0');
INSERT INTO `zone` VALUES ('127', 'Fishing', 'Water127', '102304', '184170', '105652', '187250', '-3780',0, '0');
INSERT INTO `zone` VALUES ('128', 'Fishing', 'Water128', '102304', '168567', '106804', '173567', '-3780',0, '0');
INSERT INTO `zone` VALUES ('129', 'Fishing', 'Water129', '107520', '173696', '108160', '174336', '-3776',0, '0');
INSERT INTO `zone` VALUES ('130', 'Fishing', 'Water130', '108160', '173696', '109632', '174336', '-5529',0, '0');
INSERT INTO `zone` VALUES ('131', 'Fishing', 'Water131', '98304', '196608', '131072', '229376', '-3780',0, '0');
INSERT INTO `zone` VALUES ('132', 'Fishing', 'Water132', '98304', '229376', '131072', '262144', '-3780',0, '0');
INSERT INTO `zone` VALUES ('133', 'Fishing', 'Water133', '131072', '-229376', '163840', '-196608', '-3780',0, '0');
INSERT INTO `zone` VALUES ('134', 'Fishing', 'Water134', '131072', '-196608', '163840', '-178608', '-3780',0, '0');
INSERT INTO `zone` VALUES ('135', 'Fishing', 'Water135', '145081', '25139', '149881', '29939', '-2528',0, '0');
INSERT INTO `zone` VALUES ('136', 'Fishing', 'Water136', '131059', '32768', '163827', '65536', '-3776',0, '0');
INSERT INTO `zone` VALUES ('137', 'Fishing', 'Water137', '136648', '79304', '137400', '80056', '-3736',0, '0');
INSERT INTO `zone` VALUES ('138', 'Fishing', 'Water138', '137400', '79360', '138816', '80000', '-5519',0, '0');
INSERT INTO `zone` VALUES ('139', 'Fishing', 'Water139', '137472', '65536', '163840', '98304', '-3780',0, '0');
INSERT INTO `zone` VALUES ('140', 'Fishing', 'Water140', '131072', '126072', '136072', '131072', '-3780',0, '0');
INSERT INTO `zone` VALUES ('141', 'Fishing', 'Water141', '163840', '-229376', '196608', '-196608', '-3780',0, '0');
INSERT INTO `zone` VALUES ('142', 'Fishing', 'Water142', '163840', '-196608', '168840', '-186608', '-3780',0, '0');
INSERT INTO `zone` VALUES ('143', 'Fishing', 'Water143', '173411', '-187756', '186042', '-182534', '-4465',0, '0');
INSERT INTO `zone` VALUES ('144', 'Fishing', 'Water144', '174206', '-182687', '176749', '-179167', '-2066',0, '0');
INSERT INTO `zone` VALUES ('145', 'Fishing', 'Water145', '170908', '-116846', '173748', '-113953', '-4033',0, '0');
INSERT INTO `zone` VALUES ('146', 'Fishing', 'Water146', '170926', '-113936', '173766', '-111043', '-5256',0, '0');
INSERT INTO `zone` VALUES ('147', 'Fishing', 'Water147', '163840', '65760', '173840', '95072', '-3780',0, '0');
INSERT INTO `zone` VALUES ('148', 'Fishing', 'Water148', '168576', '-17920', '169216', '-17280', '-3248',0, '0');
INSERT INTO `zone` VALUES ('149', 'Fishing', 'Water149', '169216', '-17920', '170688', '-17280', '-4991',0, '0');
INSERT INTO `zone` VALUES ('150', 'Fishing', 'Water150', '32768', '-98304', '65536', '-65536', '-3780',0, '0');
INSERT INTO `zone` VALUES ('151', 'Fishing', 'Water151', '32768', '-65536', '65536', '-32768', '-3784',0, '0');
INSERT INTO `zone` VALUES ('152', 'Fishing', 'Water152', '65536', '-40960', '98304', '-32768', '-3780',0, '0');
INSERT INTO `zone` VALUES ('153', 'Fishing', 'Water153', '91829', '-55930', '95637', '-50266', '-2633',0, '0');
INSERT INTO `zone` VALUES ('154', 'Fishing', 'Water154', '87765', '-54554', '91829', '-50266', '-2633',0, '0');
INSERT INTO `zone` VALUES ('155', 'Fishing', 'Water155', '84947', '-50234', '90133', '-46202', '-3152',0, '0');
INSERT INTO `zone` VALUES ('156', 'Fishing', 'Water156', '76003', '-46368', '84947', '-39968', '-3248',0, '0');
INSERT INTO `zone` VALUES ('157', 'Fishing', 'Water157', '68852', '-62008', '74036', '-56192', '-3161',0, '0');
INSERT INTO `zone` VALUES ('158', 'Fishing', 'Water158', '69480', '-56192', '77032', '-53203', '-3161',0, '0');
INSERT INTO `zone` VALUES ('159', 'Fishing', 'Water159', '71311', '-53203', '76175', '-50292', '-3161',0, '0');
INSERT INTO `zone` VALUES ('160', 'Fishing', 'Water160', '75059', '-63591', '78355', '-57517', '-2352',0, '0');
INSERT INTO `zone` VALUES ('161', 'Fishing', 'Water161', '78355', '-65184', '83859', '-51872', '-2352',0, '0');
INSERT INTO `zone` VALUES ('162', 'Fishing', 'Water162', '83859', '-64826', '87187', '-55418', '-2352',0, '0');
INSERT INTO `zone` VALUES ('163', 'Fishing', 'Water163', '87187', '-62909', '90963', '-58301', '-2352',0, '0');
INSERT INTO `zone` VALUES ('164', 'Fishing', 'Water164', '77283', '-51872', '83747', '-49440', '-2352',0, '0');
INSERT INTO `zone` VALUES ('165', 'Fishing', 'Water165', '83859', '-55418', '86227', '-52602', '-2352',0, '0');
INSERT INTO `zone` VALUES ('166', 'Fishing', 'Water166', '69635', '-47328', '76003', '-39776', '-3248',0, '0');
INSERT INTO `zone` VALUES ('167', 'Fishing', 'Water167', '87187', '-58301', '89235', '-56541', '-2352',0, '0');
INSERT INTO `zone` VALUES ('168', 'Fishing', 'Water168', '76800', '-32768', '98304', '-18432', '-3780',0, '0');
INSERT INTO `zone` VALUES ('169', 'Fishing', 'Giran North Entrance1', 82480, 143048, 83321, 141782, 0,0, 0);
INSERT INTO `zone` VALUES ('170', 'Fishing', 'Giran North Entrance2', 82109, 142550, 82211, 142149, 0,0, 0);
INSERT INTO `zone` VALUES ('171', 'Fishing', 'Giran North Entrance3', 82235, 141780, 82700, 142718, 0,0, 0);

-- -----------------------------------------
-- Water Zones
-- -----------------------------------------

INSERT INTO `zone` VALUES (1, 'Water', '16_21_water1', -131072, 98303, -98304, 131071, -4781, -3781, 0); 
INSERT INTO `zone` VALUES (2, 'Water', '16_22_water1', -131072, 131072, -98304, 163840, -4781, -3781, 0); 
INSERT INTO `zone` VALUES (3, 'Water', '16_23_water1', -131072, 163840, -98304, 196608, -4781, -3781, 0); 
INSERT INTO `zone` VALUES (4, 'Water', '16_24_water1', -131072, 196607, -98304, 229375, -7780, -3780, 0); 
INSERT INTO `zone` VALUES (5, 'Water', '16_25_water1', -131072, 237205, -98304, 262144, -4780, -3780, 0); 
INSERT INTO `zone` VALUES (6, 'Water', '17_20_water1', -98304, 65536, -85504, 98304, -4781, -3781, 0); 
INSERT INTO `zone` VALUES (7, 'Water', '17_20_water2', -85504, 65536, -65536, 80384, -4781, -3781, 0); 
INSERT INTO `zone` VALUES (8, 'Water', '17_20_water3', -85056, 86208, -84416, 86848, -5824, -3504, 0); 
INSERT INTO `zone` VALUES (9, 'Water', '17_20_water4', -84416, 86208, -82944, 86848, -5824, -5248, 0); 
INSERT INTO `zone` VALUES (10, 'Water', '17_21_water1', -98304, 121072, -88304, 131072, -4780, -3780, 0); 
INSERT INTO `zone` VALUES (11, 'Water', '17_21_water2', -98304, 98304, -96804, 99804, -4780, -3780, 0); 
INSERT INTO `zone` VALUES (12, 'Water', '17_21_water3', -68536, 128072, -65536, 131072, -4179, -3179, 0); 
INSERT INTO `zone` VALUES (13, 'Water', '17_21_water4', -84032, 111040, -83392, 111680, -5568, -3248, 0); 
INSERT INTO `zone` VALUES (14, 'Water', '17_21_water5', -83392, 111040, -81920, 111680, -5568, -4992, 0); 
INSERT INTO `zone` VALUES (15, 'Water', '17_22_water1', -98304, 131072, -78304, 163840, -4780, -3780, 0); 
INSERT INTO `zone` VALUES (16, 'Water', '17_22_water2', -69836, 131072, -65536, 140072, -4179, -3179, 0); 
INSERT INTO `zone` VALUES (17, 'Water', '17_23_water1', -98304, 163840, -65536, 196608, -7780, -3780, 0); 
INSERT INTO `zone` VALUES (18, 'Water', '17_24_water1', -98304, 196608, -65536, 229376, -4780, -3780, 0); 
INSERT INTO `zone` VALUES (19, 'Water', '17_25_water1', -98304, 229375, -65536, 262143, -4780, -3780, 0); 
INSERT INTO `zone` VALUES (20, 'Water', '18_14_water1', -65536, -103305, -32769, -98305, -4781, -3781, 0); 
INSERT INTO `zone` VALUES (21, 'Water', '18_19_water1', -65537, 32767, -52037, 65535, -4780, -3780, 0); 
INSERT INTO `zone` VALUES (22, 'Water', '18_19_water2', -52037, 32768, -32769, 40768, -4780, -3780, 0); 
INSERT INTO `zone` VALUES (23, 'Water', '18_20_water1', -65537, 65536, -55537, 75536, -4780, -3780, 0); 
INSERT INTO `zone` VALUES (24, 'Water', '18_20_water2', -55360, 78784, -53888, 79424, -5408, -4832, 0); 
INSERT INTO `zone` VALUES (25, 'Water', '18_20_water3', -56000, 78784, -55360, 79424, -5408, -3088, 0); 
INSERT INTO `zone` VALUES (26, 'Water', '18_21_water1', -65536, 122368, -52480, 131072, -4179, -3179, 0); 
INSERT INTO `zone` VALUES (27, 'Water', '18_21_water1', -65536, 113088, -57664, 121856, -4659, -3659, 0); 
INSERT INTO `zone` VALUES (28, 'Water', '18_22_water1', -65536, 131072, -62464, 141056, -4179, -3179, 0); 
INSERT INTO `zone` VALUES (29, 'Water', '18_22_water2', -61764, 131072, -55748, 132608, -4179, -3179, 0); 
INSERT INTO `zone` VALUES (30, 'Water', '18_23_water1', -65536, 191350, -53536, 196608, -4780, -3780, 0); 
INSERT INTO `zone` VALUES (31, 'Water', '18_23_water2', -65535, 179446, -62962, 191446, -4780, -3780, 0); 
INSERT INTO `zone` VALUES (32, 'Water', '18_23_water3', -54682, 180233, -53050, 183017, -4960, -4702, 0); 
INSERT INTO `zone` VALUES (33, 'Water', '18_23_water4', -53617, 183018, -52369, 184394, -4960, -4702, 0); 
INSERT INTO `zone` VALUES (34, 'Water', '18_24_water1', -41889, 206688, -41249, 208160, -5754, -5177, 0); 
INSERT INTO `zone` VALUES (35, 'Water', '18_24_water2', -65536, 196608, -53536, 229376, -4780, -3780, 0); 
INSERT INTO `zone` VALUES (36, 'Water', '18_24_water3', -53536, 220976, -32768, 229376, -4780, -3780, 0); 
INSERT INTO `zone` VALUES (37, 'Water', '18_24_water4', -41024, 212576, -32768, 220976, -4780, -3780, 0); 
INSERT INTO `zone` VALUES (38, 'Water', '18_24_water5', -53536, 212576, -45280, 220976, -4282, -3780, 0); 
INSERT INTO `zone` VALUES (39, 'Water', '18_24_water6', -53536, 202208, -47584, 209584, -4282, -3780, 0); 
INSERT INTO `zone` VALUES (40, 'Water', '18_24_water7', -45280, 212576, -41024, 220976, -4282, -3780, 0); 
INSERT INTO `zone` VALUES (41, 'Water', '18_24_water8', -41889, 206048, -41249, 206688, -5754, -3434, 0); 
INSERT INTO `zone` VALUES (42, 'Water', '18_25_water1', -65536, 229375, -32768, 262143, -4780, -3780, 0); 
INSERT INTO `zone` VALUES (43, 'Water', '19_14_water1', -32768, -101305, -29768, -98305, -4780, -3780, 0); 
INSERT INTO `zone` VALUES (44, 'Water', '19_15_water1', -32769, -98304, -1, -65536, -4780, -3780, 0); 
INSERT INTO `zone` VALUES (45, 'Water', '19_18_water1', -32768, 0, -23424, 32768, -4780, -3780, 0); 
INSERT INTO `zone` VALUES (46, 'Water', '19_18_water2', -23168, 13184, -22528, 13824, -5568, -3248, 0); 
INSERT INTO `zone` VALUES (47, 'Water', '19_18_water3', -22528, 13184, -21056, 13824, -5568, -4991, 0); 
INSERT INTO `zone` VALUES (48, 'Water', '19_18_water4', -23424, 0, 0, 12928, -4780, -3780, 0); 
INSERT INTO `zone` VALUES (49, 'Water', '19_19_water1', -12972, 57536, -5972, 65536, -4784, -3784, 0); 
INSERT INTO `zone` VALUES (50, 'Water', '19_19_water2', -5840, 60645, -3840, 62645, -4789, -3789, 0); 
INSERT INTO `zone` VALUES (51, 'Water', '19_20_water1', -24488, 65536, 0, 98304, -4268, -3780, 0); 
INSERT INTO `zone` VALUES (52, 'Water', '19_20_water2', -25472, 77056, -24832, 77696, -5840, -3520, 0); 
INSERT INTO `zone` VALUES (53, 'Water', '19_20_water3', -24832, 77056, -23360, 77696, -5840, -5263, 0); 
INSERT INTO `zone` VALUES (54, 'Water', '19_21_water1', -25816, 98304, 0, 131072, -10612, -3780, 0); 
INSERT INTO `zone` VALUES (55, 'Water', '19_24_water1', -32768, 209376, -10000, 229376, -7780, -3780, 0); 
INSERT INTO `zone` VALUES (56, 'Water', '19_24_water2', -10000, 196608, 0, 229376, -7780, -3780, 0); 
INSERT INTO `zone` VALUES (57, 'Water', '19_25_water1', -32768, 229376, 0, 262144, -4780, -3780, 0); 
INSERT INTO `zone` VALUES (58, 'Water', '20_15_water1', 0, -73537, 8000, -65537, -4781, -3781, 0); 
INSERT INTO `zone` VALUES (59, 'Water', '20_18_water1', 0, 0, 32768, 5500, -4780, -3780, 0); 
INSERT INTO `zone` VALUES (60, 'Water', '20_19_water1', 0, 32768, 32768, 65536, -4780, -3780, 0); 
INSERT INTO `zone` VALUES (61, 'Water', '20_20_water1', 0, 76174, 4000, 98304, -4780, -3780, 0); 
INSERT INTO `zone` VALUES (62, 'Water', '20_20_water2', 4608, 65479, 32768, 70343, -4795, -3795, 0); 
INSERT INTO `zone` VALUES (63, 'Water', '20_20_water3', 0, 66791, 4624, 72935, -4795, -3795, 0); 
INSERT INTO `zone` VALUES (64, 'Water', '20_21_water1', 0, 98304, 4300, 131072, -4781, -3781, 0); 
INSERT INTO `zone` VALUES (65, 'Water', '20_22_water1', 0, 131072, 7168, 163840, -4789, -3789, 0); 
INSERT INTO `zone` VALUES (66, 'Water', '20_23_water1', 0, 163840, 32768, 196608, -7780, -3780, 0); 
INSERT INTO `zone` VALUES (67, 'Water', '20_24_water1', -1, 196608, 32767, 229376, -4781, -3781, 0); 
INSERT INTO `zone` VALUES (68, 'Water', '21_18_water1', 32768, 0, 65536, 10000, -4780, -3780, 0); 
INSERT INTO `zone` VALUES (69, 'Water', '21_19_water1', 32768, 32767, 60672, 65535, -4781, -3781, 0); 
INSERT INTO `zone` VALUES (70, 'Water', '21_20_water1', 32767, 65535, 63567, 90535, -4781, -3781, 0); 
INSERT INTO `zone` VALUES (71, 'Water', '21_20_water2', 63236, 73140, 65536, 75440, -4780, -3780, 0); 
INSERT INTO `zone` VALUES (72, 'Water', '21_21_water1', 43516, 116836, 52516, 126092, -3981, -3781, 0); 
INSERT INTO `zone` VALUES (73, 'Water', '21_21_water2', 48773, 115910, 50237, 116708, -2377, -2227, 0); 
INSERT INTO `zone` VALUES (74, 'Water', '21_21_water3', 46597, 111628, 49597, 115128, -2409, -2259, 0); 
INSERT INTO `zone` VALUES (75, 'Water', '21_21_water4', 49172, 115128, 50136, 116868, -2409, -2259, 0); 
INSERT INTO `zone` VALUES (76, 'Water', '21_21_water5', 48000, 115128, 49172, 116356, -2409, -2259, 0); 
INSERT INTO `zone` VALUES (77, 'Water', '21_21_water6', 44928, 126976, 45568, 127616, -6080, -3776, 0); 
INSERT INTO `zone` VALUES (78, 'Water', '21_21_water7', 44928, 125504, 45568, 126976, -6080, -5504, 0); 
INSERT INTO `zone` VALUES (79, 'Water', '21_22_water1', 41916, 139479, 51916, 152769, -3949, -3749, 0); 
INSERT INTO `zone` VALUES (80, 'Water', '21_22_water2', 39296, 143616, 39936, 144256, -6048, -3728, 0); 
INSERT INTO `zone` VALUES (81, 'Water', '21_22_water3', 39936, 143616, 41408, 144256, -6048, -5471, 0); 
INSERT INTO `zone` VALUES (82, 'Water', '21_23_water1', 32768, 181239, 65536, 196607, -4781, -3781, 0); 
INSERT INTO `zone` VALUES (83, 'Water', '21_23_water2', 32768, 175599, 44032, 181239, -4781, -3781, 0); 
INSERT INTO `zone` VALUES (84, 'Water', '21_23_water3', 57472, 175599, 65536, 181239, -4781, -3781, 0); 
INSERT INTO `zone` VALUES (85, 'Water', '21_23_water4', 42496, 169984, 43136, 170624, -5648, -3328, 0); 
INSERT INTO `zone` VALUES (86, 'Water', '21_23_water5', 43136, 169984, 44608, 170624, -5648, -5071, 0); 
INSERT INTO `zone` VALUES (87, 'Water', '21_24_water1', 32768, 196608, 65536, 229376, -4780, -3780, 0); 
INSERT INTO `zone` VALUES (88, 'Water', '22_18_water1', 65536, 4768, 75536, 32768, -4780, -3780, 0); 
INSERT INTO `zone` VALUES (89, 'Water', '22_18_water2', 75536, 27768, 98304, 32768, -4780, -3780, 0); 
INSERT INTO `zone` VALUES (90, 'Water', '22_19_water1', 84727, 32768, 91727, 53768, -4780, -3780, 0); 
INSERT INTO `zone` VALUES (91, 'Water', '22_20_water1', 65536, 91304, 98304, 98304, -4772, -3772, 0); 
INSERT INTO `zone` VALUES (92, 'Water', '22_20_water2', 65536, 71304, 70536, 91304, -4772, -3772, 0); 
INSERT INTO `zone` VALUES (93, 'Water', '22_20_water3', 73984, 78080, 74624, 78720, -5792, -3472, 0); 
INSERT INTO `zone` VALUES (94, 'Water', '22_20_water4', 74624, 78080, 76096, 78720, -5792, -5215, 0); 
INSERT INTO `zone` VALUES (95, 'Water', '22_22_water1', 78304, 139840, 98304, 163840, -8778, -3780, 0); 
INSERT INTO `zone` VALUES (96, 'Water', '22_23_water1', 65536, 163840, 74752, 196608, -4780, -3780, 0); 
INSERT INTO `zone` VALUES (97, 'Water', '22_23_water2', 74752, 163840, 85215, 173976, -4780, -3780, 0); 
INSERT INTO `zone` VALUES (98, 'Water', '22_23_water3', 74752, 173976, 83244, 178267, -4780, -3780, 0); 
INSERT INTO `zone` VALUES (99, 'Water', '22_23_water4', 74752, 178267, 78031, 180291, -4780, -3780, 0); 
INSERT INTO `zone` VALUES (100, 'Water', '22_23_water5', 91149, 163840, 98304, 166737, -4780, -3780, 0); 
INSERT INTO `zone` VALUES (101, 'Water', '22_23_water6', 95485, 166737, 98304, 193105, -4780, -3780, 0); 
INSERT INTO `zone` VALUES (102, 'Water', '22_23_water7', 92290, 173976, 95485, 185103, -4780, -3780, 0); 
INSERT INTO `zone` VALUES (103, 'Water', '22_23_water8', 92290, 193105, 98304, 196608, -4780, -3780, 0); 
INSERT INTO `zone` VALUES (104, 'Water', '22_24_water1', 93696, 224128, 98304, 229376, -4780, -3780, 0); 
INSERT INTO `zone` VALUES (105, 'Water', '22_24_water2', 93056, 196608, 98304, 201216, -4780, -3780, 0); 
INSERT INTO `zone` VALUES (106, 'Water', '22_24_water3', 65536, 196608, 76032, 200320, -4780, -3780, 0); 
INSERT INTO `zone` VALUES (107, 'Water', '22_24_water4', 70024, 222217, 77862, 229376, -4780, -3780, 0); 
INSERT INTO `zone` VALUES (108, 'Water', '22_24_water5', 65536, 200320, 70024, 229376, -4780, -3780, 0); 
INSERT INTO `zone` VALUES (109, 'Water', '22_24_water6', 70024, 200320, 79174, 222217, -4268, -3780, 0); 
INSERT INTO `zone` VALUES (110, 'Water', '22_24_water7', 77862, 224128, 93696, 229376, -4268, -3780, 0); 
INSERT INTO `zone` VALUES (111, 'Water', '22_24_water8', 80192, 201216, 98304, 224128, -4268, -3780, 0); 
INSERT INTO `zone` VALUES (112, 'Water', '22_24_water9', 81856, 196608, 93056, 201216, -4268, -3780, 0); 
INSERT INTO `zone` VALUES (113, 'Water', '22_24_water10', 79360, 208897, 80000, 209537, -6106, -3786, 0); 
INSERT INTO `zone` VALUES (114, 'Water', '22_24_water11', 80000, 208897, 81472, 209537, -6106, -5529, 0); 
INSERT INTO `zone` VALUES (115, 'Water', '22_24_water12', 79174, 209727, 80192, 224128, -4268, -3780, 0); 
INSERT INTO `zone` VALUES (116, 'Water', '22_24_water13', 79174, 201216, 80192, 208704, -4268, -3780, 0); 
INSERT INTO `zone` VALUES (117, 'Water', '22_25_water1', 79010, 246660, 86498, 254710, -10903, -10887, 0); 
INSERT INTO `zone` VALUES (118, 'Water', '22_25_water2', 84195, 239659, 86179, 242731, -7220, -4204, 0); 
INSERT INTO `zone` VALUES (119, 'Water', '22_25_water3', 82979, 241471, 84195, 241919, -7220, -6764, 0); 
INSERT INTO `zone` VALUES (120, 'Water', '22_25_water4', 80356, 245012, 84644, 245844, -9355, -8899, 0); 
INSERT INTO `zone` VALUES (121, 'Water', '22_25_water5', 80303, 258156, 84591, 258988, -9355, -8899, 0); 
INSERT INTO `zone` VALUES (122, 'Water', '22_25_water6', 65536, 229376, 98304, 262144, -4780, -3780, 0); 
INSERT INTO `zone` VALUES (123, 'Water', '22_25_water7', 78624, 254710, 82336, 255593, -10903, -10887, 0); 
INSERT INTO `zone` VALUES (124, 'Water', '22_25_water8', 82785, 254710, 86497, 255593, -10903, -10887, 0); 
INSERT INTO `zone` VALUES (125, 'Water', '22_25_water9', 82336, 255350, 82785, 255593, -10903, -10887, 0); 
INSERT INTO `zone` VALUES (126, 'Water', '23_11_water1', 98304, -229376, 131072, -196608, -4780, -3780, 0); 
INSERT INTO `zone` VALUES (127, 'Water', '23_12_water1', 119040, -196608, 131072, -178624, -4780, -3780, 0); 
INSERT INTO `zone` VALUES (128, 'Water', '23_18_water1', 98304, 27767, 120320, 32767, -4781, -3781, 0); 
INSERT INTO `zone` VALUES (129, 'Water', '23_19_water1', 103091, 37760, 128091, 62760, -5656, -4656, 0); 
INSERT INTO `zone` VALUES (130, 'Water', '23_20_water1', 110208, 84224, 110848, 84864, -7184, -4864, 0); 
INSERT INTO `zone` VALUES (131, 'Water', '23_20_water2', 110848, 84224, 112320, 84864, -7208, -6631, 0); 
INSERT INTO `zone` VALUES (132, 'Water', '23_22_water1', 98304, 138239, 131072, 163839, -4781, -3781, 0); 
INSERT INTO `zone` VALUES (133, 'Water', '23_22_water2', 112096, 142623, 119232, 147603, -3177, -2893, 0); 
INSERT INTO `zone` VALUES (134, 'Water', '23_22_water3', 117509, 136740, 122009, 138240, -4080, -3780, 0); 
INSERT INTO `zone` VALUES (135, 'Water', '23_22_water4', 117509, 136740, 122009, 138240, -4080, -3780, 0); 
INSERT INTO `zone` VALUES (136, 'Water', '23_22_water5', 117509, 136740, 122009, 138240, -4080, -3780, 0); 
INSERT INTO `zone` VALUES (137, 'Water', '23_22_water6', 114560, 132480, 115200, 133120, -5504, -3184, 0); 
INSERT INTO `zone` VALUES (138, 'Water', '23_22_water7', 115200, 132480, 116672, 133120, -5504, -4928, 0); 
INSERT INTO `zone` VALUES (139, 'Water', '23_23_water1', 98304, 163840, 131072, 167840, -4780, -3780, 0); 
INSERT INTO `zone` VALUES (140, 'Water', '23_23_water2', 98304, 167840, 102304, 196608, -4780, -3780, 0); 
INSERT INTO `zone` VALUES (141, 'Water', '23_23_water3', 124072, 167840, 131072, 196608, -4780, -3780, 0); 
INSERT INTO `zone` VALUES (142, 'Water', '23_23_water4', 102304, 191608, 124072, 196608, -4780, -3780, 0); 
INSERT INTO `zone` VALUES (143, 'Water', '23_23_water5', 121672, 186199, 124072, 189151, -4780, -3780, 0); 
INSERT INTO `zone` VALUES (144, 'Water', '23_23_water6', 102304, 184170, 105652, 187250, -4780, -3780, 0); 
INSERT INTO `zone` VALUES (145, 'Water', '23_23_water7', 102304, 168567, 106804, 173567, -4780, -3780, 0); 
INSERT INTO `zone` VALUES (146, 'Water', '23_23_water8', 107520, 173696, 108160, 174336, -6096, -3776, 0); 
INSERT INTO `zone` VALUES (147, 'Water', '23_23_water9', 108160, 173696, 109632, 174336, -6106, -5529, 0); 
INSERT INTO `zone` VALUES (148, 'Water', '23_24_water1', 98304, 196608, 131072, 229376, -4780, -3780, 0); 
INSERT INTO `zone` VALUES (149, 'Water', '23_25_water1', 98304, 229376, 131072, 262144, -4781, -3781, 0); 
INSERT INTO `zone` VALUES (150, 'Water', '24_11_water1', 131072, -229376, 163840, -196608, -4780, -3780, 0); 
INSERT INTO `zone` VALUES (151, 'Water', '24_12_water1', 131072, -196608, 163840, -178608, -4780, -3780, 0); 
INSERT INTO `zone` VALUES (152, 'Water', '24_18_water1', 145081, 25139, 149881, 29939, -2626, -2528, 0); 
INSERT INTO `zone` VALUES (153, 'Water', '24_19_water1', 131059, 32768, 163827, 65536, -4776, -3776, 0); 
INSERT INTO `zone` VALUES (154, 'Water', '24_20_water1', 136648, 79304, 137400, 80056, -6096, -3736, 0); 
INSERT INTO `zone` VALUES (155, 'Water', '24_20_water2', 137400, 79360, 138816, 80000, -6096, -5519, 0); 
INSERT INTO `zone` VALUES (156, 'Water', '24_20_water3', 137472, 65536, 163840, 98304, -4480, -3780, 0); 
INSERT INTO `zone` VALUES (157, 'Water', '24_21_water1', 131072, 126072, 136072, 131072, -4780, -3780, 0); 
INSERT INTO `zone` VALUES (158, 'Water', '25_11_water1', 163840, -229376, 196608, -196608, -4780, -3780, 0); 
INSERT INTO `zone` VALUES (159, 'Water', '25_12_water1', 163840, -196608, 168840, -186608, -4780, -3780, 0); 
INSERT INTO `zone` VALUES (160, 'Water', '25_12_water2', 173411, -187756, 186042, -182534, -5465, -4465, 0); 
INSERT INTO `zone` VALUES (161, 'Water', '25_12_water3', 174206, -182687, 176749, -179167, -2322, -2066, 0); 
INSERT INTO `zone` VALUES (162, 'Water', '25_14_water1', 170908, -116846, 173748, -113953, -4643, -4033, 0); 
INSERT INTO `zone` VALUES (163, 'Water', '25_14_water2', 170926, -113936, 173766, -111043, -6130, -5256, 0); 
INSERT INTO `zone` VALUES (164, 'Water', '25_20_water1', 163840, 65760, 173840, 95072, -4280, -3780, 0); 
INSERT INTO `zone` VALUES (165, 'Water', '25_17_water1', 168576, -17920, 169216, -17280, -5568, -3248, 0); 
INSERT INTO `zone` VALUES (166, 'Water', '25_17_water2', 169216, -17920, 170688, -17280, -5568, -4991, 0); 
INSERT INTO `zone` VALUES (167, 'Water', '20_15_water1', 0, -73537, 8000, -65537, -4781, -3781, 0); 
INSERT INTO `zone` VALUES (168, 'Water', '20_16_water1', 0, -65536, 32768, -32768, -4780, -3780, 0);
INSERT INTO `zone` VALUES (169, 'Water', '21_15_water1', 32768, -98304, 65536, -65536, -4780, -3780, 0); 
INSERT INTO `zone` VALUES (170, 'Water', '21_16_water1', 32768, -65536, 65536, -32768, -4784, -3784, 0);
INSERT INTO `zone` VALUES (171, 'Water', '21_17_water1', 32768, -32768, 65536, 0, -4781, -3781, 0);
INSERT INTO `zone` VALUES (172, 'Water', '22_16_water1', 65536, -40960, 98304, -32768, -4780, -3780, 0); 
INSERT INTO `zone` VALUES (173, 'Water', '22_16_water2', 91829, -55930, 95637, -50266, -2889, -2633, 0); 
INSERT INTO `zone` VALUES (174, 'Water', '22_16_water3', 87765, -54554, 91829, -50266, -2889, -2633, 0); 
INSERT INTO `zone` VALUES (175, 'Water', '22_16_water4', 84947, -50234, 90133, -46202, -3408, -3152, 0); 
INSERT INTO `zone` VALUES (176, 'Water', '22_16_water5', 76003, -46368, 84947, -39968, -3504, -3248, 0); 
INSERT INTO `zone` VALUES (177, 'Water', '22_16_water6', 68852, -62008, 74036, -56192, -3417, -3161, 0); 
INSERT INTO `zone` VALUES (178, 'Water', '22_16_water7', 69480, -56192, 77032, -53203, -3417, -3161, 0); 
INSERT INTO `zone` VALUES (179, 'Water', '22_16_water8', 71311, -53203, 76175, -50292, -3417, -3161, 0); 
INSERT INTO `zone` VALUES (180, 'Water', '22_16_water9', 75059, -63591, 78355, -57517, -2608, -2352, 0);
INSERT INTO `zone` VALUES (181, 'Water', '22_16_water10', 78355, -65184, 83859, -51872, -2608, -2352, 0); 
INSERT INTO `zone` VALUES (182, 'Water', '22_16_water11', 83859, -64826, 87187, -55418, -2608, -2352, 0); 
INSERT INTO `zone` VALUES (183, 'Water', '22_16_water12', 87187, -62909, 90963, -58301, -2608, -2352, 0); 
INSERT INTO `zone` VALUES (184, 'Water', '22_16_water13', 77283, -51872, 83747, -49440, -2608, -2352, 0); 
INSERT INTO `zone` VALUES (185, 'Water', '22_16_water14', 83859, -55418, 86227, -52602, -2608, -2352, 0); 
INSERT INTO `zone` VALUES (186, 'Water', '22_16_water15', 69635, -47328, 76003, -39776, -3504, -3248, 0); 
INSERT INTO `zone` VALUES (187, 'Water', '22_16_water16', 87187, -58301, 89235, -56541, -2608, -2352, 0);
INSERT INTO `zone` VALUES (188, 'Water', '22_17_water1', 65536, -32768, 76800, 0, -4780, -3780, 0);
INSERT INTO `zone` VALUES (189, 'Water', '22_17_water2', 76800, -32768, 98304, -18432, -4780, -3780, 0);

-- -----------------------------------------
-- Boss Lair Zones
-- -----------------------------------------

INSERT INTO `zone` VALUES (2100, 'LairofAntharas', 'Lair of Antharas', 173439, 110176, 187346, 119469, -8220, -4870, 0);
INSERT INTO `zone` VALUES (2110, 'LairofBaium', 'Lair of Baium', 109448, 10233, 118547, 21446, 10070, 12480, 0);
INSERT INTO `zone` VALUES (2120, 'LairofValakas', 'Lair of Valakas', 199755, -124724, 224677, -103211, -1640, 9880, 0);
INSERT INTO `zone` VALUES (2130, 'LairofLilith', 'Lair of Lilith', 183986, -13716, 186097, -11532, -5500, -3895, 0);
INSERT INTO `zone` VALUES (2140, 'LairofAnakim', 'Lair of Anakim', 184008, -10681, 186107, -8589, -5500, -3895, 0);
INSERT INTO `zone` VALUES (2150, 'LairofZaken', 'Lair of Zaken', 53182, 216945, 57311, 221151, -3775, -2690, 0);
INSERT INTO `zone` VALUES (2160, 'LairofSailren', 'Lair of Sailren', 26095, -8084, 28987, -5295, -1975, -1944, 0);

-- -----------------------------------------
-- Four Sepulcher Zones
-- -----------------------------------------
INSERT INTO `zone` VALUES (2000, 'FourSepulcher', 'Four Sepulcher', 181241, -86443, 192134, -84575, -7225, 0, 0);
INSERT INTO `zone` VALUES (2010, 'FourSepulcher', 'Four Sepulcher', 179551, -89844, 190342, -88042, -7250, 0, 0);
INSERT INTO `zone` VALUES (2020, 'FourSepulcher', 'Four Sepulcher', 172354, -86630, 174073, -75565, -7250, 0, 0);
INSERT INTO `zone` VALUES (2030, 'FourSepulcher', 'Four Sepulcher', 174697, -82707, 176500, -71885, -7250, 0, 0);