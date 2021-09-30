package vo

import (
	"fmt"
	"strings"
	"time"

	"github.com/jackc/pgtype"
)

type Event struct {
	Sport       string      `json:"sport"`
	EventDate   pgtype.Date `json:"event_date"`
	StartTime   DispTime    `json:"start_time"`
	EndTime     DispTime    `json:"end_time"`
	Instruction string      `json:"instruction"`
	Area        Location    `json:"location"`
	Address     string      `json:"address"`
	NoPlayer    int         `json:"no_players"`
}

type EventPage struct {
	EventId      int         `json:"event_id"`
	Sport        string      `json:"sport"`
	EventDate    pgtype.Date `json:"event_date"`
	StartTime    DispTime    `json:"start_time"`
	EndTime      DispTime    `json:"end_time"`
	Address      string      `json:"address"`
	TotalPlayer  int         `json:"total_player"`
	Joined       int         `json:"joined"`
	Requests     *int        `json:"requests"`
	Host         UserPage    `json:"host"`
	Instruction  string      `json:"instruction"`
	PlayerJoined string      `json:"player_joined"`
}

type DispTime struct {
	time.Time
}

func (d *DispTime) UnmarshalJSON(data []byte) error {
	s := strings.Trim(string(data), "\"")
	t, err := time.Parse("15:04", s)
	if err != nil {
		return err
	}
	d.Time = t
	return nil
}

func (d DispTime) MarshalJSON() ([]byte, error) {
	stamp := fmt.Sprintf("\"%s\"", d.Format("15:04"))
	return []byte(stamp), nil
}
