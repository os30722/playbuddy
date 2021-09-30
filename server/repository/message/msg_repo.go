package message

import (
	"context"
	"time"

	"github.com/hepa/sports/vo"
)

type MsgRepo interface {
	SaveConversation(ctx context.Context, senderUid int, msg vo.MsgRecv, timestamp time.Time) (int, error)
	GetConversation(ctx context.Context, uid int, senderUid int, cursor int, pageSize int) (*[]vo.Conversation, error)
	GetInbox(ctx context.Context, uid int, cursor int, pageSize int) (*[]vo.Inbox, error)
	UpdateSeen(ctx context.Context, uid int, recipeintUid int) error
}
