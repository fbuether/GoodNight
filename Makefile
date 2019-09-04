

target=compile

build:
	cat /dev/null | sbt -no-colors $(target) || (echo "" && false)

run:
	cat /dev/null | sbt run || (echo "" && false)


deploy:
	cat /dev/null | sbt release || (echo "" && false)


view: build
	firefox src/main/html/index.html


clean:
	sbt clean

