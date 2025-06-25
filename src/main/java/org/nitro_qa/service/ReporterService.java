package org.nitro_qa.service;

import org.nitro_qa.dto.PdfRequestDTO;
import org.nitro_qa.util.PdfMerger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ReporterService {

    // Estado de trabajos PDF por ID
    private final ConcurrentHashMap<String, String> jobStatus = new ConcurrentHashMap<>();
    private final String outputFolder = "/tmp";

    @Autowired
    private ApplicationContext context;

    public String submitJob(PdfRequestDTO requestDto) {
        String jobId = UUID.randomUUID().toString();
        jobStatus.put(jobId, "PENDING");
        context.getBean(ReporterService.class).processInBackground(jobId, requestDto.getUrl());
        return jobId;
    }


    @Async("pdfExecutor")
    public void processInBackground(String jobId, String url) {
        try {
            byte[] result = PdfMerger.generateAndMergePdfs(url);
            try (FileOutputStream fos = new FileOutputStream(getPdfPath(jobId))) {
                fos.write(result);
            }
            jobStatus.put(jobId, "READY");
        } catch (Exception e) {
            jobStatus.put(jobId, "FAILED");
            e.printStackTrace();
        }
    }

    // Consulta el estado actual del job
    public String getStatus(String jobId) {
        return jobStatus.getOrDefault(jobId, "PROCESSING");
    }

    // Devuelve el contenido binario del PDF generado
    public byte[] getPdf(String jobId) throws IOException {
        File file = new File(getPdfPath(jobId));
        if (!file.exists()) {
            throw new IOException("PDF not ready yet.");
        }
        return java.nio.file.Files.readAllBytes(file.toPath());
    }


    // Obtiene la ruta completa del archivo PDF
    private String getPdfPath(String jobId) {
        return outputFolder + File.separator + jobId + ".pdf";
    }
}
