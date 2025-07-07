package com.atep.Controller;

import com.atep.Service.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
// import com.atep.dto.MessageRequest;

import com.atep.dto.ChatRequest;
import com.atep.dto.ChatResponse;
import java.io.IOException;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    
    @Autowired
    private GeminiService masageminiService;

    @PostMapping("/bot")
    
    public ChatResponse chat(@RequestBody ChatRequest request) throws IOException {
        String result = masageminiService.chat(request.getMessage());
        return new ChatResponse(result);
    }

    

    @PostMapping("/reset") 
    public String resetChat() {
        masageminiService.resetChat();
        return "Chat history has been reset.";
    }
}
