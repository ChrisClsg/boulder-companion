# ---------- Dependencies stage ----------
FROM node:22.22.2-alpine AS deps

WORKDIR /app

COPY package*.json ./

RUN npm ci --ignore-scripts


# ---------- Build base stage ----------
FROM deps AS build

WORKDIR /app

COPY . .

RUN npx quasar prepare


# ---------- Dev stage ----------
FROM build AS dev

WORKDIR /app

EXPOSE 5173

CMD ["npx", "quasar", "dev", "--host", "0.0.0.0", "--port", "5173"]


# ---------- Production build stage ----------
FROM build AS prod-build

WORKDIR /app

RUN npx quasar build


# ---------- Production runtime stage ----------
FROM nginx:alpine AS prod

COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=prod-build /app/dist/spa /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
