package database

import (
	"context"
	"log"
	"strconv"

	"github.com/jackc/pgx/v4/pgxpool"
	_ "github.com/jackc/pgx/v4/stdlib"
)

/*
	Set Postgres connection constants

*/

const (
	host     = ""
	port     = 5432
	user     = ""
	password = ""
	dbname   = ""
)

var db *pgxpool.Pool = nil

// To get an instance of postgres connection

func GetPostgres() (*pgxpool.Pool, error) {

	var err error
	db, err = pgxpool.Connect(context.Background(), "postgresql://"+host+":"+strconv.Itoa(port)+"/"+dbname+"?user="+user+"&password="+password)
	if err != nil {
		return nil, err
	}

	initTables()

	log.Println("Established Database Connection...")
	return db, nil
}

var tables = []string{

	// Users Table
	`create table if not exists users(user_id serial primary key, first_name varchar(20), last_name varchar(20), dob date, gender char(1),
	email varchar(70), pass varchar(128), date_time timestamp  without time zone default (now() at time zone 'utc'), profile_pic varchar(150),
	no_event int default 0, friends int default 0, reputation int default 0);`,

	// Conversation Table
	`create table if not exists conversation(id serial primary key, sender_id int, recipient_id int, message varchar(300), time timestamp, is_read bool default false);`,

	// Event Table
	`create table if not exists event(event_id serial primary key, sport varchar(20), event_date date, start_time timestamp, end_time timestamp,
	no_player integer, location geometry(Point), instruction varchar(200), host_id int, address varchar(200), joined int, requests int default 0 not null,
	constraint event_fkey foreign key(host_id) references users(user_id) on delete cascade);`,

	// Joined Players Table
	`create table if not exists joined(id serial primary key, event_id int, player_id int, status varchar(10),
	 date_time timestamp without time zone default (now() at time zone 'utc'),
	constraint joined_event_id_fkey foreign key(event_id) references event(event_id) on delete cascade);`,

	// Relationship Table
	`create table if not exists relationship(friend1 int, friend2 int, status varchar(15), 
	date_time timestamp without time zone default (now() at time zone 'utc'), action_user int);`,
}

func initTables() {
	var ctx = context.Background()
	for _, table := range tables {
		_, err := db.Exec(ctx, table)
		if err != nil {
			log.Fatal(err)
		}
	}
}
