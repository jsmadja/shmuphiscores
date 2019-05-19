const Database = require('./database');

const Repository = {
    getRankedScores: (player_id = 1) =>
        Database.query(`SELECT *
                               FROM score
        WHERE player_id=${player_id}
        AND rank IS NOT NULL
        `),

    getPreviousScore: (score) =>
        Database.query(`SELECT game.cover, game.title, score.id, mode.name mode_name, difficulty.name difficulty_name, value, player.name player_name
        FROM score
JOIN game ON game.id = score.game_id
JOIN player ON player.id = score.player_id
LEFT JOIN mode ON mode.id = score.mode_id
LEFT JOIN difficulty ON difficulty.id = score.difficulty_id
        WHERE rank = ${score.rank - 1}
AND score.game_id = ${score.game_id}
AND mode_id ${score.mode_id ? '= ' + score.mode_id : 'IS NULL'}
AND difficulty_id ${score.difficulty_id ? ' = ' + score.difficulty_id : 'IS NULL'}
        `)
};

module.exports = Repository;