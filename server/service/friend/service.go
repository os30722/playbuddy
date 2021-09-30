package friend

import (
	"github.com/hepa/sports/repository/user"
)

type friendService struct {
	userRepo user.UserRepo
}

func NewFriendService(userRepo user.UserRepo) *friendService {
	return &friendService{
		userRepo: userRepo,
	}
}
