
# from https://github.com/hseeberger/scala-sbt/blob/master/debian/Dockerfile

ARG JDK_VERSION=11-jdk
FROM openjdk:$JDK_VERSION AS build
WORKDIR /goodnight/

ARG SBT_VERSION
ENV SBT_VERSION ${SBT_VERSION:-1.2.8}
ARG SCALA_VERSION
ENV SCALA_VERSION ${SCALA_VERSION:-2.12.8}

# Install sbt
RUN \
  curl -L -o sbt-$SBT_VERSION.deb https://dl.bintray.com/sbt/debian/sbt-$SBT_VERSION.deb && \
  dpkg -i sbt-$SBT_VERSION.deb && \
  rm sbt-$SBT_VERSION.deb && \
  apt-get update && \
  apt-get install sbt

# Install Scala
## Piping curl directly in tar
RUN \
  curl -fsL https://downloads.typesafe.com/scala/$SCALA_VERSION/scala-$SCALA_VERSION.tgz | tar xfz - -C /usr/share && \
  mv /usr/share/scala-$SCALA_VERSION /usr/share/scala && \
  chown -R root:root /usr/share/scala && \
  chmod -R 755 /usr/share/scala && \
  ln -s /usr/share/scala/bin/scala /usr/local/bin/scala

WORKDIR /goodnight/

# copy the build definition only, in order to provide a base for sbt to
# detect which plugins and libs and all that to pull.
COPY build.sbt /goodnight/
RUN mkdir /goodnight/project
COPY project/build.properties /goodnight/project/
COPY project/plugins.sbt /goodnight/project/

# this causes sbt to update itself, and is a seperate step in order
# to speed up the next, actual build step.
RUN sbt update

COPY . /goodnight/

RUN sbt dist:packageZipTarball
RUN mv server/target/universal/goodnight-server-*.tar.gz \
  /goodnight/goodnight-server.tar.gz


FROM openjdk:11-jre
WORKDIR /goodnight/
COPY --from=build /goodnight/goodnight-server.tar.gz .
RUN tar xf goodnight-server.tar.gz \
  && rm goodnight-server.tar.gz \
  && mv goodnight-server-*/* . \
  && rmdir goodnight-server-*

# valid environment variables:
# APPLICATION_SECRET=12345678
# DATABASE_URL=jdbc:postgresql://localhost:5432/goodnight?currentSchema=public
# DATABASE_USER=goodnight
# DATABASE_PASSWORD=v8zrqsV7vFgzaNEVn1a4

EXPOSE 9000
CMD ["/goodnight/bin/goodnight-server"]
