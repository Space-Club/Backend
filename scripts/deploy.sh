#!/usr/bin/env bash

source /home/ubuntu/action/scripts/properties.sh

PROJECT_NAME=space-club-backend
REPOSITORY=/home/ubuntu/action
PACKAGE=$REPOSITORY/build/libs/
JAR_NAME=$(ls -tr $PACKAGE | grep 'SNAPSHOT.jar' | tail -n 1)
JAR_PATH=$PACKAGE$JAR_NAME

echo $JAR_NAME
echo $JAR_PATH

cd $REPOSITORY

CURRENT_PID=$(pgrep -f $PROJECT_NAME)

if [ -z $CURRENT_PID ]
then
  echo "> 종료할 애플리케이션이 없습니다"
else
  echo "> 실행 중인 애플리케이션 종료 $CURRENT_PID"
  kill -15 $CURRENT_PID
  sleep 5
fi

echo "> 배포 - $JAR_PATH"
chmod +x $JAR_PATH

sudo nohup java -jar $JAR_PATH --spring.profiles.active=develop --jasypt.encryptor.password=${encrypt} > /home/ubuntu/log/nohup_log.out 2> /home/ubuntu/log/nohup_error.out &
