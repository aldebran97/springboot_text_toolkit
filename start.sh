if [[ -f 'nohup.out' ]]
then
  rm nohup.out
fi
JAR_FILE_NAME='./text-0.0.1-SNAPSHOT.jar'
nohup java '-Xms90g' '-Xmx90g' '-Xss512m' '-Dspring.profiles.active=prod' -jar $JAR_FILE_NAME &