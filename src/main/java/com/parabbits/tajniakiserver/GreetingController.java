package com.parabbits.tajniakiserver;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class GreetingController {

  @MessageMapping("/send/message")
  @SendTo("/chat")
  public String greeting(String message) throws Exception {
    System.out.println(message);
    return message + "!";
  }
}