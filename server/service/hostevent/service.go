package hostevent

import (
	"github.com/hepa/sports/repository/event"
)

type hostService struct {
	eventRepo event.EventRepo
}

func NewHostService(repo event.EventRepo) *hostService {
	return &hostService{
		eventRepo: repo,
	}
}
