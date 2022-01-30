package db

import (
	"os"

	"github.com/go-pg/pg/v10"
)

func ConnectDatabase() (*pg.DB, error) {
	user := os.Getenv("DB_USER")
	password := os.Getenv("DB_PASSWORD")
	name := os.Getenv("DB_NAME")
	port := os.Getenv("DB_PORT")
	host := os.Getenv("DB_HOST")
	opt, err := pg.ParseURL("postgres://" + user + ":" + password + "@" + host + ":" + port + "/" + name + "?sslmode=disable")
	if err != nil {
		return nil, err
	}

	db := pg.Connect(opt)

	return db, nil
}
