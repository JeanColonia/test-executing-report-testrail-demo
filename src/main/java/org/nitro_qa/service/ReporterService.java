package org.nitro_qa.service;


import org.nitro_qa.dto.PdfRequestDTO;
import org.nitro_qa.util.PdfMerger;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ReporterService {
    public byte[] generateAndMergerPdfService(PdfRequestDTO requestDto){
        try {
            return PdfMerger.generateAndMergePdfs(requestDto.getUrl());
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }
}
