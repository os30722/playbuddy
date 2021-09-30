package hostevent

import (
	"testing"
	"time"

	"github.com/hepa/sports/vo"
	"github.com/jackc/pgtype"
)

func TestValidateEvent(t *testing.T) {

	var date = pgtype.Date{
		Time: time.Now().UTC(),
	}
	var startTime = vo.DispTime{time.Now().UTC()}
	var endTime = vo.DispTime{time.Now().UTC().Add(2 * time.Hour)}

	var tt = []struct {
		name  string
		event vo.Event
		res   bool
	}{

		{name: "valid input", event: vo.Event{"Football", date, startTime, endTime, "This is a test",
			vo.Location{13.99, 12.34}, "Flate no 102", 8}, res: true},

		{name: "empty sport field", event: vo.Event{"", date, startTime, endTime, "This is a test",
			vo.Location{13.99, 12.34}, "Flate no 102", 8}, res: false},

		{name: "empty address field", event: vo.Event{"Football", date, startTime, endTime, "This is a test",
			vo.Location{13.99, 12.34}, "", 8}, res: false},
	}

	validator := new(Validator)

	for _, tc := range tt {
		t.Run(tc.name, func(t *testing.T) {
			if got := validator.ValidateEvent(tc.event); got != tc.res {
				t.Fatalf("ValidateEventDate(%v) = %v ; expected = %v", tc.event, got, tc.res)
			}
		})

	}
}

func TestValidateEventTime(t *testing.T) {
	type eventTime struct {
		date time.Time
		time time.Time
	}
	var tt = []struct {
		name string
		time eventTime
		res  bool
	}{
		{"current date", eventTime{time.Now().UTC(), time.Now().UTC()}, true},
		{"previous date", eventTime{time.Now().UTC(), time.Now().UTC().Add(-4 * time.Hour)}, false},
		{"after 1 month", eventTime{time.Now().UTC().AddDate(0, 1, 1), time.Now().UTC()}, false},
	}
	validator := new(Validator)
	for _, tc := range tt {
		t.Run(tc.name, func(t *testing.T) {
			if got := validator.ValidateEventTime(tc.time.date, tc.time.time); got != tc.res {
				t.Fatalf("ValidateEventDate(%q, %q) = %v ; expected = %v", tc.time.date, tc.time.time, got, tc.res)
			}
		})

	}
}

func TestValidateSports(t *testing.T) {

	var tt = []struct {
		name      string
		sportName string
		res       bool
	}{
		{"valid sports name", "Football", true},
		{"sport name(containing digit)", "Foo33ede", false},
		{"sport special character", "Foo&&:", false},
	}
	validator := new(Validator)
	for _, tc := range tt {
		t.Run(tc.name, func(t *testing.T) {
			if got := validator.ValidateSport(tc.sportName); got != tc.res {
				t.Fatalf("ValidateEventDate(%q) = %v ; expected = %v", tc.sportName, got, tc.res)
			}
		})

	}
}
