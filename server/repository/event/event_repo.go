package event

import (
	"context"
	"net/url"

	"github.com/hepa/sports/vo"
)

type EventRepo interface {
	HostEvent(ctx context.Context, uid int, event vo.Event) error
	GetEventDetail(ctx context.Context, uid int, eventId int) (*vo.EventPage, error)
	GetEventList(ctx context.Context, uid int, cursor int, pageSize int, filters url.Values) (*[]vo.EventPage, error)
	GetUserSchedule(ctx context.Context, uid int, cursor int, pageSize int) (*[]vo.EventPage, error)
	RequestEvent(ctx context.Context, uid int, eventId int, host bool) error
	LeaveEvent(ctx context.Context, uid int, eventId int) error
	AcceptRequest(ctx context.Context, uid int, eventId int) error
	RemoveFromEvent(ctx context.Context, uid int, eventId int) error
	GetJoinedUsers(ctx context.Context, eventId int, cursor int, pagesize int, filters url.Values) (*[]vo.UserPage, error)

	DeleteEvent(ctx context.Context, uid int, eventId int) error

	// For notification purpose
	FriendsPayload(ctx context.Context, uid int) ([]vo.RequestPayload, error)

	GetJoinRequests(ctx context.Context, eventId int, cursor int, pagesize int) (*[]vo.UserPage, error)
}
