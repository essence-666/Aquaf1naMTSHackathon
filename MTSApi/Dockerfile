FROM ubuntu:22.04

WORKDIR /app

COPY requirements.txt .

ARG DEBIAN_FRONTEND=noninteractive

RUN apt update -y \ 
    && apt install pip ffmpeg apturl python3-brlapi -y \
    && python3 -m pip install --upgrade pip \
    && pip install -r requirements.txt

COPY . .

EXPOSE 5000

CMD ["python3", "main.py"]