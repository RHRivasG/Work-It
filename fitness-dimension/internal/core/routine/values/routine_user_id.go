package values

import (
	"fitness-dimension/internal/core/routine/errors"
	"strings"
)

type RoutineUserID struct {
	value string
}

func NewRoutineUserID(value string) (RoutineUserID, error) {

	if len(strings.TrimSpace(value)) == 0 {
		return RoutineUserID{}, &errors.RoutineUserIdEmpty{}
	}

	return RoutineUserID{value: value}, nil
}

func (r *RoutineUserID) Value() string {
	return r.value
}
