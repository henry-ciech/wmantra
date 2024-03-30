FROM node:16-alpine

RUN apk add --no-cache \
      chromium \
      nss \
      freetype \
      harfbuzz \
      ca-certificates \
      ttf-freefont

ENV PUPPETEER_SKIP_CHROMIUM_DOWNLOAD=true \
    PUPPETEER_EXECUTABLE_PATH=/usr/bin/chromium-browser

WORKDIR /app

RUN mkdir /app/logs

COPY .env .
COPY screenshotter/ .

RUN npm install

CMD ["sh", "-c", "node screenshotter.mjs >> /app/logs/application.log 2>&1"]
