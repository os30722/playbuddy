package info

import (
	"github.com/hepa/sports/repository/event"
)

type infoService struct {
	eventRepo event.EventRepo
}

func NewInfoService(eventRepo event.EventRepo) *infoService {
	return &infoService{
		eventRepo: eventRepo,
	}
}
