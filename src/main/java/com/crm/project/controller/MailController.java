package com.crm.project.controller;

import com.crm.project.dto.response.ApiResponse;
import com.crm.project.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/mail")
public class MailController {
    private final MailService mailService;


    @PostMapping()
    public ResponseEntity<ApiResponse> sendMail(@RequestParam("to") String to) {
        mailService.sendMail(to);
        ApiResponse apiResponse = ApiResponse.builder().build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}
