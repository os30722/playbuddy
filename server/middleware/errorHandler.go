package middleware

import (
	"fmt"
	"io"
	"io/ioutil"
	"net/http"

	"github.com/hepa/sports/util"
)

var errStr = []byte(`Internal Server Error`)

type ErrHandler func(http.ResponseWriter, *http.Request) *util.AppError

func (fn ErrHandler) ServeHTTP(res http.ResponseWriter, req *http.Request) {

	if err := fn(res, req); err != nil {
		switch err.Code {
		case 500:
			fmt.Println(err)
			res.WriteHeader(500)
			res.Write(errStr)
			break

		case 400:
			fmt.Println(err)
			res.WriteHeader(400)
			res.Write([]byte(`{"err":"Bad Request"}`))
			break

		case 401:
			fmt.Println(err)
			io.Copy(ioutil.Discard, req.Body)
			res.WriteHeader(401)
			res.Write([]byte(`{"msg":"Unauthorized"}`))
			defer req.Body.Close()
			break

		case 409:
			res.WriteHeader(409)
			res.Write([]byte(`{"err":"Duplicate"}`))
			break
		}

	}
}
