package config

import (
	"github.com/go-pg/pg/v10"
)

func ConnectDatabase() (*pg.DB, error) {

	opt, err := pg.ParseURL("postgres://postgres:postgres@localhost:5432/WorkItDB")
	if err != nil {
		return nil, err
	}

	db := pg.Connect(opt)

	return db, nil
}
