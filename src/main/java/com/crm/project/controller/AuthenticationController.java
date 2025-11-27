package com.crm.project.controller;

import com.crm.project.dto.request.LoginRequest;
import com.crm.project.dto.request.UserCreationRequest;
import com.crm.project.dto.response.MyApiResponse;
import com.crm.project.dto.response.AuthenticationResponse;
import com.crm.project.dto.response.UserResponse;
import com.crm.project.service.AuthenticationService;
import com.crm.project.service.UserService;
import com.crm.project.validator.group_sequences.ValidationSequences;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@Tag(
        name = "Authentication",
        description = "APIs for user authentication: login, registration, and logout operations."
)
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/authentication")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final UserService userService;

    @PostMapping("/login")
    @Operation(
            summary = "User Login",
            description = "Authenticate user credentials and return an access token with a refresh token."
    )
    public ResponseEntity<MyApiResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        AuthenticationResponse authenticationResponse = authenticationService.login(loginRequest);
        MyApiResponse apiResponse = MyApiResponse.builder()
                .data(authenticationResponse)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/logout")
    @Operation(
            summary = "User logout",
            description = "Invalidate the current access token and end the user's session.",
            security = {@SecurityRequirement(name = "bearerAuth")}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Logout successful",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "code": 200,
                                      "message": "Logout successful"
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthenticated – Missing or invalid token",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "code": 401,
                                      "message": "Process Failed",
                                      "error": {
                                        "code": 1005,
                                        "message": "Unauthenticated"
                                      }
                                    }
                                    """)
                    )
            )
    })
    public ResponseEntity<MyApiResponse> logout(HttpServletRequest request) throws ParseException {
        authenticationService.logout(request);
        MyApiResponse apiResponse = MyApiResponse.builder()
                .message("Logout successful")
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/registration")
    @Operation(
            summary = "Register a new user",
            description = "Create a new user account after validating input fields.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "User registration payload",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "username": "nguyenvannguyen",
                                      "password": "12345678",
                                      "firstName": "Nguyen",
                                      "lastName": "Nguyen",
                                      "email": "nguyenvannguyen@gmail.com",
                                      "phoneNumber": "0932646180",
                                      "address": "So 10, Nguyen Thai Hoc, Quy Nhon, Binh Dinh"
                                    }
                                    """)
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "code": 200,
                                      "message": "Process succeed",
                                      "data": {
                                        "id": "4a3d2f2a-4d3c-4cb3-8a3f-c82713bb4c7c",
                                        "username": "nguyenvannguyen",
                                        "firstName": "Nguyen",
                                        "lastName": "Nguyen",
                                        "email": "nguyenvannguyen@gmai.com",
                                        "phoneNumber": "0932646180",
                                        "address": "So 10, Nguyen Thai Hoc, Quy Nhon, Binh Dinh",
                                        "avatarUrl": null,
                                        "createdAt": "2025-11-26T19:50:00.9207786",
                                        "updatedAt": "2025-11-26T19:50:00.9207786"
                                      }
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Username already exists",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "code": 400,
                                      "message": "Process Failed",
                                      "error": {
                                        "code": 1001,
                                        "errorField": "username",
                                        "message": "Username exsited, please fill in another username"
                                      }
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed – missing or invalid fields",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "code": 400,
                                      "message": "Processed Failed",
                                      "error": [
                                        {
                                          "code": 3001,
                                          "errorField": "username",
                                          "message": "Username must have 8-100 characters"
                                        },
                                        {
                                          "code": 3002,
                                          "errorField": "password",
                                          "message": "Password must have at least 8 characters"
                                        },
                                        {
                                          "code": 3003,
                                          "errorField": "email",
                                          "message": "Please fill in valid email"
                                        }
                                      ]
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed – required fields missing",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "code": 400,
                                      "message": "Processed Failed",
                                      "error": [
                                        {
                                          "code": 2001,
                                          "errorField": "username",
                                          "message": "Please fill in username"
                                        },
                                        {
                                          "code": 2002,
                                          "errorField": "password",
                                          "message": "Please fill in password"
                                        },
                                        {
                                          "code": 2005,
                                          "errorField": "email",
                                          "message": "Please fill in email"
                                        }
                                      ]
                                    }
                                    """)
                    )
            )
    })
    public ResponseEntity<MyApiResponse> createUser(@RequestBody @Validated(ValidationSequences.class) UserCreationRequest request) {
        UserResponse userResponse = userService.createUser(request);
        MyApiResponse apiResponse = MyApiResponse.builder()
                .data(userResponse)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

//    @GetMapping
//    public ResponseEntity<ApiResponse> get() {
//        List<LogoutToken> logoutTokens = authenticationService.getAllBlacklistTokens();
//        ApiResponse apiResponse = ApiResponse.builder()
//                .data(logoutTokens)
//                .build();
//        return ResponseEntity.ok(apiResponse);
//    }
}
