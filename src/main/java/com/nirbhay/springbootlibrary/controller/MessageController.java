package com.nirbhay.springbootlibrary.controller;


import com.nirbhay.springbootlibrary.entity.Message;
import com.nirbhay.springbootlibrary.requestmodels.AdminQuestionRequest;
import com.nirbhay.springbootlibrary.service.MessageService;
import com.nirbhay.springbootlibrary.utils.ExtractJWT;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("https://localhost:3000")
@RestController
@RequestMapping("/api/messages")
public class MessageController {
    private MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/secure/add/message")
    public void postMessage(@RequestHeader(value = "Authorization") String token,
                            @RequestBody Message messageRequest) {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        messageService.postMessage(messageRequest, userEmail);

    }

    @PutMapping("/secure/admin/message")
    public void putMessage(@RequestHeader(value="Authorization")String token,
                           @RequestBody AdminQuestionRequest adminQuestionRequest) throws Exception{
        String userEmail=ExtractJWT.payloadJWTExtraction(token,"\"sub\"");
        String admin=ExtractJWT.payloadJWTExtraction(token,"\"userType\"");
        if(admin==null || !admin.equals("admin")){
            throw new Exception("Administration page only.");
        }
        messageService.putMessage(adminQuestionRequest,userEmail);
    }
}
