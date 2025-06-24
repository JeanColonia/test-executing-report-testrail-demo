package org.nitro_qa;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.nitro_qa.dto.PdfRequestDTO;
import org.nitro_qa.service.ReporterService;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;

public class PdfServiceTest {

    public static void main(String[] args) {
        try {
            // Leer el archivo JSON con la URL
            File jsonFile = ResourceUtils.getFile("classpath:mock/data-request.json");
            String jsonContent = new String(Files.readAllBytes(jsonFile.toPath()));

            // Convertir JSON a DTO
            ObjectMapper objectMapper = new ObjectMapper();
            PdfRequestDTO dto = objectMapper.readValue(jsonContent, PdfRequestDTO.class);

            // Ejecutar generación de PDF
            ReporterService service = new ReporterService();
            byte[] pdfBytes = service.generateAndMergerPdfService(dto);

            // Guardar resultado en archivo
            File output = new File("output/merged.pdf");
            output.getParentFile().mkdirs(); // Crear directorios si no existen

            try (FileOutputStream fos = new FileOutputStream(output)) {
                fos.write(pdfBytes);
                System.out.println("PDF generado: " + output.getAbsolutePath());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
