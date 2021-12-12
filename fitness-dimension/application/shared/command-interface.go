package shared

import "fmt"

func handle(i interface{}) {
	switch t := i.(type) {
	case int64:
		// if type is an integer
		fmt.Println("Type is an integer:", t)
	case float64:
		// if type is a floating point number
		fmt.Println("Type is a float:", t)
	case string:
		// if type is a string
		fmt.Println("Type is a string:", t)
	case nil:
		// if type is nil (zero-value)
		fmt.Println("Type is nil.")
	case bool:
		// if type is a boolean
		fmt.Println("Type is a bool:", t)
	default:
		// if type is other than above
		fmt.Println("Type is unknown!")
	}
}
