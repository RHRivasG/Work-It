FROM node:18

WORKDIR /usr/src/app

COPY ./core/ ./core/
COPY ./application/ ./application/

WORKDIR /usr/src/app/core
RUN npm ci
RUN npm run build

WORKDIR /usr/src/app/application
RUN npm install ../core
RUN npm ci
RUN npm run build

WORKDIR /usr/src/app/server
COPY ./server/package*.json ./

RUN npm install ../core
RUN npm install ../application
RUN npm ci

COPY ./server ./

RUN npm run build

CMD [ "node", "./server/dist/main.js" ]
