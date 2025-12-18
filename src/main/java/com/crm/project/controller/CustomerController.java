package com.crm.project.controller;

import com.crm.project.dto.response.CustomerResponse;
import com.crm.project.dto.response.MyApiResponse;
import com.crm.project.internal.PageInfo;
import com.crm.project.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {
    private final CustomerService customerService;

    @GetMapping("/{id}")
    public ResponseEntity<MyApiResponse> getCustomerDetails(@PathVariable String id) {
        CustomerResponse response = customerService.getCustomerDetails(id);
        MyApiResponse myApiResponse = MyApiResponse.builder()
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(myApiResponse);
    }

    @GetMapping
    public ResponseEntity<MyApiResponse> getAllCustomers(@RequestParam(name = "pageNo", required = false, defaultValue = "1") int pageNumber,
                                                         @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize,
                                                         @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
                                                         @RequestParam(required = false, defaultValue = "desc") String sortOrder) {
        Page<CustomerResponse> customersList = customerService.getAllCustomers(pageNumber, pageSize, sortBy, sortOrder);

        MyApiResponse apiResponse = MyApiResponse.builder()
                .data(customersList.getContent())
                .pagination(new PageInfo<>(customersList))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MyApiResponse> deleteCustomer(@PathVariable String id) {
        customerService.deleteCustomer(id);
        MyApiResponse apiResponse = MyApiResponse.builder()
                .message("Customer deleted")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/search")
    public ResponseEntity<MyApiResponse> searchCustomers(@RequestParam("query") String query,
                                                         @RequestParam(name = "pageNo", required = false, defaultValue = "1") int pageNumber,
                                                         @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize) {
        Page<CustomerResponse> customersList = customerService.searchCustomers(query, pageNumber, pageSize);
        MyApiResponse apiResponse = MyApiResponse.builder()
                .data(customersList.getContent())
                .pagination(new PageInfo<>(customersList))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}
