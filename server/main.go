package main

import (
	"log"
	"net/http"

	"github.com/gorilla/mux"
	"github.com/hepa/sports/database"
	md "github.com/hepa/sports/middleware"
	"github.com/hepa/sports/repository/event"
	"github.com/hepa/sports/repository/message"
	"github.com/hepa/sports/repository/user"
	"github.com/hepa/sports/service/auth"
	"github.com/hepa/sports/service/chats"
	"github.com/hepa/sports/service/friend"
	"github.com/hepa/sports/service/hostevent"
	"github.com/hepa/sports/service/info"
	"github.com/hepa/sports/service/list"
	"github.com/hepa/sports/service/player"
	"github.com/hepa/sports/service/profile"
	"github.com/hepa/sports/service/schedule"
	"github.com/hepa/sports/service/search"
	"github.com/hepa/sports/service/socket"
)

func main() {
	port := ":3000"

	//Creating And Setting Server
	server := http.Server{
		Addr:    port,
		Handler: router(),
	}

	server.ListenAndServe()
}

func router() *mux.Router {
	router := mux.NewRouter()

	db, err := database.GetPostgres()
	if err != nil || db == nil {
		log.Fatal(err)
	}

	//Static image file server
	fs := http.FileServer(http.Dir("./imagestore"))

	// Dao's
	userDao := user.GetUserDao(db)
	eventDao := event.GetEventDao(db)
	msgDao := message.GetMsgDao(db)

	// For Autorizaton Serivce
	authService := auth.NewAuthService(userDao)
	router.PathPrefix("/imagestore/").Handler(http.StripPrefix("/imagestore/", fs))
	router.Handle("/login", md.ErrHandler(authService.Login)).Methods("Post")
	router.Handle("/signup", md.ErrHandler(authService.SignUp)).Methods("Post")
	router.Handle("/register", md.ErrHandler(md.Authenticate(authService.RegisterDevice))).Methods("POST")

	//Refresh Token
	router.Handle("/refresh", md.ErrHandler(authService.RefreshToken)).Methods("POST")

	//For Hosting Service
	hostService := hostevent.NewHostService(eventDao)
	router.Handle("/host", md.ErrHandler(md.Authenticate(hostService.HostEvent))).Methods("POST")
	router.Handle("/remove/event", md.ErrHandler(md.Authenticate(hostService.RemoveEvent)))

	// For Scheduling Serive
	scheduleService := schedule.NewScheduleService(eventDao)
	router.Handle("/user/schedule", md.ErrHandler(md.Authenticate(scheduleService.UserSchedule)))

	//For Event Information Service
	infoService := info.NewInfoService(eventDao)
	router.Handle("/event/{eid}", md.ErrHandler(md.Authenticate(infoService.EventDetail)))
	router.Handle("/event/{eid}/joined", md.ErrHandler(md.Authenticate(infoService.GetJoinedUsers)))

	//For Event Recommendation Service
	listService := list.NewListService(eventDao)
	router.Handle("/list/event", md.ErrHandler(md.Authenticate(listService.EventList)))

	// For Player Services
	playerService := player.NewPlayerService(eventDao)
	router.Handle("/player/join/{eid}", md.ErrHandler(md.Authenticate(playerService.RequestEvent)))
	router.Handle("/player/leave/{eid}", md.ErrHandler(md.Authenticate(playerService.LeaveEvent)))
	router.Handle("/event/requests/{eid}", md.ErrHandler(md.Authenticate(playerService.GetEventRequest)))
	router.Handle("/event/accept/{eid}", md.ErrHandler(md.Authenticate(playerService.AcceptRequest)))
	router.Handle("/event/remove/{eid}", md.ErrHandler(md.Authenticate(playerService.RemoveFromEvent)))

	// For Search Service
	searchService := search.NewSearchService(userDao)
	router.Handle("/search/user", md.ErrHandler(md.Authenticate(searchService.SearchUsers)))
	router.Handle("/user/{uid}", md.ErrHandler(md.Authenticate(searchService.SearchUser)))

	// For Profile Service
	profileService := profile.NewProfileService(userDao)
	router.Handle("/user/profile/{uid}", md.ErrHandler(md.Authenticate(profileService.UserProfile)))

	// For Friend Service
	friendService := friend.NewFriendService(userDao)
	router.Handle("/friend/request/{uid}", md.ErrHandler(md.Authenticate(friendService.RequestFriend)))
	router.Handle("/friends", md.ErrHandler(md.Authenticate(friendService.FriendRequests)))
	router.Handle("/friend/accept/{uid}", md.ErrHandler(md.Authenticate(friendService.AcceptFriend)))
	router.Handle("/friend/remove/{uid}", md.ErrHandler(md.Authenticate(friendService.RemoveFriend)))
	router.Handle("/user/{uid}/friends", md.ErrHandler(md.Authenticate(friendService.GetFriendsList)))

	// For Messaging Service (This is a WebSocket implementation)
	socketService := socket.NewSocketService(userDao, msgDao)
	router.Handle("/msg/ws", md.ErrHandler(md.Authenticate(socketService.ServerWs)))

	// For Chat Service
	chatService := chats.NewChatService(msgDao)
	router.Handle("/conversation/{rec}", md.ErrHandler(md.Authenticate(chatService.GetConversation)))
	router.Handle("/inbox", md.ErrHandler(md.Authenticate(chatService.GetInbox)))

	return router

}
