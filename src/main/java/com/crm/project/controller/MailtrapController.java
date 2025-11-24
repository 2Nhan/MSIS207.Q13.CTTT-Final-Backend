package com.crm.project.controller;

import com.crm.project.dto.response.ApiResponse;
import com.crm.project.service.MailService;
import com.crm.project.service.MailtrapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/mailtrap")
public class MailtrapController {
    private final MailtrapService mailtrapService;
    private final MailService mailService;
//    @PostMapping()
//    public ResponseEntity<ApiResponse> sendMail() {
//        mailtrapService.sendResultEmail();
//        ApiResponse apiResponse = ApiResponse.builder().build();
//        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
//    }

    @PostMapping()
    public ResponseEntity<ApiResponse> sendMail(@RequestParam("to") String to) {
        mailService.sendTestMail(to);
        ApiResponse apiResponse = ApiResponse.builder().build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}
