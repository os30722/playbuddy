package chats

import (
	"encoding/json"
	"net/http"
	"strconv"

	"github.com/gorilla/mux"
	"github.com/hepa/sports/util"
	cerr "github.com/hepa/sports/util"
	"github.com/hepa/sports/vo"
)

func (cs chatService) GetConversation(res http.ResponseWriter, req *http.Request) *cerr.AppError {
	res.Header().Set("Content-Type", "application/json")

	recipient, err := strconv.Atoi(mux.Vars(req)["rec"])
	if err != nil {
		return cerr.HttpError(err, "", 400)
	}

	params := req.URL.Query()

	cursor, err := strconv.Atoi(params.Get("crs"))
	if err != nil {
		return cerr.HttpError(err, "", 400)
	}

	pageSize, err := strconv.Atoi(params.Get("pagesize"))
	if err != nil {
		return cerr.HttpError(err, "", 400)
	}

	var db = cs.msgRepo

	ctx := req.Context()
	uid, ok := util.GetUid(ctx)
	if !ok {
		return cerr.HttpError(nil, "", 500)
	}

	conversation, err := db.GetConversation(ctx, uid, recipient, cursor, pageSize)
	if err != nil {
		return cerr.HttpError(err, "", 500)
	}

	conversationPage := vo.Page{
		TotalCount: len((*conversation)),
		Items:      conversation,
	}

	err = json.NewEncoder(res).Encode(conversationPage)
	if err != nil {
		return cerr.HttpError(err, "", 500)
	}

	return nil

}

func (cs chatService) GetInbox(res http.ResponseWriter, req *http.Request) *cerr.AppError {
	res.Header().Set("Content-Type", "application/json")

	params := req.URL.Query()

	cursor, err := strconv.Atoi(params.Get("crs"))
	if err != nil {
		return cerr.HttpError(err, "", 400)
	}

	pageSize, err := strconv.Atoi(params.Get("pagesize"))
	if err != nil || cursor < 1 || pageSize > 200 || pageSize < 1 {
		return cerr.HttpError(err, "", 400)
	}

	var db = cs.msgRepo

	ctx := req.Context()
	uid, ok := util.GetUid(ctx)
	if !ok {
		return cerr.HttpError(nil, "", 500)
	}

	inbox, err := db.GetInbox(ctx, uid, cursor, pageSize)
	if err != nil {
		return cerr.HttpError(err, "", 500)
	}

	inboxPage := vo.Page{
		TotalCount: len((*inbox)),
		Items:      inbox,
	}

	err = json.NewEncoder(res).Encode(inboxPage)
	if err != nil {
		return cerr.HttpError(err, "", 500)
	}

	return nil

}
