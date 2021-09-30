package info

import (
	"encoding/json"
	"net/http"
	"strconv"

	"github.com/gorilla/mux"
	"github.com/hepa/sports/util"
	cerr "github.com/hepa/sports/util"
	"github.com/hepa/sports/vo"
)

func (is infoService) EventDetail(res http.ResponseWriter, req *http.Request) *cerr.AppError {
	res.Header().Set("Content-Type", "application/json")

	eventId, err := strconv.Atoi(mux.Vars(req)["eid"])
	if err != nil || eventId < 1 {
		return cerr.HttpError(err, "", 400)
	}

	var db = is.eventRepo

	ctx := req.Context()
	uid, ok := util.GetUid(ctx)
	if !ok {
		return cerr.HttpError(nil, "", 500)
	}

	info, err := db.GetEventDetail(req.Context(), uid, eventId)
	if err != nil {
		return cerr.HttpError(err, "", 500)
	}

	if err = json.NewEncoder(res).Encode(info); err != nil {
		return cerr.HttpError(err, "", 500)
	}

	return nil

}

func (is infoService) GetJoinedUsers(res http.ResponseWriter, req *http.Request) *cerr.AppError {
	res.Header().Set("Content-Type", "application/json")

	params := req.URL.Query()

	eventId, err := strconv.Atoi(mux.Vars(req)["eid"])
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

	var db = is.eventRepo

	ctx := req.Context()

	users, err := db.GetJoinedUsers(ctx, eventId, cursor, pageSize, params)
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
