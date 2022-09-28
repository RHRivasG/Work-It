FROM node:18


WORKDIR /usr/src/app/

COPY ./package*.json ./

RUN npm ci

COPY ./ ./

RUN mv ./.env.production ./.env

RUN npm run build

CMD [ "node", "./dist/main.js" ]
