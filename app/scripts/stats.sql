SELECT name, COUNT(*) FROM score, player WHERE player_id = player.id GROUP BY player_id ORDER BY COUNT(*) DESC LIMIT 10;

SELECT game.title, COUNT(*) FROM score, game WHERE game_id = game.id GROUP BY game_id ORDER BY COUNT(*) DESC LIMIT 10;

SELECT game.title, COUNT(*) FROM score, game WHERE game_id = game.id AND game.created_at < date('2014-04-01') AND score.created_at >= DATE('2014-04-01') AND score.created_at < date('2014-05-01') GROUP BY game_id ORDER BY COUNT(*) DESC LIMIT 10;

SELECT game.title, COUNT(*) FROM score, game, player WHERE player_id = player.id AND game_id = game.id AND game.created_at < date('2014-04-01') AND score.created_at >= DATE('2014-04-01') AND score.created_at < date('2014-05-01') GROUP BY game_id, player_id ORDER BY COUNT(*) DESC LIMIT 10;

SELECT COUNT(*) FROM score, game WHERE game_id = game.id AND game.created_at < date('2014-04-01') AND score.created_at >= DATE('2014-04-01') AND score.created_at < date('2014-05-01');

SELECT player.name, COUNT(*) FROM game,score, player WHERE score.player_id = player.id and game_id = game.id AND game.created_at < date('2014-04-01') and score.created_at >= date('2014-04-01') and score.created_at<date('2014-05-01') GROUP BY player_id ORDER BY COUNT(*) DESC LIMIT 10
