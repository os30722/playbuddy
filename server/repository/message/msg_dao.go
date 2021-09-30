package message

import (
	"context"
	"log"
	"time"

	"github.com/hepa/sports/vo"
	"github.com/jackc/pgx/v4"
	"github.com/jackc/pgx/v4/pgxpool"
)

type msgDao struct {
	db *pgxpool.Pool
}

func GetMsgDao(db *pgxpool.Pool) *msgDao {
	return &msgDao{
		db: db,
	}
}

const querySave = `insert into conversation(sender_id, recipient_id, message, time) values ($1, $2, $3, $4) returning id`

func (ms *msgDao) SaveConversation(ctx context.Context, senderUid int, msg vo.MsgRecv, timestamp time.Time) (int, error) {
	var db = ms.db
	var id int
	err := db.QueryRow(ctx, querySave, senderUid, msg.RecvUid, msg.Msg, timestamp.Format("2006-01-02 15:04")).Scan(&id)
	if err != nil {
		return 0, err
	}

	return id, nil
}

const queryConv = `select id, sender_id, recipient_id, is_read, message, time from conversation
join users on user_id = sender_id
where sender_id in ($1, $2) and recipient_id in ($1, $2) and id <  $3 order by id desc
limit $4`
const queryConvInit = `select id, sender_id, recipient_id, is_read, message, time from conversation
join users on user_id = sender_id
where sender_id in ($1, $2) and recipient_id in ($1, $2) order by id desc
limit $3`

func (ms *msgDao) GetConversation(ctx context.Context, uid int, senderUid int, cursor int, pageSize int) (*[]vo.Conversation, error) {
	var db = ms.db
	var rows pgx.Rows

	var err error

	if cursor == 0 {
		rows, err = db.Query(ctx, queryConvInit, uid, senderUid, pageSize)
	} else {
		rows, err = db.Query(ctx, queryConv, uid, senderUid, cursor, pageSize)
	}

	if err != nil {
		return nil, err
	}

	defer rows.Close()

	conversation := make([]vo.Conversation, 0, pageSize)

	for rows.Next() {
		var c vo.Conversation
		err := rows.Scan(&c.Id, &c.SenderUid, &c.RecipientUid, &c.IsRead, &c.Msg, &c.TimeStamp)

		if err != nil {
			log.Println(err)
			break
		}

		conversation = append(conversation, c)
	}

	if err != nil {
		return nil, err
	}

	return &conversation, nil
}

const queryInbox = `select user_id, first_name, last_name, profile_pic, id, sender_id, is_read, message
from conversation
join users on (users.user_id = conversation.sender_id and conversation.sender_id != $1)
or (users.user_id = conversation.recipient_id and conversation.recipient_id != $1)
where id in (select  MAX(id)
from conversation
where sender_id = $1
or recipient_id = $1
group by greatest(sender_id, recipient_id), least(sender_id,recipient_id)) order by id desc offset $2 limit $3;`

func (ms *msgDao) GetInbox(ctx context.Context, uid int, cursor int, pageSize int) (*[]vo.Inbox, error) {
	var db = ms.db

	rows, err := db.Query(ctx, queryInbox, uid, (cursor-1)*pageSize, pageSize)

	if err != nil {
		return nil, err
	}

	defer rows.Close()

	inbox := make([]vo.Inbox, 0, pageSize)

	for rows.Next() {
		var i vo.Inbox

		err := rows.Scan(&i.User.Uid, &i.User.FirstName, &i.User.LastName, &i.User.ProfilePic, &i.Id, &i.SenderUid, &i.IsRead, &i.Msg)

		if err != nil {
			log.Println(err)
			break
		}

		inbox = append(inbox, i)
	}

	if err != nil {
		return nil, err
	}

	return &inbox, nil
}

const querySeen = `update conversation set is_read = true where sender_id = $1 and recipient_id = $2`

func (ms *msgDao) UpdateSeen(ctx context.Context, senderUid int, recieverUid int) error {
	var db = ms.db

	_, err := db.Exec(ctx, querySeen, recieverUid, senderUid)
	if err != nil {
		return err
	}
	return nil
}
