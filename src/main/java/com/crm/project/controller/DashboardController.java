package com.crm.project.controller;

import com.crm.project.dto.response.ChartResponse;
import com.crm.project.dto.response.MyApiResponse;
import com.crm.project.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/sale-reports")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/charts/orders")
    public ResponseEntity<MyApiResponse> getOrderRevenueChart() {
        ChartResponse chartResponse = dashboardService.getOrderRevenueChart();
        MyApiResponse apiResponse = MyApiResponse.builder()
                .data(chartResponse)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/charts/quotations")
    public ResponseEntity<MyApiResponse> getQuotationRevenueChart() {
        ChartResponse chartResponse = dashboardService.getQuotationRevenueChart();
        MyApiResponse apiResponse = MyApiResponse.builder()
                .data(chartResponse)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/charts/tasks-completed")
    public ResponseEntity<MyApiResponse> getTasksCompletedChart() {
        ChartResponse chartResponse = dashboardService.getTasksCompletedChart();
        MyApiResponse apiResponse = MyApiResponse.builder()
                .data(chartResponse)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/charts/lead-conversion")
    public ResponseEntity<MyApiResponse> getLeadConversionChart() {
        ChartResponse chartResponse = dashboardService.getLeadConversionChart();
        MyApiResponse apiResponse = MyApiResponse.builder()
                .data(chartResponse)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}
