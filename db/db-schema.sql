CREATE DATABASE stats;
\c stats;
CREATE TABLE public.usercashstats (
   user_Id UUID NOT NULL,
   life_time_rake decimal default 0.00,
   life_time_amount_won decimal default 0.00,
   life_time_amount_lost decimal default 0.00,
   life_time_games_won INTEGER default 0,
   life_time_games_lost INTEGER default 0,
   life_time_deposit_amount decimal default 0.00,
   life_time_deposit_count INTEGER default 0,
   first_games_played TIMESTAMP,
   last_game_played TIMESTAMP,
   life_time_withdrawl_amount decimal default 0.00,
   life_time_withdrawal_count INTEGER default 0,
   first_deposit_date TIMESTAMP,
   first_deposit_amount decimal default 0.00,
   first_withdrawal_amount decimal default 0.00,
   first_withdrawal_date TIMESTAMP,
   last_deposit_date TIMESTAMP,
   last_withdrawal_date TIMESTAMP,
   last_deposit_amount decimal default 0.00,
   last_withdrawal_amount decimal default 0.00,
   last_game_played_name varchar(50) NULL,
   last_game_played_result varchar(10) NULL,
   last_seen TIMESTAMP,
   first_game_played_name varchar(50) NULL,
   games_tried varchar(100) NULL,
   created_at TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
   updated_at TIMESTAMP(3),
   CONSTRAINT cashstats_pkey PRIMARY KEY (user_Id)
  );
  
ALTER TABLE usercashstats ADD COLUMN deposit_balance decimal DEFAULT 0.00;
ALTER TABLE usercashstats ADD COLUMN withdrawable_balance decimal DEFAULT 0.00;
ALTER TABLE usercashstats ADD COLUMN pending_withdrawal_amount decimal DEFAULT 0.00;
ALTER TABLE usercashstats ADD COLUMN last_manual_withdrawal_processed_time TIMESTAMP;
ALTER TABLE usercashstats ADD COLUMN life_time_rummy_rake decimal DEFAULT 0.00;
ALTER TABLE usercashstats ADD COLUMN life_time_callbreak_rake decimal DEFAULT 0.00;
ALTER TABLE usercashstats ALTER COLUMN last_seen set DEFAULT now();
ALTER TABLE usercashstats ADD COLUMN daily_login_streak int4 DEFAULT 0;
ALTER TABLE usercashstats ALTER COLUMN daily_login_streak set DEFAULT 1;

CREATE TABLE public.suspicious_users (
   user_id UUID NOT NULL,
   is_suspicious boolean DEFAULT false,
   is_fraud boolean DEFAULT false,
   marked_fraud_date TIMESTAMP,
   fraud_reason varchar(250) NULL,
   marked_not_suspicious_date TIMESTAMP,
   is_manual_withdrawal_in_last_hours boolean DEFAULT false,
   profit_and_loss boolean DEFAULT false,
   game_play_profit_and_loss boolean DEFAULT false,
   created_at TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
   updated_at TIMESTAMP(3),
   CONSTRAINT suspicious_users_pkey PRIMARY KEY (user_Id)
 );
 
CREATE INDEX created_at_idx ON public.suspicious_users USING btree (created_at);
CREATE INDEX marked_not_suspicious_date_idx ON public.suspicious_users USING btree (marked_not_suspicious_date);

alter table suspicious_users ALTER COLUMN  fraud_reason type varchar(500);

ALTER TABLE usercashstats ADD COLUMN life_time_attempted_deposit_count INTEGER default 0;
ALTER TABLE suspicious_users ADD COLUMN deposit_failure boolean default false;

DROP TABLE IF EXISTS usercashgamestats cascade;
CREATE TABLE public.usercashgamestats (
	user_id uuid NOT NULL,
	life_time_rummy_tourney_winnings decimal DEFAULT 0.0,
	life_time_poker_tourney_winnings decimal DEFAULT 0.0,
	created_at timestamp NOT NULL DEFAULT now(),
	updated_at timestamp NOT NULL DEFAULT now(),
	CONSTRAINT usercashgamestats_pkey PRIMARY KEY (user_id)
);