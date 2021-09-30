package user

import (
	"context"
	"log"
	"net/url"

	"github.com/hepa/sports/vo"
	"github.com/jackc/pgx/v4/pgxpool"
	"golang.org/x/crypto/bcrypt"
)

type userDao struct {
	db *pgxpool.Pool
}

func GetUserDao(db *pgxpool.Pool) *userDao {
	return &userDao{
		db: db,
	}
}

func (ur userDao) GetUserCred(ctx context.Context, email string) (*vo.UserCred, error) {
	var db = ur.db
	var userCred vo.UserCred

	err := db.QueryRow(ctx, "select user_id, email, pass from users where email=$1 limit 1", email).Scan(&userCred.Id, &userCred.Email, &userCred.Pass)
	if err != nil {
		return nil, err
	}

	return &userCred, nil
}

func (ur userDao) FindDuplicate(ctx context.Context, email string) (bool, error) {
	var db = ur.db
	var flag int
	err := db.QueryRow(ctx, "select count(*) from users where email=$1 limit 1", email).Scan(&flag)
	if err != nil {
		return false, err
	}
	if flag == 0 {
		return false, nil
	} else {
		return true, nil
	}
}

func (ur userDao) SignUp(ctx context.Context, form *vo.UserForm) (int, error) {
	var db = ur.db
	var id int
	hash, err := bcrypt.GenerateFromPassword([]byte(form.Pass), 10)
	if err != nil {
		return -1, err
	}
	err = db.QueryRow(ctx, "insert into  users(first_name,last_name,dob,gender,email,pass) values($1,$2,$3,$4,$5,$6) returning user_id",
		form.FirstName, form.LastName, form.Dob, string(form.Gender[0]), form.Email, hash).Scan(&id)
	if err != nil {
		return -1, err
	}
	return id, nil
}

const querySearch = `select user_id, first_name, last_name, profile_pic from users 
where first_name || '' || last_name ilike $1 || '%' and user_id > $2 limit $3;`

func (ur userDao) SearchUsers(ctx context.Context, query string, cursor int, pageSize int) (*[]vo.UserPage, error) {
	var db = ur.db

	rows, err := db.Query(ctx, querySearch, query, (cursor-1)*pageSize, pageSize)

	if err != nil {
		return nil, err
	}

	defer rows.Close()

	userInfo := make([]vo.UserPage, 0, pageSize)

	for rows.Next() {
		var u vo.UserPage
		err := rows.Scan(&u.Uid, &u.FirstName, &u.LastName, &u.ProfilePic)

		if err != nil {
			log.Println(err)
			break
		}

		userInfo = append(userInfo, u)
	}

	if err != nil {
		return nil, err
	}

	return &userInfo, nil

}

const queryProfile = `select user_id, first_name, last_name, profile_pic, no_event, friends, reputation,
 (select status from relationship where friend1 = $1 and friend2 = $2)
 from users where user_id = $3`

func (ur userDao) GetProfile(ctx context.Context, uid int, profileId int) (*vo.Profile, error) {
	var db = ur.db
	var p vo.Profile

	var u = profileId

	if uid > profileId {
		temp := profileId
		profileId = uid
		uid = temp
	}

	err := db.QueryRow(ctx, queryProfile, uid, profileId, u).Scan(&p.User.Uid, &p.User.FirstName, &p.User.LastName, &p.User.ProfilePic,
		&p.NoEvents, &p.Friends, &p.Reputation, &p.FriendStatus)

	if err != nil {
		return nil, err
	}

	return &p, nil

}

const queryRequest = `select users.user_id, first_name, last_name, profile_pic 
from relationship join users on (relationship.friend2 = users.user_id and relationship.friend2 != $1)
or (relationship.friend1 = users.user_id and relationship.friend1 != $1)
where (relationship.friend2= $1 or relationship.friend1 = $1) and relationship.action_user != $1
and relationship.status='request'
order by relationship.date_time offset $2 limit $3;`

func (ur userDao) FriendRequests(ctx context.Context, uid int, cursor int, pageSize int) (*[]vo.UserPage, error) {
	var db = ur.db

	rows, err := db.Query(ctx, queryRequest, uid, (cursor-1)*pageSize, pageSize)

	if err != nil {
		return nil, err
	}
	defer rows.Close()

	friends := make([]vo.UserPage, 0, pageSize)

	for rows.Next() {
		var f vo.UserPage

		err := rows.Scan(&f.Uid, &f.FirstName, &f.LastName, &f.ProfilePic)
		if err != nil {
			log.Println(err)
			break
		}

		friends = append(friends, f)
	}

	if err != nil {
		return nil, err
	}

	return &friends, nil
}

const queryLogged = `insert into logged(user_id, device_id) values ($1, $2) 
		on conflict (user_id) do update set device_id = $2;`

func (ur userDao) LoginUser(ctx context.Context, uid int, devId string) error {
	var db = ur.db

	_, err := db.Exec(ctx, queryLogged, uid, devId)
	if err != nil {
		return err
	}
	return nil
}

const requestFriend = `insert into relationship(friend1, friend2, status, action_user) 
		values ($1, $2, 'request', $3)`

func (ur userDao) RequestFriend(ctx context.Context, uid int, friendId int) error {
	var db = ur.db

	action_user := uid
	if uid > friendId {
		temp := friendId
		friendId = uid
		uid = temp
	}
	_, err := db.Exec(ctx, requestFriend, uid, friendId, action_user)
	if err != nil {
		return err
	}

	return nil
}

func (ur userDao) FriendRequestData(ctx context.Context, uid int, friendId int) (*vo.RequestPayload, error) {

	var p vo.RequestPayload
	var db = ur.db
	var firstName, lastName string
	err := db.QueryRow(ctx, "select first_name, last_name from users where user_id = $1", uid).Scan(&firstName, &lastName)
	if err != nil {
		return nil, err
	}

	err = db.QueryRow(ctx, "select device_id from logged where user_id = $1", friendId).Scan(&p.RecieverToken)
	if err != nil {
		return nil, err
	}

	p.Sender = firstName + " " + lastName

	return &p, nil

}

const queryAccept = `update relationship set status = 'confirm' , date_time = now() at time zone 'utc' 
	where friend1= $1 and friend2 = $2;`
const incFriend = `update users set friends = friends + 1 where user_id in ($1, $2)`

func (ur userDao) AcceptFriend(ctx context.Context, uid int, friendId int) error {
	var db = ur.db

	if uid > friendId {
		temp := friendId
		friendId = uid
		uid = temp
	}

	_, err := db.Exec(ctx, queryAccept, uid, friendId)
	if err != nil {
		return err
	}

	_, err = db.Exec(ctx, incFriend, uid, friendId)
	if err != nil {
		return err
	}

	return nil
}

const queryRemove = `delete from relationship where friend1 = $1 and friend2 = $2`
const decFriend = `update table users set friends = friends - 1 where user_id in ($1, $2)`

func (ur userDao) RemoveFriend(ctx context.Context, uid int, friendId int) error {
	var db = ur.db

	if uid > friendId {
		temp := friendId
		friendId = uid
		uid = temp
	}

	_, err := db.Exec(ctx, queryRemove, uid, friendId)
	if err != nil {
		return err
	}

	_, err = db.Exec(ctx, decFriend, uid, friendId)
	if err != nil {
		return err
	}

	return nil
}

const queryUser = `select user_id, first_name, last_name, profile_pic from users where user_id = $1`

func (ur userDao) FindUser(ctx context.Context, uid int) (*vo.UserPage, error) {
	var db = ur.db
	var p vo.UserPage

	err := db.QueryRow(ctx, queryUser, uid).Scan(&p.Uid, &p.FirstName, &p.LastName, &p.ProfilePic)
	if err != nil {
		return nil, err
	}

	return &p, err
}

func (ur userDao) GetFriendsList(ctx context.Context, uid int, cursor int, pageSize int, filters url.Values) (*[]vo.UserPage, error) {
	var db = ur.db
	var filterStr = ""
	values := []interface{}{uid, (cursor - 1) * pageSize, pageSize}

	if len(filters.Get("q")) != 0 {
		filterStr += `and (first_name || '' || last_name ilike $4 || '%')`
		values = append(values, filters.Get("q"))
	}

	var queryFriends = `select user_id, first_name, last_name,profile_pic from relationship
	join users on (friend1 = user_id and friend1 != $1) or
	(friend2 = user_id and friend2 != $1)
	where (friend1 = $1 or friend2 = $1) ` + filterStr + `order by relationship.date_time offset $2 limit $3; `

	rows, err := db.Query(ctx, queryFriends, values...)
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
