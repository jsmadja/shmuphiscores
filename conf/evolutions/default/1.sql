# --- !Ups

create table base_model (
  id                        bigint auto_increment not null,
  created_at                datetime not null,
  updated_at                datetime not null,
  constraint pk_base_model primary key (id))
;

create table difficulty (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  game_id                   bigint,
  created_at                datetime not null,
  updated_at                datetime not null,
  constraint pk_difficulty primary key (id))
;

create table game (
  id                        bigint auto_increment not null,
  cover                     varchar(255),
  title                     varchar(255),
  thread                    varchar(255),
  created_at                datetime not null,
  updated_at                datetime not null,
  constraint pk_game primary key (id))
;

create table mode (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  game_id                   bigint,
  created_at                datetime not null,
  updated_at                datetime not null,
  constraint pk_mode primary key (id))
;

create table platform (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  game_id                   bigint,
  created_at                datetime not null,
  updated_at                datetime not null,
  constraint pk_platform primary key (id))
;

create table player (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  shmup_user_id             bigint,
  created_at                datetime not null,
  updated_at                datetime not null,
  constraint pk_player primary key (id))
;

create table score (
  id                        bigint auto_increment not null,
  game_id                   bigint,
  player_id                 bigint,
  stage_id                  bigint,
  mode_id                   bigint,
  difficulty_id             bigint,
  platform_id               bigint,
  value                     bigint,
  comment                   longtext,
  photo                     varchar(255),
  created_at                datetime not null,
  updated_at                datetime not null,
  constraint pk_score primary key (id))
;

create table stage (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  game_id                   bigint,
  created_at                datetime not null,
  updated_at                datetime not null,
  constraint pk_stage primary key (id))
;

alter table difficulty add constraint fk_difficulty_game_1 foreign key (game_id) references game (id) on delete restrict on update restrict;
create index ix_difficulty_game_1 on difficulty (game_id);
alter table mode add constraint fk_mode_game_2 foreign key (game_id) references game (id) on delete restrict on update restrict;
create index ix_mode_game_2 on mode (game_id);
alter table platform add constraint fk_platform_game_3 foreign key (game_id) references game (id) on delete restrict on update restrict;
create index ix_platform_game_3 on platform (game_id);
alter table score add constraint fk_score_game_4 foreign key (game_id) references game (id) on delete restrict on update restrict;
create index ix_score_game_4 on score (game_id);
alter table score add constraint fk_score_player_5 foreign key (player_id) references player (id) on delete restrict on update restrict;
create index ix_score_player_5 on score (player_id);
alter table score add constraint fk_score_stage_6 foreign key (stage_id) references stage (id) on delete restrict on update restrict;
create index ix_score_stage_6 on score (stage_id);
alter table score add constraint fk_score_mode_7 foreign key (mode_id) references mode (id) on delete restrict on update restrict;
create index ix_score_mode_7 on score (mode_id);
alter table score add constraint fk_score_difficulty_8 foreign key (difficulty_id) references difficulty (id) on delete restrict on update restrict;
create index ix_score_difficulty_8 on score (difficulty_id);
alter table score add constraint fk_score_platform_9 foreign key (platform_id) references platform (id) on delete restrict on update restrict;
create index ix_score_platform_9 on score (platform_id);
alter table stage add constraint fk_stage_game_10 foreign key (game_id) references game (id) on delete restrict on update restrict;
create index ix_stage_game_10 on stage (game_id);



# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table base_model;

drop table difficulty;

drop table game;

drop table mode;

drop table platform;

drop table player;

drop table score;

drop table stage;

SET FOREIGN_KEY_CHECKS=1;

