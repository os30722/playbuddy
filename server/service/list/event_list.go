package list

import (
	"encoding/json"
	"net/http"
	"strconv"

	"github.com/hepa/sports/util"
	cerr "github.com/hepa/sports/util"
	"github.com/hepa/sports/vo"
)

func (ls listService) EventList(res http.ResponseWriter, req *http.Request) *cerr.AppError {
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

	var db = ls.eventRepo

	ctx := req.Context()
	uid, ok := util.GetUid(ctx)
	if !ok {
		return cerr.HttpError(nil, "", 500)
	}

	eventPages, err := db.GetEventList(ctx, uid, cursor, pageSize, params)
	if err != nil {
		return cerr.HttpError(err, "", 500)
	}

	eventList := vo.Page{
		TotalCount: len((*eventPages)),
		Items:      eventPages,
	}

	err = json.NewEncoder(res).Encode(eventList)
	if err != nil {
		return cerr.HttpError(err, "", 500)
	}

	return nil

}
