package chats

import (
	"github.com/hepa/sports/repository/message"
)

type chatService struct {
	msgRepo message.MsgRepo
}

func NewChatService(repo message.MsgRepo) *chatService {
	return &chatService{
		msgRepo: repo,
	}
}
