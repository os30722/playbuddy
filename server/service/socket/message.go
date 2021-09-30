package socket

import (
	"context"
	"fmt"
	"net/http"

	"github.com/gorilla/websocket"
	"github.com/hepa/sports/util"
	cerr "github.com/hepa/sports/util"
	"github.com/hepa/sports/vo"
)

var upgrader = websocket.Upgrader{
	ReadBufferSize:  1024,
	WriteBufferSize: 1024,
	CheckOrigin:     func(r *http.Request) bool { return true },
}

func (ss socketService) ServerWs(res http.ResponseWriter, req *http.Request) *cerr.AppError {

	ctx := req.Context()
	uid, ok := util.GetUid(ctx)
	if !ok {
		return cerr.HttpError(nil, "", 500)
	}

	user, err := ss.userRepo.FindUser(ctx, uid)
	if err != nil {
		return cerr.HttpError(err, "", 500)
	}

	conn, err := upgrader.Upgrade(res, req, nil)
	if err != nil {
		return cerr.HttpError(err, "", 500)
	}

	client := &Client{user: *user, conn: conn}
	ss.clients[uid] = client

	fmt.Println("Connection Established....")
	go ss.msgPump(client)

	return nil

}

func (ss socketService) msgPump(c *Client) {
	var recv vo.SocketEvent
	ctx := context.Background()
	ctx, cancel := context.WithCancel(ctx)

	defer cancel()

	for {
		if err := c.conn.ReadJSON(&recv); err != nil {
			fmt.Println("Closing connection")
			fmt.Println(err)
			return
		}
		fmt.Println(recv)

		// var send vo.MsgSend
		eventType := recv.Event

		f, ok := ss.socketOn[eventType]
		if !ok {
			fmt.Print("Handler Not Found")
			return
		}

		err := f(ctx, c, recv.Data)
		if err != nil {
			fmt.Println(err)
		}

	}

}
