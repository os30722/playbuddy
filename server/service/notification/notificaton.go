package notification

import (
	"context"
	"fmt"

	firebase "firebase.google.com/go"
	"firebase.google.com/go/messaging"
	"github.com/hepa/sports/vo"
	"google.golang.org/api/option"
)

var app *firebase.App

func init() {

	/*
		Place the directory of credential file downloaded from firebase console
		inside this function parameter.
	*/

	opt := option.WithCredentialsFile("")
	var err error
	app, err = firebase.NewApp(context.Background(), nil, opt)
	if err != nil {
		fmt.Errorf("Error initializing app: %v", err)
	}
}

func FriendRequest(ctx context.Context, payload *vo.RequestPayload) {

	client, err := app.Messaging(ctx)
	if err != nil {
		fmt.Println("Error Messaging")
	}

	msg := &messaging.Message{
		Notification: &messaging.Notification{
			Title: "Friend Request",
			Body:  payload.Sender + " sent you a friend request.",
		},
		Token: payload.RecieverToken,
	}

	client.Send(ctx, msg)

}

func HostEventNotif(ctx context.Context, event vo.Event, payloads []vo.RequestPayload) {
	msgs := make([]*messaging.Message, 0, len(payloads))
	client, err := app.Messaging(ctx)
	if err != nil {
		fmt.Println("Error Messaging")
	}
	for _, p := range payloads {
		msg := &messaging.Message{
			Notification: &messaging.Notification{
				Title: event.Sport,
				Body:  p.Sender + " hosted a new event",
			},
			Token: p.RecieverToken,
		}
		msgs = append(msgs, msg)
	}
	client.SendAll(ctx, msgs)

}
