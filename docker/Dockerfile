FROM openjdk:11-jre
LABEL author="fbuether@jasminefields.net"

WORKDIR /goodnight/
COPY ./goodnight-server*.zip goodnight-server.zip
RUN unzip -d . goodnight-server.zip && \
    rm goodnight-server.zip && \
    mv ./*/* . && \
    rmdir goodnight-server*

# valid environment variables:
# APPLICATION_SECRET=12345678
# DATABASE_URL=jdbc:postgresql://localhost:5432/goodnight?currentSchema=public
# DATABASE_USER=goodnight
# DATABASE_PASSWORD=v8zrqsV7vFgzaNEVn1a4

EXPOSE 9000
CMD ["/goodnight/bin/goodnight-server"]
