FROM postgres:14-alpine

COPY ./certs/server.key /var/lib/postgresql/server.key
COPY ./certs/server.crt /var/lib/postgresql/server.crt

RUN chmod 600 /var/lib/postgresql/server.key
RUN chown postgres:postgres /var/lib/postgresql/server.key

EXPOSE 5432