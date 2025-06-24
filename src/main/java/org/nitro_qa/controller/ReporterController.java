package org.nitro_qa.controller;

import org.nitro_qa.dto.PdfRequestDTO;
import org.nitro_qa.service.ReporterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pdf")
public class ReporterController {
    @Autowired
    private ReporterService reporterService;

    @PostMapping("/generate")
    public ResponseEntity<byte[]> generatePdf(@RequestBody PdfRequestDTO requestDTO){
        byte[] mergedPdf = reporterService.generateAndMergerPdfService(requestDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=merged.pdf");
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<>(mergedPdf, headers, HttpStatus.OK);
    }
}
