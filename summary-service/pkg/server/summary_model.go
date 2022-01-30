package server

import (
	"time"

	"github.com/google/uuid"
)

type Summary struct {
	tableName struct{}      `pg:"summaries,alias:s"`
	ID        uuid.UUID     `pg:"id,pk"`
	Routine   uuid.UUID     `pg:"routine"`
	Mintime   time.Duration `pg:"mintime"`
	Maxtime   time.Duration `pg:"maxtime"`
}
