package player

import (
	"encoding/json"
	"net/http"
	"strconv"

	"github.com/gorilla/mux"
	"github.com/hepa/sports/util"
	cerr "github.com/hepa/sports/util"
	"github.com/hepa/sports/vo"
)

func (ps playerService) RequestEvent(res http.ResponseWriter, req *http.Request) *cerr.AppError {
	res.Header().Set("Content-Type", "application/json")

	eventId, err := strconv.Atoi(mux.Vars(req)["eid"])

	if err != nil || eventId < 1 {
		return cerr.HttpError(err, "", 400)
	}

	var db = ps.eventRepo

	ctx := req.Context()
	uid, ok := util.GetUid(ctx)
	if !ok {
		return cerr.HttpError(nil, "", 500)
	}

	err = db.RequestEvent(ctx, uid, eventId, false)
	if err != nil {
		return cerr.HttpError(err, "", 500)
	}

	res.Write([]byte(`{"msg":"Successful"}`))

	return nil

}

func (ps playerService) GetEventRequest(res http.ResponseWriter, req *http.Request) *cerr.AppError {
	res.Header().Set("Content-Type", "application/json")

	params := req.URL.Query()

	eventId, err := strconv.Atoi(mux.Vars(req)["eid"])
	cursor, err := strconv.Atoi(params.Get("crs"))
	pageSize, err := strconv.Atoi(params.Get("pagesize"))

	if err != nil {
		return cerr.HttpError(err, "", 400)
	}
	var db = ps.eventRepo

	ctx := req.Context()
	_, ok := util.GetUid(ctx)
	if !ok {
		return cerr.HttpError(nil, "", 400)
	}

	users, err := db.GetJoinRequests(ctx, eventId, cursor, pageSize)
	if err != nil {
		return cerr.HttpError(err, "", 500)
	}

	userList := vo.Page{
		TotalCount: len((*users)),
		Items:      users,
	}

	if err = json.NewEncoder(res).Encode(userList); err != nil {
		return cerr.HttpError(err, "", 500)
	}

	return nil

}

func (ps playerService) AcceptRequest(res http.ResponseWriter, req *http.Request) *cerr.AppError {
	res.Header().Set("Content-Type", "application/json")

	params := req.URL.Query()

	eventId, err := strconv.Atoi(mux.Vars(req)["eid"])
	if err != nil {
		return cerr.HttpError(err, "", 400)
	}

	playerId, err := strconv.Atoi(params.Get("uid"))
	if err != nil {
		return cerr.HttpError(err, "", 400)
	}

	var db = ps.eventRepo

	ctx := req.Context()

	err = db.AcceptRequest(ctx, playerId, eventId)
	if err != nil {
		return cerr.HttpError(err, "", 500)
	}

	res.Write([]byte(`{"msg":"Successful"}`))

	return nil
}

func (ps playerService) LeaveEvent(res http.ResponseWriter, req *http.Request) *cerr.AppError {
	res.Header().Set("Content-Type", "application/json")

	eventId, err := strconv.Atoi(mux.Vars(req)["eid"])
	if err != nil || eventId < 1 {
		return cerr.HttpError(err, "", 400)
	}

	var db = ps.eventRepo

	ctx := req.Context()
	uid, ok := util.GetUid(ctx)
	if !ok {
		return cerr.HttpError(nil, "", 500)
	}

	err = db.LeaveEvent(ctx, uid, eventId)
	if err != nil {
		return cerr.HttpError(err, "", 500)
	}

	res.Write([]byte(`{"msg":"Successful"}`))

	return nil
}

func (ps playerService) RemoveFromEvent(res http.ResponseWriter, req *http.Request) *cerr.AppError {
	res.Header().Set("Content-Type", "application/json")

	params := req.URL.Query()

	eventId, err := strconv.Atoi(mux.Vars(req)["eid"])
	playerId, err := strconv.Atoi(params.Get("uid"))

	if err != nil {
		return cerr.HttpError(err, "", 400)
	}

	var db = ps.eventRepo

	ctx := req.Context()

	err = db.RemoveFromEvent(ctx, playerId, eventId)
	if err != nil {
		return cerr.HttpError(err, "", 500)
	}

	res.Write([]byte(`{"msg":"Successful"}`))

	return nil
}
