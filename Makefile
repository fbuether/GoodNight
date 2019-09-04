

target=compile

build:
	cat /dev/null | sbt -no-colors $(target) || (echo "" && false)

run:
	cat /dev/null | sbt run || (echo "" && false)


release:
	cat /dev/null | sbt release || (echo "" && false)


database:
	sudo docker run --name goodnight-postgres -p 5432:5432 \
	-e POSTGRES_PASSWORD=gnpgsecretpassword -d postgres:alpine


clean:
	sbt clean

