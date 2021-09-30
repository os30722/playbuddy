package schedule

import (
	"encoding/json"
	"net/http"
	"strconv"

	"github.com/hepa/sports/util"
	cerr "github.com/hepa/sports/util"
	"github.com/hepa/sports/vo"
)

func (sc scheduleService) UserSchedule(res http.ResponseWriter, req *http.Request) *cerr.AppError {
	res.Header().Set("Content-Type", "application/json")

	params := req.URL.Query()

	cursor, err := strconv.Atoi(params.Get("crs"))
	if err != nil {
		return cerr.HttpError(err, "", 400)

	}

	pageSize, err := strconv.Atoi(params.Get("pagesize"))
	if err != nil || pageSize < 1 || pageSize > 200 || cursor < 0 {
		return cerr.HttpError(err, "", 400)
	}

	var db = sc.eventRepo

	ctx := req.Context()
	uid, ok := util.GetUid(ctx)
	if !ok {
		return cerr.HttpError(nil, "", 500)
	}

	eventPages, err := db.GetUserSchedule(ctx, uid, cursor, pageSize)
	if err != nil {
		return cerr.HttpError(err, "", 500)
	}

	eventList := vo.Page{
		TotalCount: len((*eventPages)),
		Items:      eventPages,
	}

	if err = json.NewEncoder(res).Encode(eventList); err != nil {
		return cerr.HttpError(err, "", 500)
	}

	return nil

}
