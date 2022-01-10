package config

import (
	"github.com/go-pg/pg/v10"
)

func ConnectDatabase() (*pg.DB, error) {

	user := GoDotEnvVariable("DB_USER")
	password := GoDotEnvVariable("DB_PASSWORD")
	name := GoDotEnvVariable("DB_NAME")
	port := GoDotEnvVariable("DB_PORT")
	host := GoDotEnvVariable("DB_HOST")
	opt, err := pg.ParseURL("postgres://" + user + ":" + password + "@" + host + ":" + port + "/" + name)
	if err != nil {
		return nil, err
	}

	db := pg.Connect(opt)

	return db, nil
}
