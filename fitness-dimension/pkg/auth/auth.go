package auth

import (
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
		SigningKey:    []byte("CDulchjJLbzSGsePItkZUiyTYrMXdAawQmKpxVRnOEqNfWvFBgoHmvgrePCNyBfb"),
		SigningMethod: "HS512",
	}
	return middleware.JWTWithConfig(config)
}
