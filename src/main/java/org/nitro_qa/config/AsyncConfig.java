package org.nitro_qa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {
    @Bean(name = "pdfExecutor")
    public Executor pdfExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);  // cantidad mínima de hilos
        executor.setMaxPoolSize(4);   // cantidad máxima de hilos simultáneos
        executor.setQueueCapacity(50); // cola para tareas pendientes
        executor.setThreadNamePrefix("PdfJob-");
        executor.initialize();
        return executor;
    }
}
