FROM nginx:1.27

WORKDIR /workspace

COPY docker/development/nginx/nginx.conf /etc/nginx/nginx.conf

EXPOSE 80

CMD [ "nginx", "-g", "daemon off;" ]
