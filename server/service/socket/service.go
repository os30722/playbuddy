package socket

import (
	"context"
	"encoding/json"

	"github.com/gorilla/websocket"
	"github.com/hepa/sports/repository/message"
	"github.com/hepa/sports/repository/user"
	"github.com/hepa/sports/vo"
)

type socketService struct {
	clients  map[int]*Client
	socketOn map[string]func(context.Context, *Client, json.RawMessage) error
	msgRepo  message.MsgRepo
	userRepo user.UserRepo
}

type Client struct {
	user vo.UserPage
	conn *websocket.Conn
}

func initEventOn(service *socketService) {
	service.socketOn["chat_message"] = service.chatMessage
	service.socketOn["seen_message"] = service.chatSeen
}

func NewSocketService(userRepo user.UserRepo, msgRepo message.MsgRepo) *socketService {
	var service = &socketService{
		clients:  make(map[int]*Client),
		socketOn: make(map[string]func(context.Context, *Client, json.RawMessage) error),
		msgRepo:  msgRepo,
		userRepo: userRepo,
	}
	initEventOn(service)
	return service

}
