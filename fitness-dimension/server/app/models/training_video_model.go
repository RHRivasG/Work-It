package models

type TrainingVideo struct {
	ID     string
	Name   string
	Ext    string
	Length int
	Buff   []byte
}
