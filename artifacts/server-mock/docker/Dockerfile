FROM node:24-slim

WORKDIR /app

COPY artifacts/server-mock/package.json ./

RUN npm install

COPY artifacts/server-mock/ .

EXPOSE 3000

CMD ["npm", "run", "dev"]
