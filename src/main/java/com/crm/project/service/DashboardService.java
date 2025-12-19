package com.crm.project.service;

import com.crm.project.repository.ActivityRepository;
import com.crm.project.repository.LeadRepository;
import com.crm.project.repository.OrderRepository;
import com.crm.project.repository.QuotationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.DayOfWeek;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.crm.project.dto.response.ChartResponse;
import com.crm.project.dto.response.ChartDataPoint;


@RequiredArgsConstructor
@Service
public class DashboardService {
    private final LeadRepository leadRepository;
    private final QuotationRepository quotationRepository;
    private final OrderRepository orderRepository;
    private final ActivityRepository activityRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ChartResponse getOrderRevenueChart() {
        // Calculate date ranges
        LocalDateTime now = LocalDateTime.now();

        // Current week: from Monday this week to now
        LocalDateTime currentWeekStart = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .toLocalDate().atStartOfDay();
        LocalDateTime currentWeekEnd = now;

        // Past week: Monday to Sunday of last week
        LocalDateTime pastWeekStart = currentWeekStart.minusWeeks(1);
        LocalDateTime pastWeekEnd = currentWeekStart;

        // Get revenue data
        List<Object[]> currentData = orderRepository.getRevenueByDayOfWeek(currentWeekStart, currentWeekEnd);
        List<Object[]> pastData = orderRepository.getRevenueByDayOfWeek(pastWeekStart, pastWeekEnd);

        // Get total amounts
        BigDecimal currentTotal = orderRepository.getTotalRevenueByPeriod(currentWeekStart, currentWeekEnd);
        BigDecimal pastTotal = orderRepository.getTotalRevenueByPeriod(pastWeekStart, pastWeekEnd);

        // Calculate percentage change
        double percentage = 0.0;
        String status = "stable";

        if (pastTotal.compareTo(BigDecimal.ZERO) > 0) {
            percentage = currentTotal.subtract(pastTotal)
                    .divide(pastTotal, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(100))
                    .doubleValue();

            if (percentage > 0) {
                status = "profit";
            } else if (percentage < 0) {
                status = "loss";
                percentage = Math.abs(percentage);
            }
        } else if (currentTotal.compareTo(BigDecimal.ZERO) > 0) {
            percentage = 100.0;
            status = "profit";
        }

        // Create chart data points
        List<ChartDataPoint> chartData = createChartDataPoints(currentData, pastData);

        return ChartResponse.builder()
                .name("Order Revenue")
                .currentAmount(currentTotal)
                .pastAmount(pastTotal)
                .percentage(percentage)
                .status(status)
                .chartData(chartData)
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ChartResponse getQuotationRevenueChart() {
        // Calculate date ranges
        LocalDateTime now = LocalDateTime.now();

        // Current week: from Monday this week to now
        LocalDateTime currentWeekStart = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .toLocalDate().atStartOfDay();
        LocalDateTime currentWeekEnd = now;

        // Past week: Monday to Sunday of last week
        LocalDateTime pastWeekStart = currentWeekStart.minusWeeks(1);
        LocalDateTime pastWeekEnd = currentWeekStart;

        // Get revenue data
        List<Object[]> currentData = quotationRepository.getRevenueByDayOfWeek(currentWeekStart, currentWeekEnd);
        List<Object[]> pastData = quotationRepository.getRevenueByDayOfWeek(pastWeekStart, pastWeekEnd);

        // Get total amounts
        BigDecimal currentTotal = quotationRepository.getTotalRevenueByPeriod(currentWeekStart, currentWeekEnd);
        BigDecimal pastTotal = quotationRepository.getTotalRevenueByPeriod(pastWeekStart, pastWeekEnd);

        // Calculate percentage change
        double percentage = 0.0;
        String status = "stable";

        if (pastTotal.compareTo(BigDecimal.ZERO) > 0) {
            percentage = currentTotal.subtract(pastTotal)
                    .divide(pastTotal, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(100))
                    .doubleValue();

            if (percentage > 0) {
                status = "profit";
            } else if (percentage < 0) {
                status = "loss";
                percentage = Math.abs(percentage);
            }
        } else if (currentTotal.compareTo(BigDecimal.ZERO) > 0) {
            percentage = 100.0;
            status = "profit";
        }

        // Create chart data points
        List<ChartDataPoint> chartData = createChartDataPoints(currentData, pastData);

        return ChartResponse.builder()
                .name("Quotation Revenue")
                .currentAmount(currentTotal)
                .pastAmount(pastTotal)
                .percentage(percentage)
                .status(status)
                .chartData(chartData)
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ChartResponse getTasksCompletedChart() {
        // Calculate date ranges
        LocalDateTime now = LocalDateTime.now();

        // Current week: from Monday this week to now
        LocalDateTime currentWeekStart = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .toLocalDate().atStartOfDay();
        LocalDateTime currentWeekEnd = now;

        // Past week: Monday to Sunday of last week
        LocalDateTime pastWeekStart = currentWeekStart.minusWeeks(1);
        LocalDateTime pastWeekEnd = currentWeekStart;

        // Get completed tasks data
        List<Object[]> currentData = activityRepository.getCompletedTasksByDayOfWeek(currentWeekStart, currentWeekEnd);
        List<Object[]> pastData = activityRepository.getCompletedTasksByDayOfWeek(pastWeekStart, pastWeekEnd);

        // Get total counts (BigDecimal)
        BigDecimal currentTotal = activityRepository.getTotalCompletedTasksByPeriod(currentWeekStart, currentWeekEnd);
        BigDecimal pastTotal = activityRepository.getTotalCompletedTasksByPeriod(pastWeekStart, pastWeekEnd);

        // Calculate percentage change
        double percentage = 0.0;
        String status = "stable";

        if (pastTotal.compareTo(BigDecimal.ZERO) > 0) {
            percentage = currentTotal.subtract(pastTotal)
                    .divide(pastTotal, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(100))
                    .doubleValue();

            if (percentage > 0) {
                status = "profit";
            } else if (percentage < 0) {
                status = "loss";
                percentage = Math.abs(percentage);
            }
        } else if (currentTotal.compareTo(BigDecimal.ZERO) > 0) {
            percentage = 100.0;
            status = "profit";
        }

        // Create chart data points
        List<ChartDataPoint> chartData = createChartDataPoints(currentData, pastData);

        return ChartResponse.builder()
                .name("Tasks Completed")
                .currentAmount(currentTotal)
                .pastAmount(pastTotal)
                .percentage(percentage)
                .status(status)
                .chartData(chartData)
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ChartResponse getLeadConversionChart() {
        // Calculate date ranges
        LocalDateTime now = LocalDateTime.now();

        // Current week: from Monday this week to now
        LocalDateTime currentWeekStart = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .toLocalDate().atStartOfDay();
        LocalDateTime currentWeekEnd = now;

        // Past week: Monday to Sunday of last week
        LocalDateTime pastWeekStart = currentWeekStart.minusWeeks(1);
        LocalDateTime pastWeekEnd = currentWeekStart;

        // Get converted leads data
        List<Object[]> currentData = leadRepository.getConvertedLeadsByDayOfWeek(currentWeekStart, currentWeekEnd);
        List<Object[]> pastData = leadRepository.getConvertedLeadsByDayOfWeek(pastWeekStart, pastWeekEnd);

        // Get total counts (BigDecimal)
        BigDecimal currentTotal = leadRepository.getTotalConvertedLeadsByPeriod(currentWeekStart, currentWeekEnd);
        BigDecimal pastTotal = leadRepository.getTotalConvertedLeadsByPeriod(pastWeekStart, pastWeekEnd);

        // Calculate percentage change
        double percentage = 0.0;
        String status = "stable";

        if (pastTotal.compareTo(BigDecimal.ZERO) > 0) {
            percentage = currentTotal.subtract(pastTotal)
                    .divide(pastTotal, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(100))
                    .doubleValue();

            if (percentage > 0) {
                status = "profit";
            } else if (percentage < 0) {
                status = "loss";
                percentage = Math.abs(percentage);
            }
        } else if (currentTotal.compareTo(BigDecimal.ZERO) > 0) {
            percentage = 100.0;
            status = "profit";
        }

        // Create chart data points
        List<ChartDataPoint> chartData = createChartDataPoints(currentData, pastData);

        return ChartResponse.builder()
                .name("Lead Conversion")
                .currentAmount(currentTotal)
                .pastAmount(pastTotal)
                .percentage(percentage)
                .status(status)
                .chartData(chartData)
                .build();
    }


    private List<ChartDataPoint> createChartDataPoints(List<Object[]> currentData, List<Object[]> pastData) {
        // Create map for easy lookup
        Map<String, BigDecimal> currentMap = new HashMap<>();
        Map<String, BigDecimal> pastMap = new HashMap<>();

        for (Object[] row : currentData) {
            String dayName = (String) row[0];
            BigDecimal amount = (BigDecimal) row[1];
            currentMap.put(dayName, amount);
        }

        for (Object[] row : pastData) {
            String dayName = (String) row[0];
            BigDecimal amount = (BigDecimal) row[1];
            pastMap.put(dayName, amount);
        }

        // Create data points for all days of week
        String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        String[] shortDays = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

        List<ChartDataPoint> dataPoints = new ArrayList<>();
        for (int i = 0; i < daysOfWeek.length; i++) {
            String day = daysOfWeek[i];
            String shortDay = shortDays[i];

            BigDecimal currentAmount = currentMap.getOrDefault(day, BigDecimal.ZERO);
            BigDecimal pastAmount = pastMap.getOrDefault(day, BigDecimal.ZERO);

            dataPoints.add(ChartDataPoint.builder()
                    .name(shortDay)
                    .current(currentAmount)
                    .past(pastAmount)
                    .build());
        }

        return dataPoints;
    }
}
