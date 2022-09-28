package values

import (
	"fitness-dimension/internal/core/routine/errors"
	"strings"
)

type RoutineName struct {
	value string
}

func NewRoutineName(value string) (RoutineName, error) {

	if len(strings.TrimSpace(value)) == 0 {
		return RoutineName{}, &errors.RoutineNameEmpty{}
	}

	if len(value) > 255 {
		return RoutineName{}, &errors.RoutineNameTooLong{}
	}

	return RoutineName{value: value}, nil
}

func (r *RoutineName) Value() string {
	return r.value
}
