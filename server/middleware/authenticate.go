package middleware

import (
	"context"
	"fmt"
	"net/http"
	"strings"

	"github.com/dgrijalva/jwt-go"
	"github.com/hepa/sports/env"
	"github.com/hepa/sports/util"
)

type errHandler func(http.ResponseWriter, *http.Request) *util.AppError

//Authenticattion Middleware
func Authenticate(endpoint func(res http.ResponseWriter, req *http.Request) *util.AppError) errHandler {
	return func(res http.ResponseWriter, req *http.Request) *util.AppError {
		if req.Header["Authorization"] != nil {
			t := strings.Split(req.Header["Authorization"][0], " ")[1]
			token, err := jwt.Parse(t, func(token *jwt.Token) (interface{}, error) {
				if _, ok := token.Method.(*jwt.SigningMethodHMAC); !ok {
					return nil, fmt.Errorf("Illegal SigningMethod")
				}

				return env.AccessKey, nil
			})

			if err != nil {
				return util.HttpError(err, "", 401)
			}

			if claims, ok := token.Claims.(jwt.MapClaims); ok && token.Valid {
				ctx := req.Context()
				ctx = context.WithValue(ctx, util.GetUserContextKey(util.ClaimsKey), claims)
				return endpoint(res, req.WithContext(ctx))
			}

		} else {
			return util.HttpError(nil, "", 401)
		}
		return nil
	}
}
