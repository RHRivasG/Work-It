package server

import (
	"time"

	"github.com/google/uuid"
)

type Summary struct {
	tableName struct{}      `pg:"summaries,alias:s"`
	Routine   uuid.UUID     `pg:"routine,pk"`
	Mintime   time.Duration `pg:"mintime"`
	Maxtime   time.Duration `pg:"maxtime"`
}
