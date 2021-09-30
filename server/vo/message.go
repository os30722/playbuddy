package vo

import (
	"encoding/json"
	"time"
)

type MsgRecv struct {
	RecvUid int    `json:"recv_uid"`
	Msg     string `json:"msg"`
}

type MsgSend struct {
	Event string      `json:"event"`
	Data  interface{} `json:"data"`
}

type SocketEvent struct {
	Event string          `json:"event"`
	Data  json.RawMessage `json:"data"`
}

type Conversation struct {
	Id           int       `json:"id"`
	SenderUid    int       `json:"sender_uid""`
	RecipientUid int       `json:"recipient_uid"`
	IsRead       bool      `json:"is_read"`
	Msg          string    `json:"msg"`
	TimeStamp    time.Time `json:"timestamp"`
}

type Inbox struct {
	User      UserPage `json:"user"`
	Id        int      `json:"id"`
	SenderUid int      `json:"sender_uid"`
	IsRead    bool     `json:"is_read"`
	Msg       string   `json:"msg"`
}
