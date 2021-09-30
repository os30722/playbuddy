package search

import (
	"encoding/json"
	"net/http"
	"strconv"

	"github.com/gorilla/mux"
	cerr "github.com/hepa/sports/util"
	"github.com/hepa/sports/vo"
)

func (ss searchService) SearchUsers(res http.ResponseWriter, req *http.Request) *cerr.AppError {
	res.Header().Set("Content-Type", "application/json")

	params := req.URL.Query()

	cursor, err := strconv.Atoi(params.Get("crs"))
	pageSize, err := strconv.Atoi(params.Get("pagesize"))
	query := params.Get("q")

	if err != nil || pageSize < 1 || cursor < 1 || pageSize > 200 {
		return cerr.HttpError(err, "", 400)
	}

	var db = ss.userRepo
	ctx := req.Context()

	userPages, err := db.SearchUsers(ctx, query, cursor, pageSize)
	if err != nil {
		return cerr.HttpError(err, "", 500)
	}

	userList := vo.Page{
		TotalCount: len((*userPages)),
		Items:      userPages,
	}

	if err = json.NewEncoder(res).Encode(userList); err != nil {
		return cerr.HttpError(err, "", 500)
	}

	return nil
}

func (ss searchService) SearchUser(res http.ResponseWriter, req *http.Request) *cerr.AppError {
	res.Header().Set("Content-Type", "application/json")

	uid, err := strconv.Atoi(mux.Vars(req)["uid"])
	if err != nil {
		return cerr.HttpError(err, "", 400)
	}

	var db = ss.userRepo
	ctx := req.Context()

	userPage, err := db.FindUser(ctx, uid)
	if err != nil {
		return cerr.HttpError(err, "", 500)
	}

	if err = json.NewEncoder(res).Encode(userPage); err != nil {
		return cerr.HttpError(err, "", 500)
	}

	return nil
}
