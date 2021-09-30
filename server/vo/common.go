package vo

type Page struct {
	TotalCount int         `json:"total_count"`
	Items      interface{} `json:"items"`
}
