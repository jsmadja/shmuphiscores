#!/bin/bash
for i in $(seq 1000);
do curl  hiscores.shmup.com/game/$i/ranking.png > /dev/null; done
