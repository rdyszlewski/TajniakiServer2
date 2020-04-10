package com.parabbits.tajniakiserver;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

@Controller
public class GreetingController {


  @MessageMapping("/hello")
  @SendTo("/topic/greetings")
  public Greeting greeting(HelloMessage message) throws Exception {
    Thread.sleep(1000); // simulated delay
    String messageText = HtmlUtils.htmlEscape(message.getName());
    System.out.println(messageText);
    return new Greeting("Hello, " + messageText + "!");
  }

}