1. r6-database 레포지토리에서 버전에 맞는 sql문을 데이터 베이스에서 수행한다.
2. build.xml에서 full-build(-local).xml을 빌드한다.
3. 톰캣을 다운로드 받은 후 conf>catailna>localhost폴더를 만들고 r6.xml파일 생성 후 example-tomcat-file레포지토리처럼 환경에 맞게 db 정보 및 docbase등을 고친다.
4. 톰캣의 bin폴더아래에 catalina.bat을 example-tomcat-file레포지토리의 catalina.bat처럼 환경에 맞게 고친다
   
