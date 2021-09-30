package util

type AppError struct {
	Error error
	Msg   string
	Code  int
}

func HttpError(err error, msg string, code int) *AppError {
	return &AppError{
		Error: err,
		Msg:   msg,
		Code:  code,
	}
}
