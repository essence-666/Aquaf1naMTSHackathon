Важно!!!
АПК андроид приложение подключается к питон Серверу через доменное имя, если будете собирать у себя на сервере API:
1. убедитесь что IP уникален и публичен и что есть права на прослушку своего IP адреса (на 5000 порту)
2. напишите в телеграм @againlose привяжу айпишник к домену

otherwise можете потестить через приложение, сервер указанный в АПК останется включенным до 23:59 16 Февраля и будет дальше принимать запросы.

Для сборки python-API части:

Через докер!!
- docker run essence666/hackathon_api:mvp2
or 
- git clone <link to gitlab>
- cd <project-name>/API && docker build -t <image-name> .
- docker run --rm --name api -d -p 5000:5000 <image-name>


напрямую ручками >
- pip install -r requirements.txt && python3 main.py
