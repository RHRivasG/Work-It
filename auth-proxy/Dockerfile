FROM rust:1.57-buster

COPY ./src /auth-proxy/src
COPY ./Cargo.toml /auth-proxy/Cargo.toml
COPY ./server_config.production.toml /auth-proxy/src/server_config.toml

WORKDIR /auth-proxy/src

RUN apt install -y libssl-dev
RUN cargo build
RUN cargo install --path ../
CMD ["auth-proxy"]