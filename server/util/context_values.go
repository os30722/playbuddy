package util

import (
	"context"

	"github.com/dgrijalva/jwt-go"
)

type userContextKey string

const UidKey = "UID"
const ClaimsKey = "claims"

func GetUserContextKey(str string) userContextKey {
	return userContextKey(str)
}

func GetUid(ctx context.Context) (int, bool) {
	claims, ok := ctx.Value(GetUserContextKey(ClaimsKey)).(jwt.MapClaims)
	if !ok {
		return -1, ok
	}

	uid := int(claims[UidKey].(float64))
	return uid, true
}
