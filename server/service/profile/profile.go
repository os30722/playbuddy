package profile

import (
	"encoding/json"
	"net/http"
	"strconv"

	"github.com/gorilla/mux"
	"github.com/hepa/sports/util"
	cerr "github.com/hepa/sports/util"
)

func (ps profileService) UserProfile(res http.ResponseWriter, req *http.Request) *cerr.AppError {
	res.Header().Set("Content-Type", "application/json")

	var db = ps.userRepo

	ctx := req.Context()

	profileId, err := strconv.Atoi(mux.Vars(req)["uid"])
	if err != nil {
		return cerr.HttpError(err, "", 500)

	}

	uid, ok := util.GetUid(ctx)
	if !ok {
		return cerr.HttpError(nil, "", 500)
	}

	profile, err := db.GetProfile(ctx, uid, profileId)
	if err != nil {
		return cerr.HttpError(err, "", 500)
	}

	if err = json.NewEncoder(res).Encode(profile); err != nil {
		return cerr.HttpError(err, "", 500)
	}

	return nil
}
