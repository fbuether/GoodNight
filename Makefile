

target=compile

build:
	cat /dev/null | sbt -no-colors $(target) || (echo "" && false)

run:
	cat /dev/null | sbt run || (echo "" && false)


release:
	cat /dev/null | sbt release || (echo "" && false)



clean:
	sbt clean

