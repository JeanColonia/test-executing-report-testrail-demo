package org.nitro_qa.service;

import org.nitro_qa.util.PdfMerger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class PdfJobExecutor {

    @Async("pdfExecutor")
    public void runPdfJob(String jobId, String url, ConcurrentHashMap<String, String> jobStatus, String outputFolder) {
        try {
            byte[] result = PdfMerger.generateAndMergePdfs(url);
            try (FileOutputStream fos = new FileOutputStream(outputFolder + File.separator + jobId + ".pdf")) {
                fos.write(result);
            }
            jobStatus.put(jobId, "READY");
        } catch (Exception e) {
            jobStatus.put(jobId, "FAILED");
            e.printStackTrace();
        }
    }
}
