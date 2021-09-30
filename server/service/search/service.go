package search

import (
	"github.com/hepa/sports/repository/user"
)

type searchService struct {
	userRepo user.UserRepo
}

func NewSearchService(repo user.UserRepo) *searchService {
	return &searchService{
		userRepo: repo,
	}
}
