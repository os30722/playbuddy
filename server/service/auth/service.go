package auth

import "github.com/hepa/sports/repository/user"

type authService struct {
	userRepo user.UserRepo
}

func NewAuthService(repo user.UserRepo) *authService {
	return &authService{
		userRepo: repo,
	}
}
