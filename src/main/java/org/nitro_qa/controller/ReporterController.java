package org.nitro_qa.controller;

import org.nitro_qa.dto.PdfRequestDTO;
import org.nitro_qa.service.ReporterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/pdf")
public class ReporterController {

    @Autowired
    private ReporterService reporterService;

    // 1. Iniciar la generación PDF
    @PostMapping("/generate")
    public ResponseEntity<String> generatePdf(@RequestBody PdfRequestDTO requestDTO) {
        String jobId = reporterService.submitJob(requestDTO);
        return ResponseEntity.accepted().body(jobId);
    }

    // 2. Consultar estado
    @GetMapping("/status/{jobId}")
    public ResponseEntity<String> checkStatus(@PathVariable String jobId) {
        String status = reporterService.getStatus(jobId);
        if ("NOT_FOUND".equals(status)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Job not found");
        }
        return ResponseEntity.ok(status);
    }

    // 3. Descargar el PDF generado
    @GetMapping("/{jobId}")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable String jobId) {
        try {
            byte[] file = reporterService.getPdf(jobId);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.attachment().filename("merged.pdf").build());
            return new ResponseEntity<>(file, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
