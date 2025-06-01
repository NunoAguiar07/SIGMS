FROM nginx:alpine
LABEL authors="toms223"

# Remove default nginx static content and config
RUN rm -rf /usr/share/nginx/html/* /etc/nginx/conf.d/default.conf

# Copy built SPA files
WORKDIR /usr/share/nginx/html
COPY dist/ .

# Copy custom nginx config
COPY ./docker/nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]