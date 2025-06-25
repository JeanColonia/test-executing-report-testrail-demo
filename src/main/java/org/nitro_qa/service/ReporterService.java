package org.nitro_qa.service;

import org.nitro_qa.dto.PdfRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ReporterService {

    // Estado de trabajos PDF por ID
    private final ConcurrentHashMap<String, String> jobStatus = new ConcurrentHashMap<>();
    private final String outputFolder = "/tmp";

    @Autowired
    private PdfJobExecutor jobExecutor;

    public String submitJob(PdfRequestDTO requestDto) {
        String jobId = UUID.randomUUID().toString();
        jobStatus.put(jobId, "PENDING");
        jobExecutor.runPdfJob(jobId, requestDto.getUrl(), jobStatus, outputFolder);
        return jobId;
    }



    // Consulta el estado actual del job
    public String getStatus(String jobId) {
        return jobStatus.getOrDefault(jobId, "NOT_FOUND");
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
