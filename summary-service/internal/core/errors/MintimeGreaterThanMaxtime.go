package errors

type MintimeGreaterThanMaxtime struct{}

func (e *MintimeGreaterThanMaxtime) Error() string {
	return "The mintime is greater than maxtime"
}
