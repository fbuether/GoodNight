

target=compile

build:
	cat /dev/null | sbt -no-colors $(target) || (echo "" && false)

run:
	cat /dev/null | sbt run || (echo "" && false)


release:
#	cat /dev/null | sbt universal:packageZipTarball || (echo "" && false)
	cat /dev/null | sbt stage || (echo "" && false)


database:
	(cd postgres && sudo docker build -t logging_postgres .)
	sudo docker run --name gnp -p 5432:5432 --restart always \
	-e POSTGRES_PASSWORD=gnpgsecretpassword -d logging_postgres
	sudo docker logs -f gnp
# sudo docker run --name gnp -p 5432:5432 --restart always -e POSTGRES_PASSWORD=gnpgsecretpassword -d postgres:11-alpine
	sudo docker run --link gnp -p 8080:8080 --name adminer -d adminer

# CREATE DATABASE goodnight;
# CREATE USER goodnight WITH ENCRYPTED PASSWORD 'v8zrqsV7vFgzaNEVn1a4';
# GRANT ALL PRIVILEGES ON DATABASE goodnight TO goodnight;

sbt:
	clear; clear; sbt -J-XX:MaxMetaspaceSize=500m


clean:
	sbt clean

