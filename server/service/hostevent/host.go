package hostevent

import (
	"encoding/json"
	"net/http"
	"strconv"

	"github.com/hepa/sports/service/notification"
	"github.com/hepa/sports/util"
	cerr "github.com/hepa/sports/util"
	"github.com/hepa/sports/vo"
)

func (ho hostService) HostEvent(res http.ResponseWriter, req *http.Request) *cerr.AppError {
	res.Header().Set("Content-Type", "application/json")

	ctx := req.Context()
	uid, ok := util.GetUid(ctx)
	if !ok {
		return cerr.HttpError(nil, "", 500)
	}

	var db = ho.eventRepo
	var event vo.Event

	err := json.NewDecoder(req.Body).Decode(&event)
	if err != nil {
		return cerr.HttpError(err, "", 400)
	}

	//Validating Event
	validator := new(Validator)
	ok = validator.ValidateEvent(event)
	if ok != true {
		return cerr.HttpError(validator.err, "", 400)
	}

	err = db.HostEvent(req.Context(), uid, event)
	if err != nil {
		return cerr.HttpError(err, "", 500)
	}

	payloads, err := db.FriendsPayload(ctx, uid)
	if err != nil {
		return cerr.HttpError(err, "", 500)
	}
	notification.HostEventNotif(ctx, event, payloads)

	res.Write([]byte(`{"msg":"Successful"}`))
	return nil

}

func (ho hostService) RemoveEvent(res http.ResponseWriter, req *http.Request) *cerr.AppError {
	res.Header().Set("Content-Type", "application/json")

	params := req.URL.Query()

	eventid, err := strconv.Atoi(params.Get("eid"))
	if err != nil {
		return cerr.HttpError(err, "", 400)
	}

	ctx := req.Context()
	uid, ok := util.GetUid(ctx)
	if !ok {
		return cerr.HttpError(nil, "", 500)
	}

	var db = ho.eventRepo

	err = db.DeleteEvent(ctx, uid, eventid)
	if err != nil {
		return cerr.HttpError(err, "", 500)
	}

	res.Write([]byte(`{"msg":"Successful"}`))
	return nil
}
