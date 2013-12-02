DELETE FROM `player` WHERE id NOT IN (SELECT distinct player_id FROM score) AND shmup_user_id IS NULL
