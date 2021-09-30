package hostevent

import (
	"errors"
	"time"
	"unicode"

	"github.com/hepa/sports/vo"
)

type Validator struct {
	err error
}

func (v *Validator) ValidateEvent(event vo.Event) bool {
	if len(event.Sport) == 0 || len(event.Address) == 0 {
		return false
	}
	return v.ValidateSport(event.Sport) &&
		v.ValidateEventTime(event.EventDate.Time, event.StartTime.Time) &&
		v.ValidateEventTime(event.EventDate.Time, event.EndTime.Time)

}

func (v *Validator) ValidateEventTime(date time.Time, eventTime time.Time) bool {
	t, _ := time.Parse("2006-01-0215:04", date.Format("2006-01-02")+eventTime.Format("15:04"))

	if t.Before(time.Now().UTC().Add(-30*time.Minute)) || t.After(time.Now().UTC().AddDate(0, 1, 0)) {
		v.err = errors.New("time out of range")
		return false
	}
	return true
}

func (v *Validator) ValidateSport(name string) bool {
	for _, r := range name {
		if !unicode.IsLetter(r) {
			return false
		}
	}
	return true
}
