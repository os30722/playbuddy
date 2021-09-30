package profile

import (
	"github.com/hepa/sports/repository/user"
)

type profileService struct {
	userRepo user.UserRepo
}

func NewProfileService(repo user.UserRepo) *profileService {
	return &profileService{
		userRepo: repo,
	}
}
