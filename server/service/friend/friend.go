package friend

import (
	"encoding/json"
	"net/http"
	"strconv"

	"github.com/gorilla/mux"
	"github.com/hepa/sports/service/notification"
	"github.com/hepa/sports/util"
	cerr "github.com/hepa/sports/util"
	"github.com/hepa/sports/vo"
)

func (fs friendService) RequestFriend(res http.ResponseWriter, req *http.Request) *cerr.AppError {
	res.Header().Set("Content-Type", "application/json")

	friendId, err := strconv.Atoi(mux.Vars(req)["uid"])
	if err != nil {
		return cerr.HttpError(err, "", 400)
	}

	var db = fs.userRepo

	ctx := req.Context()
	uid, ok := util.GetUid(ctx)
	if !ok {
		return cerr.HttpError(nil, "", 400)
	}

	err = db.RequestFriend(ctx, uid, friendId)
	if err != nil {
		return cerr.HttpError(err, "", 500)
	}

	payload, err := db.FriendRequestData(ctx, uid, friendId)
	if err != nil {
		return cerr.HttpError(err, "", 500)
	}

	notification.FriendRequest(ctx, payload)

	res.Write([]byte(`{"msg":"Successful"}`))
	return nil
}

func (fs friendService) FriendRequests(res http.ResponseWriter, req *http.Request) *cerr.AppError {
	res.Header().Set("Content-Type", "application/json")

	params := req.URL.Query()

	cursor, err := strconv.Atoi(params.Get("crs"))
	pageSize, err := strconv.Atoi(params.Get("pagesize"))
	if err != nil {
		return cerr.HttpError(err, "", 500)
	}

	var db = fs.userRepo

	ctx := req.Context()
	uid, ok := util.GetUid(ctx)
	if !ok {
		return cerr.HttpError(nil, "", 500)
	}

	friends, err := db.FriendRequests(ctx, uid, cursor, pageSize)
	if err != nil {
		return cerr.HttpError(err, "", 500)
	}

	userList := vo.Page{
		TotalCount: len((*friends)),
		Items:      friends,
	}

	if err = json.NewEncoder(res).Encode(userList); err != nil {
		return cerr.HttpError(err, "", 500)
	}

	return nil
}

func (fs *friendService) AcceptFriend(res http.ResponseWriter, req *http.Request) *cerr.AppError {
	res.Header().Set("Content-Type", "application/json")

	friendId, err := strconv.Atoi(mux.Vars(req)["uid"])
	if err != nil {
		return cerr.HttpError(err, "", 400)
	}

	var db = fs.userRepo

	ctx := req.Context()
	uid, ok := util.GetUid(ctx)
	if !ok {
		return cerr.HttpError(nil, "", 400)
	}

	err = db.AcceptFriend(ctx, uid, friendId)
	if err != nil {
		return cerr.HttpError(nil, "", 500)
	}

	res.Write([]byte(`{"msg":"Successful"}`))
	return nil
}

func (fs friendService) RemoveFriend(res http.ResponseWriter, req *http.Request) *cerr.AppError {
	res.Header().Set("Content-Type", "application/json")

	friendId, err := strconv.Atoi(mux.Vars(req)["uid"])
	if err != nil {
		return cerr.HttpError(err, "", 400)
	}

	var db = fs.userRepo

	ctx := req.Context()
	uid, ok := util.GetUid(ctx)
	if !ok {
		return cerr.HttpError(nil, "", 500)
	}

	err = db.RemoveFriend(ctx, uid, friendId)
	if err != nil {
		return cerr.HttpError(err, "", 500)
	}

	res.Write([]byte(`{"msg":"Successful"}`))
	return nil
}

func (fs friendService) GetFriendsList(res http.ResponseWriter, req *http.Request) *cerr.AppError {
	res.Header().Set("Content-Type", "application/json")

	params := req.URL.Query()

	uid, err := strconv.Atoi(mux.Vars(req)["uid"])
	if err != nil {
		return cerr.HttpError(err, "", 400)
	}

	cursor, err := strconv.Atoi(params.Get("crs"))
	if err != nil {
		return cerr.HttpError(err, "", 400)
	}

	pageSize, err := strconv.Atoi(params.Get("pagesize"))
	if err != nil {
		return cerr.HttpError(err, "", 400)
	}

	var db = fs.userRepo
	ctx := req.Context()

	friends, err := db.GetFriendsList(ctx, uid, cursor, pageSize, params)
	if err != nil {
		return cerr.HttpError(err, "", 500)
	}

	userList := vo.Page{
		TotalCount: len((*friends)),
		Items:      friends,
	}

	if err = json.NewEncoder(res).Encode(userList); err != nil {
		return cerr.HttpError(err, "", 500)
	}

	return nil
}
