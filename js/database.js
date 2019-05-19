const Mysql = require('mysql');
const Promise = require('bluebird');

const connection = Mysql.createConnection({
    host: 'kaddath.fr',
    user: 'root',
    port: 3307,
    password: 'XedQr25m1710calos',
    database: 'shmup',
});
connection.connect();

setInterval(() => connection.query('SELECT 1'), 5000);

const Database = {
    query: (sql) => new Promise((resolve, reject) => {
        connection.query(sql, (err, rows) => {
            if (err) {
                console.error(err.message);
                reject(err);
            } else {
                resolve(rows);
            }
        });
    })
};

module.exports = Database;
