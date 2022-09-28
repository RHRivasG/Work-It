package http

import (
	"net"
	"net/http"
	"summary-service/pkg/auth"
	"summary-service/pkg/server"

	"github.com/go-pg/pg/v10"
	"github.com/labstack/echo/v4"
	"github.com/labstack/echo/v4/middleware"
	"google.golang.org/grpc"
)

func HtttpServe(l net.Listener, database *pg.DB) error {
	conn, err := grpc.Dial(
		"localhost:8081",
		grpc.WithInsecure(),
	)
	if err != nil {
		return err
	}
	defer conn.Close()

	//Server
	e := echo.New()

	//CORS
	e.Use(middleware.CORSWithConfig(middleware.CORSConfig{
		AllowOrigins:     []string{"*"},
		AllowMethods:     []string{http.MethodGet, http.MethodHead, http.MethodPut, http.MethodPatch, http.MethodPost, http.MethodDelete, http.MethodOptions},
		AllowCredentials: true,
	}))

	//Auth
	e.HTTPErrorHandler = auth.AuthErrorHandler
	e.Use(auth.AuthMiddleware())

	//Routes
	server.HtttpSummaryServe(e, database, conn)

	s := &http.Server{Handler: e}
	return s.Serve(l)
}
