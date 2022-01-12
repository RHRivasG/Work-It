FROM node:17-alpine AS build

COPY ./ /workit-client/
WORKDIR /workit-client/
RUN npm i
RUN npm run build

FROM httpd:2.4.52-alpine
COPY --from=build /workit-client/dist/client/ /usr/local/apache2/htdocs
COPY certs/server.crt /usr/local/apache2/conf
COPY certs/server.key /usr/local/apache2/conf
COPY .htaccess /usr/local/apache2/htdocs/.htaccess
COPY server.conf /usr/local/apache2/conf/httpd.conf
