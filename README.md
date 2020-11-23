#Tajniaki Server

 Server for game Tajniaki, virtual version of board game Codenames.
 Client: https://github.com/Parabbits/Tajniaki
 
 The server has been made with the use of Spring Boot. The communication with clients is done using WebSocket.
 
 ## Usage
 
 ### Maven
 ```bash
mvn spring-boot: run
```

### Docket

```bash
sudo docker build -t tajniaki .
sudo docker run -p 8080-8080 tajniaki
```