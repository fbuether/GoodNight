

target=compile

build:
	cat /dev/null | sbt -no-colors $(target) || (echo "" && false)

run:
	cat /dev/null | sbt run || (echo "" && false)

release:
	cat /dev/null | sbt dist || (echo "" && false)
	cp server/target/universal/goodnight-server-*.zip docker/goodnight-server.zip
	(cd docker && \
		sudo docker build --compress -t goodnight:latest .)

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


docker:
	docker build -t goodnight .
	docker tag goodnight docker.jasminefields.net/goodnight
	docker login -u registryuser -p kTxvorKOpiydQ1pCZ9Lt \
	  https://docker.jasminefields.net/
	docker push docker.jasminefields.net/goodnight


.PHONY: docker
