package com.dentalflow.dentalflow.reportes;

import com.dentalflow.dentalflow.dto.ReportePeriodoResponse;
import com.dentalflow.dentalflow.dto.ReporteResumenResponse;
import com.dentalflow.dentalflow.reportes.export.ReporteExportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/reportes")
public class ReporteController {

    private final ReporteService service;
    private final ReporteExportService exportService;

    public ReporteController(ReporteService service, ReporteExportService exportService) {
        this.service = service;
        this.exportService = exportService;
    }

    @GetMapping("/resumen")
    public ReporteResumenResponse resumen() {
        return service.resumen();
    }

    @GetMapping("/rango")
    public ReportePeriodoResponse resumenPorRango(@RequestParam LocalDate desde, @RequestParam LocalDate hasta) {
        return service.resumenPeriodo(desde, hasta);
    }

    @GetMapping(value = "/resumen/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> resumenPdf() {
        byte[] contenido = exportService.exportarPdf(service.resumen());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte-resumen.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(contenido);
    }

    @GetMapping(value = "/resumen/excel", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> resumenExcel() {
        byte[] contenido = exportService.exportarExcel(service.resumen());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte-resumen.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(contenido);
    }

    @GetMapping(value = "/rango/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> resumenPorRangoPdf(@RequestParam LocalDate desde, @RequestParam LocalDate hasta) {
        ReportePeriodoResponse reporte = service.resumenPeriodo(desde, hasta);
        byte[] contenido = exportService.exportarPdf("Reporte por rango Dentalflow", reporte.toMap());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte-rango.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(contenido);
    }

    @GetMapping(value = "/rango/excel", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> resumenPorRangoExcel(@RequestParam LocalDate desde, @RequestParam LocalDate hasta) {
        ReportePeriodoResponse reporte = service.resumenPeriodo(desde, hasta);
        byte[] contenido = exportService.exportarExcel("Reporte rango", reporte.toMap());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte-rango.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(contenido);
    }
}


