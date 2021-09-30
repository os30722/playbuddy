package list

import "github.com/hepa/sports/repository/event"

type listService struct {
	eventRepo event.EventRepo
}

func NewListService(repo event.EventRepo) *listService {
	return &listService{
		eventRepo: repo,
	}
}
