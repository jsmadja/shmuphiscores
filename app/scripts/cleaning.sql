UPDATE score
SET player_id = (SELECT id FROM player WHERE name = '-S.L-')
WHERE player_id IN (SELECT id FROM player WHERE name = '-SL-');

UPDATE score
SET player_id = (SELECT id FROM player WHERE name = 'back')
WHERE player_id IN (SELECT id FROM player WHERE name = 'backwash');

UPDATE score
SET player_id = (SELECT id FROM player WHERE name = 'coven')
WHERE player_id IN (SELECT id FROM player WHERE name = 'coven81');

UPDATE score
SET player_id = (SELECT id FROM player WHERE name = '-S.L-')
WHERE player_id IN (SELECT id FROM player WHERE name = 'SL');

UPDATE score
SET player_id = (SELECT id FROM player WHERE name = 'DKU-A-M')
WHERE player_id IN (SELECT id FROM player WHERE name = 'DKU-AM');

UPDATE score
SET player_id = (SELECT id FROM player WHERE name = 'DKU-A-M')
WHERE player_id IN (SELECT id FROM player WHERE name = 'DKU-AM');

UPDATE score
SET player_id = (SELECT id FROM player WHERE name = 'KAB')
WHERE player_id IN (SELECT id FROM player WHERE name = 'DKU-KAB');

UPDATE score
SET player_id = (SELECT id FROM player WHERE name = 'KPE')
WHERE player_id IN (SELECT id FROM player WHERE name = 'DKU-KPE');

UPDATE score
SET player_id = (SELECT id FROM player WHERE name = 'KPE')
WHERE player_id IN (SELECT id FROM player WHERE name = 'KPE_');

UPDATE score
SET player_id = (SELECT id FROM player WHERE name = 'Sharivan')
WHERE player_id IN (SELECT id FROM player WHERE name = 'Sharivan 59');

UPDATE score
SET player_id = (SELECT id FROM player WHERE name = 'Sharivan')
WHERE player_id IN (SELECT id FROM player WHERE name = 'Sharivan59');

UPDATE score
SET player_id = (SELECT id FROM player WHERE name = 'Yami')
WHERE player_id IN (SELECT id FROM player WHERE name = 'Yami9999');

UPDATE score
SET player_id = (SELECT id FROM player WHERE name = 'Undef')
WHERE player_id IN (SELECT id FROM player WHERE name = 'Undef-UND');

UPDATE score
SET player_id = (SELECT id FROM player WHERE name = 'Y\'om')
WHERE player_id IN (SELECT id FROM player WHERE name = 'Yom');

UPDATE score
SET player_id = (SELECT id FROM player WHERE name = 'Zapbranisac')
WHERE player_id IN (SELECT id FROM player WHERE name = 'Zapbrinisac');

UPDATE score
SET player_id = (SELECT id FROM player WHERE name = 'überwenig')
WHERE player_id IN (SELECT id FROM player WHERE name = 'überwenig (TBC/T23)');

UPDATE score
SET player_id = (SELECT id FROM player WHERE name = 'Rechiku')
WHERE player_id IN (SELECT id FROM player WHERE name = 'Rechiku_');

UPDATE score
SET player_id = (SELECT id FROM player WHERE name = 'Rechiku')
WHERE player_id IN (SELECT id FROM player WHERE name = 'Rechiku__');

UPDATE score
SET player_id = (SELECT id FROM player WHERE name = 'Sephyross')
WHERE player_id IN (SELECT id FROM player WHERE name = 'Sephyross_');

UPDATE score
SET player_id = (SELECT id FROM player WHERE name = 'Tawhiri')
WHERE player_id IN (SELECT id FROM player WHERE name = 'Tawhiri_');

UPDATE score
SET player_id = (SELECT id FROM player WHERE name = 'back')
WHERE player_id IN (SELECT id FROM player WHERE name = 'back_');

UPDATE score
SET player_id = (SELECT id FROM player WHERE name = 'BAD')
WHERE player_id IN (SELECT id FROM player WHERE name = 'BAD_');

UPDATE score
SET player_id = (SELECT id FROM player WHERE name = 'Doudinou')
WHERE player_id IN (SELECT id FROM player WHERE name = 'doudinou__');

UPDATE score
SET player_id = (SELECT id FROM player WHERE name = 'Doudinou')
WHERE player_id IN (SELECT id FROM player WHERE name = 'doudinou_');

UPDATE score
SET player_id = (SELECT id FROM player WHERE name = 'NEO')
WHERE player_id IN (SELECT id FROM player WHERE name = 'neo_');

UPDATE score
SET player_id = (SELECT id FROM player WHERE name = 'NEO')
WHERE player_id IN (SELECT id FROM player WHERE name = 'NEO__');

UPDATE score
SET player_id = (SELECT id FROM player WHERE name = 'baboulinet')
WHERE player_id IN (SELECT id FROM player WHERE name = 'Baboulinet_');

UPDATE score
SET player_id = (SELECT id FROM player WHERE name = 'としまと-HUU')
WHERE player_id IN (SELECT id FROM player WHERE name = 'としまと-HUU_2');

UPDATE score
SET player_id = (SELECT id FROM player WHERE name = 'としまと-HUU')
WHERE player_id IN (SELECT id FROM player WHERE name = 'としまと-HUU_4');

UPDATE score
SET player_id = (SELECT id FROM player WHERE name = 'HUU')
WHERE player_id IN (SELECT id FROM player WHERE name = 'としまと-HUU');

UPDATE score
SET player_id = (SELECT id FROM player WHERE name = 'Sulf\'')
WHERE player_id IN (SELECT id FROM player WHERE name = 'Sulfateur');

UPDATE score
SET player_id = (SELECT id FROM player WHERE name = 'dodonpasim')
WHERE player_id IN (SELECT id FROM player WHERE name = 'dodonpasism');

UPDATE score
SET player_id = (SELECT id FROM player WHERE name = '123RYU')
WHERE player_id IN (SELECT id FROM player WHERE name = '123 RYU');

UPDATE score
SET player_id = (SELECT id FROM player WHERE name = 'Johnny_Crypt')
WHERE player_id IN (SELECT id FROM player WHERE name = 'Johnny Crypt');

UPDATE score
SET player_id = (SELECT id FROM player WHERE name = 'Knuckels')
WHERE player_id IN (SELECT id FROM player WHERE name = 'KPE');

UPDATE score
SET player_id = (SELECT id FROM player WHERE name = 'Sephyross')
WHERE player_id IN (SELECT id FROM player WHERE name = 'SePhyr0ss');



DELETE FROM player WHERE id NOT IN (SELECT distinct player_id FROM score) AND shmup_user_id IS NULL