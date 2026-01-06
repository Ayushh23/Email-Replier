package com.emailreplier.EmailReplier.controller;


import com.emailreplier.EmailReplier.entity.EmailEntity;
import com.emailreplier.EmailReplier.service.emailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/public/api")
public class emailController {

    @Autowired
    private emailService emailService;

    @PostMapping("/emailReply")
    public ResponseEntity<?> emailResponse(@RequestBody EmailEntity emailEntity){
            String response=emailService.generateEmailReply(emailEntity);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping
    public String healthcheck(){
        return "Everything works fine";
    }
}
