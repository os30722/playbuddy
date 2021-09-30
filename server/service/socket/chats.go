package socket

import (
	"context"
	"encoding/json"
	"fmt"
	"time"

	"github.com/hepa/sports/vo"
)

func (ss socketService) chatMessage(ctx context.Context, client *Client, data json.RawMessage) error {
	var msg vo.MsgRecv
	var db = ss.msgRepo

	err := json.Unmarshal(data, &msg)
	if err != nil {
		return err
	}

	fmt.Println(msg)

	timestamp := time.Now().UTC()
	convId, err := db.SaveConversation(ctx, client.user.Uid, msg, timestamp)
	if err != nil {
		fmt.Println(err)
		return err
	}

	var sendMsg vo.MsgSend
	sendMsg.Event = "chat_message"
	sendMsg.Data = vo.Conversation{
		Id:           convId,
		SenderUid:    client.user.Uid,
		RecipientUid: msg.RecvUid,
		Msg:          msg.Msg,
		TimeStamp:    timestamp,
	}

	err = client.conn.WriteJSON(sendMsg)
	if err != nil {
		fmt.Println(err)
		return err
	}

	recvClient, ok := ss.clients[msg.RecvUid]
	if !ok {
		fmt.Println("User Not Found")
		return nil
	}

	err = recvClient.conn.WriteJSON(sendMsg)
	if err != nil {
		fmt.Println(err)
		return err
	}

	return nil

}

func (ss socketService) chatSeen(ctx context.Context, client *Client, data json.RawMessage) error {
	fmt.Println("seen")
	var recieverUid int
	var db = ss.msgRepo

	err := json.Unmarshal(data, &recieverUid)
	if err != nil {
		return err
	}

	err = db.UpdateSeen(ctx, client.user.Uid, recieverUid)
	if err != nil {
		fmt.Println(err)
		return err
	}

	recvClient, ok := ss.clients[recieverUid]
	if !ok {
		fmt.Println("User Not Found")
		return nil
	}

	var sendMsg vo.MsgSend
	sendMsg.Event = "seen_message"
	sendMsg.Data = client.user.Uid

	err = recvClient.conn.WriteJSON(sendMsg)
	if err != nil {
		fmt.Println(err)
		return err
	}

	return nil
}
