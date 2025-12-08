package com.crm.project.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Configuration
@EnableAsync
public class AsyncConfiguration implements AsyncConfigurer {

    /**
     * Định nghĩa thread pool mặc định cho tất cả @Async task
     */
    @Override
    @Bean(name = "taskExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // Cấu hình cơ bản
        executor.setCorePoolSize(5);     // số thread tối thiểu
        executor.setMaxPoolSize(10);     // tối đa khi tải cao
        executor.setQueueCapacity(50);   // hàng đợi chờ task
        executor.setThreadNamePrefix("AsyncUploader-");

        // Khi full queue → xử lý thế nào?
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // → Nếu pool đầy, task sẽ chạy trên thread hiện tại thay vì bị bỏ

        executor.initialize();
        log.info("Async Executor Initialized [core={}, max={}, queue={}]",
                executor.getCorePoolSize(), executor.getMaxPoolSize(), executor.getQueueCapacity());
        return executor;
    }

    /**
     * Xử lý ngoại lệ cho các phương thức @Async void (không trả về Future)
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) -> {
            log.error("Async error in method: {} with params: {} → {}",
                    method.getName(), params, ex.getMessage());
        };
    }
}
