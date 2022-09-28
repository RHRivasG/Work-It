package auth

import (
	"net/http"
	"training-service/internal/env"

	"github.com/golang-jwt/jwt"
	"github.com/labstack/echo/v4"
	"github.com/labstack/echo/v4/middleware"
)

type JwtWorkItClaims struct {
	Roles       []string
	Preferences []string
	jwt.StandardClaims
}

func AuthMiddleware() echo.MiddlewareFunc {
	config := middleware.JWTConfig{
		Claims:        &JwtWorkItClaims{},
		SigningKey:    []byte(env.GoDotEnvVariable("JWT_SECRET")),
		SigningMethod: "HS512",
	}
	return middleware.JWTWithConfig(config)
}

func AuthErrorHandler(err error, c echo.Context) {
	if err == middleware.ErrJWTMissing {
		c.Error(echo.NewHTTPError(http.StatusUnauthorized, "Login required"))
		return
	}
	c.Echo().DefaultHTTPErrorHandler(err, c)
}
