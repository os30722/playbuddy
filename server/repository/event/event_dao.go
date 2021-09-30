package event

import (
	"context"
	"fmt"
	"log"
	"net/url"

	"github.com/hepa/sports/vo"
	"github.com/jackc/pgx/v4/pgxpool"
)

type eventDao struct {
	db *pgxpool.Pool
}

func GetEventDao(db *pgxpool.Pool) *eventDao {
	return &eventDao{
		db: db,
	}
}

const queryHost = `insert into event(host_id, sport, event_date, start_time, end_time, no_player, location, address,
	instruction, joined) values($1, $2, $3, $4, $5, $6 ,$7, $8, $9, 1) returning event_id`
const incrEventNo = `update users set no_event = no_event + 1 where user_id = $1`

func (ev eventDao) HostEvent(ctx context.Context, uid int, event vo.Event) error {
	var db = ev.db
	var eventId int
	geoPoints := fmt.Sprintf("point(%f %f)", event.Area.Longitude, event.Area.Latitude)

	err := db.QueryRow(ctx, queryHost,
		uid, event.Sport, event.EventDate, event.StartTime.Time, event.EndTime.Time, event.NoPlayer, geoPoints, event.Address,
		event.Instruction).Scan(&eventId)
	if err != nil {
		return err
	}

	err = ev.RequestEvent(ctx, uid, eventId, true)
	if err != nil {
		return err
	}

	_, err = db.Exec(ctx, incrEventNo, uid)
	if err != nil {
		return err
	}

	return nil

}

const queryDetail = `select event_id, sport, event_date, start_time, end_time, requests, address, instruction, joined,
	coalesce((select status from joined where player_id = $1 and event_id = $2) ,'nj') as player_joined, no_player 
	 from event where event_id = $2;`

func (ev eventDao) GetEventDetail(ctx context.Context, uid int, eventId int) (*vo.EventPage, error) {
	var db = ev.db

	var info vo.EventPage

	err := db.QueryRow(ctx, queryDetail, uid, eventId).Scan(&info.EventId, &info.Sport, &info.EventDate,
		&info.StartTime.Time, &info.EndTime.Time, &info.Requests,
		&info.Address, &info.Instruction, &info.Joined, &info.PlayerJoined, &info.TotalPlayer)
	if err != nil {
		return nil, err
	}

	return &info, nil
}

const queryEventList = `select event.event_id, sport, event_date, start_time, end_time, left(address, 30) as address,
joined, host_id, profile_pic, first_name, last_name  
from event join users on event.host_id = users.user_id 
where event_id not in (select event_id from joined where player_id = $1)`

const queryOrder = ` order by event_date, start_time offset $2 limit $3;`

func (ev eventDao) GetEventList(ctx context.Context, uid int, cursor int, pageSize int, filters url.Values) (*[]vo.EventPage, error) {
	var db = ev.db
	var queryBuilder = ""
	values := []interface{}{uid, (cursor - 1) * pageSize, pageSize}

	if filters.Get("sport") != "" {
		queryBuilder += "and sport = $4"
		values = append(values, filters.Get("sport"))

	}

	rows, err := db.Query(ctx, queryEventList+queryBuilder+queryOrder, values...)

	if err != nil {
		return nil, err
	}

	defer rows.Close()

	eventInfo := make([]vo.EventPage, 0, pageSize)

	for rows.Next() {
		var e vo.EventPage
		err := rows.Scan(&e.EventId, &e.Sport, &e.EventDate, &e.StartTime.Time, &e.EndTime.Time,
			&e.Address, &e.Joined, &e.Host.Uid, &e.Host.ProfilePic, &e.Host.FirstName, &e.Host.LastName)

		if err != nil {
			log.Println(err)
			break
		}

		eventInfo = append(eventInfo, e)
	}

	if err != nil {
		return nil, err
	}

	return &eventInfo, nil
}

const joinQuery = `insert into joined(event_id, player_id, status) values($1,$2, $3)`
const incRequest = `update event set requests = requests + 1 where event_id = $1`

func (ev eventDao) RequestEvent(ctx context.Context, uid int, eventId int, host bool) error {
	var db = ev.db
	var err error
	if host {
		_, err = db.Exec(ctx, joinQuery, eventId, uid, "host")
		return nil

	}
	_, err = db.Exec(ctx, joinQuery, eventId, uid, "req")
	_, err = db.Exec(ctx, incRequest, eventId)
	if err != nil {
		return err
	}

	return nil
}

const leaveQuery = `delete from joined where event_id=$1 and player_id=$2`
const decrJoined = `update event set joined = joined - 1 where event_id = $1`

func (ev eventDao) LeaveEvent(ctx context.Context, uid int, eventId int) error {
	var db = ev.db

	_, err := db.Exec(ctx, leaveQuery, eventId, uid)
	if err != nil {
		return err
	}

	_, err = db.Exec(ctx, decrJoined, eventId)
	if err != nil {
		return err
	}

	return nil

}

const remQuery = `update joined set status = 'rem'  where event_id = $1 and player_id = $2 returning (
	select status from joined where event_id = $1 and player_id = $2 )`
const decrRequests = `update event set requests = requests - 1 where event_id = $1`

func (ev eventDao) RemoveFromEvent(ctx context.Context, uid int, eventId int) error {
	var db = ev.db
	var status string

	err := db.QueryRow(ctx, remQuery, eventId, uid).Scan(&status)
	if err != nil {
		return err
	}

	if status == "req" {
		_, err = db.Exec(ctx, decrRequests, eventId)
	} else {
		_, err = db.Exec(ctx, decrJoined, eventId)

	}

	if err != nil {
		return err
	}

	return nil
}

const querySchedule = `select event.event_id, sport, event_date, start_time, end_time, left(address, 30) as address,
	joined, requests, host_id, profile_pic, first_name, last_name from joined 
	inner join event on event.event_id = joined.event_id 
	inner join users on event.host_id = users.user_id where player_id = $1 and joined.status in ('host','jo')
	order by event_date, start_time, id offset $2 limit $3;`

func (ev eventDao) GetUserSchedule(ctx context.Context, uid int, cursor int, pageSize int) (*[]vo.EventPage, error) {
	var db = ev.db

	rows, err := db.Query(ctx, querySchedule, uid, (cursor-1)*pageSize, pageSize)

	if err != nil {
		return nil, err
	}

	defer rows.Close()

	eventInfo := make([]vo.EventPage, 0, pageSize)

	for rows.Next() {
		var e vo.EventPage

		err := rows.Scan(&e.EventId, &e.Sport, &e.EventDate, &e.StartTime.Time, &e.EndTime.Time,
			&e.Address, &e.Joined, &e.Requests, &e.Host.Uid, &e.Host.ProfilePic, &e.Host.FirstName, &e.Host.LastName)
		if uid != e.Host.Uid {
			e.Requests = nil
		}

		if err != nil {
			log.Println(err)
			break
		}

		eventInfo = append(eventInfo, e)
	}

	if err != nil {
		return nil, err
	}

	return &eventInfo, nil
}

const queryFriends = `select device_id from relationship
	join logged on (relationship.user_id = logged.user_id and friend_id = $1) or
	(relationship.friend_id = logged.user_id and relationship.user_id = $1)
	where (relationship.user_id = $1 or relationship.friend_id = $1) and  status = 'confirm';`

func (ev eventDao) FriendsPayload(ctx context.Context, uid int) ([]vo.RequestPayload, error) {
	var db = ev.db

	var firstName, lastName string
	err := db.QueryRow(ctx, "select first_name, last_name from users where user_id = $1", uid).Scan(&firstName, &lastName)
	if err != nil {
		return nil, err
	}
	rows, err := db.Query(ctx, queryFriends, uid)
	if err != nil {
		return nil, err
	}

	defer rows.Close()

	//@@TODO add no. of friends
	payloads := make([]vo.RequestPayload, 0, 10)

	for rows.Next() {
		var p vo.RequestPayload
		err := rows.Scan(&p.RecieverToken)
		if err != nil {
			log.Println(err)
			break
		}
		p.Sender = firstName + " " + lastName
		payloads = append(payloads, p)

	}

	return payloads, nil

}

const joinReqeustQuery = `select user_id, first_name, last_name, profile_pic from joined 
join users on  player_id = user_id where event_id = $1 and status ='req'
offset $2 limit $3;`

func (ev eventDao) GetJoinRequests(ctx context.Context, eventId int, cursor int, pageSize int) (*[]vo.UserPage, error) {
	var db = ev.db

	rows, err := db.Query(ctx, joinReqeustQuery, eventId, (cursor-1)*pageSize, pageSize)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	users := make([]vo.UserPage, 0, pageSize)

	for rows.Next() {
		var u vo.UserPage
		err := rows.Scan(&u.Uid, &u.FirstName, &u.LastName, &u.ProfilePic)
		if err != nil {
			log.Println(err)
			break
		}

		users = append(users, u)
	}

	if err != nil {
		return nil, err
	}

	return &users, nil
}

const queryAccept = `update joined set status = 'jo' where event_id = $1 and player_id = $2`
const incJoin = `update event set joined = joined + 1, requests = requests - 1 where event_id = $1`

func (ev eventDao) AcceptRequest(ctx context.Context, uid int, eventId int) error {
	var db = ev.db

	_, err := db.Exec(ctx, queryAccept, eventId, uid)
	if err != nil {
		return err
	}

	_, err = db.Exec(ctx, incJoin, eventId)
	if err != nil {
		return err
	}

	return nil

}

const queryDelete = `delete from event where event_id  = $1`
const decrNoEvent = `update users set no_event = no_event - 1 where user_id = $1`

func (ev eventDao) DeleteEvent(ctx context.Context, uid int, eventId int) error {
	var db = ev.db

	_, err := db.Exec(ctx, queryDelete, eventId)
	if err != nil {
		return err
	}

	_, err = db.Exec(ctx, decrNoEvent, uid)
	if err != nil {
		return err
	}

	return nil
}

func (ev eventDao) GetJoinedUsers(ctx context.Context, eventId int, cursor int, pageSize int, filters url.Values) (*[]vo.UserPage, error) {
	var db = ev.db
	var filterStr = ""
	values := []interface{}{eventId, (cursor - 1) * pageSize, pageSize}

	if len(filters.Get("q")) != 0 {
		filterStr += `and first_name || '' || last_name ilike $4 || '%'`
		values = append(values, filters.Get("q"))
	}

	var queryJoined = `select user_id, first_name, last_name, profile_pic from joined
	join users on  player_id = user_id where event_id = $1 and status in ('jo','host')` +
		filterStr + ` order by joined.date_time offset $2 limit $3;`

	rows, err := db.Query(ctx, queryJoined, values...)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	users := make([]vo.UserPage, 0, pageSize)

	for rows.Next() {
		var u vo.UserPage
		err := rows.Scan(&u.Uid, &u.FirstName, &u.LastName, &u.ProfilePic)
		if err != nil {
			log.Println(err)
			break
		}

		users = append(users, u)
	}

	if err != nil {
		return nil, err
	}

	return &users, nil
}
