package player

import (
	"github.com/hepa/sports/repository/event"
)

type playerService struct {
	eventRepo event.EventRepo
}

func NewPlayerService(eventRepo event.EventRepo) *playerService {
	return &playerService{
		eventRepo: eventRepo,
	}
}
