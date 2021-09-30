package schedule

import (
	"github.com/hepa/sports/repository/event"
)

type scheduleService struct {
	eventRepo event.EventRepo
}

func NewScheduleService(repo event.EventRepo) *scheduleService {
	return &scheduleService{
		eventRepo: repo,
	}
}
