package auth

import (
	"encoding/json"
	"fmt"
	"net/http"

	"github.com/jackc/pgx/v4"
	"golang.org/x/crypto/bcrypt"

	"github.com/hepa/sports/vo"

	cerr "github.com/hepa/sports/util"
)

func (au authService) Login(res http.ResponseWriter, req *http.Request) *cerr.AppError {
	res.Header().Set("Content-Type", "application/json")
	var repo = au.userRepo
	var userInput vo.UserCred

	err := json.NewDecoder(req.Body).Decode(&userInput)
	if err != nil {
		return cerr.HttpError(err, "", 500)
	}

	if userInput.Email == "" && len(userInput.Pass) < 0 {
		return cerr.HttpError(err, "", 500)
	}

	userCred, err := repo.GetUserCred(req.Context(), userInput.Email)
	if err != nil {
		if err == pgx.ErrNoRows {
			return cerr.HttpError(err, "", 401)
		}
		return cerr.HttpError(err, "", 500)
	}

	err = bcrypt.CompareHashAndPassword([]byte(userCred.Pass), []byte(userInput.Pass))
	if err != nil {
		return cerr.HttpError(err, "", 401)
	}

	refreshToken, err := generateRefreshToken(userCred.Id)
	accessToken, err := generateAccessToken(userCred.Id)

	if err != nil {
		return cerr.HttpError(err, "", 500)
	}

	res.WriteHeader(200)
	res.Write([]byte(fmt.Sprintf(`{"uid":"%v","refresh_token":"%v","access_token":"%v"}`, userCred.Id, refreshToken, accessToken)))
	return nil
}

func (au authService) SignUp(res http.ResponseWriter, req *http.Request) *cerr.AppError {
	res.Header().Set("Content-Type", "application/json")
	fmt.Println("Jit")
	var form vo.UserForm
	var repo = au.userRepo
	var ctx = req.Context()

	err := json.NewDecoder(req.Body).Decode(&form)
	if err != nil {
		return cerr.HttpError(err, "", 500)
	}

	// @TODO = To validate user input

	test, err := repo.FindDuplicate(ctx, form.Email)
	if err != nil {
		return cerr.HttpError(err, "", 500)
	}
	if test == true {
		return cerr.HttpError(err, "", 409)
	}

	id, err := repo.SignUp(ctx, &form)
	if err != nil {
		return cerr.HttpError(err, "", 500)
	}

	refreshToken, err := generateRefreshToken(id)
	accessToken, err := generateAccessToken(id)

	if err != nil {
		return cerr.HttpError(err, "", 500)
	}

	res.WriteHeader(200)
	res.Write([]byte(fmt.Sprintf(`{"uid":"%v","refresh_token":"%v","access_token":"%v"}`, id, refreshToken, accessToken)))
	return nil

}
