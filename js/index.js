const express = require('express');
const Promise = require('bluebird');
const Repository = require('./repository');
const app = express();

const toCSV = (score) => `${score.cover.indexOf('/') === 0 ? 'http://hiscores.shmup.com' + score.cover : score.cover},${score.id},${score.title},${score.difficulty_name || ' '},${score.mode_name || ' '},${score.player_name},${score.value}`;

app.get('/kill-list', function (req, res) {
    Repository.getRankedScores()
        .then(scores =>
            Promise
                .mapSeries(scores, score => Repository.getPreviousScore(score))
                .then(scores => scores.filter(score => score[0]))
                .then(scores => scores.map(score => score[0]))
                .then(scores => scores.map(toCSV))
                .then(scores => res.send(scores.join('<br>'))));
});

app.listen(3000, function () {
    console.log('Example app listening on port 3000!')
});