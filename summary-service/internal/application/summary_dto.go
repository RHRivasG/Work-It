package application

type SummaryDto struct {
	Routine string `json:"routine"`
	MinTime string `json:"mintime"`
	MaxTime string `json:"maxtime"`
}
