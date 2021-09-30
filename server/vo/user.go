package vo

type UserCred struct {
	Email string `json:"email`
	Pass  string `json:"pass"`
	Id    int
}

type UserForm struct {
	FirstName string `json:"firstname"`
	LastName  string `json:"lastname"`
	Dob       string `json:"dob"`
	Gender    string `json:"gender"`
	Email     string `json:"email"`
	Pass      string `json:"pass"`
}

type UserPage struct {
	Uid        int    `json:"uid"`
	ProfilePic string `json:"profile_pic"`
	FirstName  string `json:"firstname"`
	LastName   string `json:"lastname"`
}

type Profile struct {
	User         UserPage `json:"user"`
	FriendStatus *string  `json:"friend_status"`
	NoEvents     int      `json:"no_events"`
	Friends      int      `json:"friends"`
	Reputation   int      `json:"reputation"`
}

type LoggedUser struct {
	DeviceId string `json:"device_id"`
}
