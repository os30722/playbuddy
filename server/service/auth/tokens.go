package auth

import (
	"encoding/json"
	"fmt"
	"net/http"
	"strings"
	"time"

	"github.com/dgrijalva/jwt-go"
	"github.com/hepa/sports/env"
	"github.com/hepa/sports/util"
	cerr "github.com/hepa/sports/util"
	"github.com/hepa/sports/vo"
)

var (
	accessKey  = env.AccessKey
	refreshKey = env.RefreshKey
)

func generateRefreshToken(id int) (string, error) {

	token := jwt.NewWithClaims(jwt.SigningMethodHS256, jwt.MapClaims{
		"authorized": true,
		"UID":        id,
	})

	refreshToken, err := token.SignedString(refreshKey)

	return refreshToken, err
}

func generateAccessToken(id int) (string, error) {

	token := jwt.NewWithClaims(jwt.SigningMethodHS256, jwt.MapClaims{
		"authorized": true,
		"UID":        id,
		"iat":        time.Now(),
		"exp":        time.Now().Add(time.Minute * 30).Unix(),
	})

	accessToken, err := token.SignedString(accessKey)

	return accessToken, err
}

//To generate new tokens from refresh token
//@@TODO - To perfrom database look up of refresh tokens
func (au *authService) RefreshToken(res http.ResponseWriter, req *http.Request) *util.AppError {

	res.Header().Set("Content-Type", "application/json")

	if req.Header["Authorization"] != nil {
		t := strings.Split(req.Header["Authorization"][0], " ")[1]
		token, err := jwt.Parse(t, func(token *jwt.Token) (interface{}, error) {
			if _, ok := token.Method.(*jwt.SigningMethodHMAC); !ok {
				res.WriteHeader(500)
				return nil, fmt.Errorf("Illegal Signing Method")
			}

			return env.RefreshKey, nil
		})

		if err != nil {
			return util.HttpError(err, "", 401)
		}

		if claims, ok := token.Claims.(jwt.MapClaims); ok && token.Valid {
			id, ok := claims[util.UidKey].(float64)
			if !ok {
				return util.HttpError(nil, "", 500)
			}
			refreshToken, err := generateRefreshToken(int(id))
			accessToken, err := generateAccessToken(int(id))

			if err != nil {
				return util.HttpError(err, "", 500)
			}

			res.Write([]byte(fmt.Sprintf(`{"refresh_token":"%v","access_token":"%v"}`, refreshToken, accessToken)))
		}

	} else {
		return util.HttpError(nil, "", 401)
	}

	return nil

}

func (au *authService) RegisterDevice(res http.ResponseWriter, req *http.Request) *util.AppError {
	res.Header().Set("Content-Type", "application/json")

	fmt.Println("Registeres")

	var db = au.userRepo
	var loggedUser vo.LoggedUser

	ctx := req.Context()
	uid, ok := util.GetUid(ctx)
	if !ok {
		return cerr.HttpError(nil, "", 500)
	}

	err := json.NewDecoder(req.Body).Decode(&loggedUser)
	if err != nil {
		return cerr.HttpError(err, "", 500)
	}

	err = db.LoginUser(ctx, uid, loggedUser.DeviceId)
	if err != nil {
		return cerr.HttpError(err, "", 500)
	}

	res.Write([]byte(fmt.Sprintf(`{"mesg":"Successful}`)))

	return nil

}
