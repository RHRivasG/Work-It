package db

import (
	"fitness-dimension/pkg/env"
	"os"

	"github.com/go-pg/pg/v10"
)

func ConnectDatabase(sigChannel chan os.Signal) (*pg.DB, error) {

	user := env.GoDotEnvVariable("DB_USER")
	password := env.GoDotEnvVariable("DB_PASSWORD")
	name := env.GoDotEnvVariable("DB_NAME")
	port := env.GoDotEnvVariable("DB_PORT")
	host := env.GoDotEnvVariable("DB_HOST")
	opt, err := pg.ParseURL("postgres://" + user + ":" + password + "@" + host + ":" + port + "/" + name)
	if err != nil {
		return nil, err
	}

	db := pg.Connect(opt)

	defer db.Close()
	go cleanUp(db, sigChannel)

	return db, nil
}

func cleanUp(db *pg.DB, sigChannel chan os.Signal) {
	select {
	case <-sigChannel:
		db.Close()
		os.Exit(0)
	}
}
