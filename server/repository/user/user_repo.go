package user

import (
	"context"
	"net/url"

	"github.com/hepa/sports/vo"
)

type UserRepo interface {
	GetUserCred(ctx context.Context, email string) (*vo.UserCred, error)
	FindDuplicate(ctx context.Context, email string) (bool, error)
	SignUp(ctx context.Context, userForm *vo.UserForm) (int, error)
	SearchUsers(ctx context.Context, query string, cursor int, pageSize int) (*[]vo.UserPage, error)
	GetProfile(ctx context.Context, uid int, profileId int) (*vo.Profile, error)
	LoginUser(ctx context.Context, uid int, devId string) error
	FindUser(ctx context.Context, uid int) (*vo.UserPage, error)
	GetFriendsList(ctx context.Context, uid int, cursor int, pageSize int, filters url.Values) (*[]vo.UserPage, error)

	RequestFriend(ctx context.Context, uid int, friendId int) error
	FriendRequests(ctx context.Context, uid int, cursor int, pageSize int) (*[]vo.UserPage, error)
	AcceptFriend(ctx context.Context, uid int, friendId int) error
	RemoveFriend(ctx context.Context, uid int, friendId int) error

	// For notification purpose
	FriendRequestData(ctx context.Context, uid int, friendId int) (*vo.RequestPayload, error)
}
