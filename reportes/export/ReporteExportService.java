package com.dentalflow.dentalflow.reportes.export;

import com.dentalflow.dentalflow.dto.ReporteResumenResponse;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Map;

@Service
public class ReporteExportService {

    public byte[] exportarPdf(ReporteResumenResponse resumen) {
        return exportarPdf("Reporte resumen Dentalflow", resumen.toMap());
    }

    public byte[] exportarPdf(String titulo, Map<String, Object> datos) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, outputStream);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD);
            document.add(new Paragraph(titulo, titleFont));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            for (Map.Entry<String, Object> entry : datos.entrySet()) {
                agregarFila(table, entry.getKey(), String.valueOf(entry.getValue()));
            }

            document.add(table);
            document.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo exportar el reporte PDF", e);
        }
    }

    public byte[] exportarExcel(ReporteResumenResponse resumen) {
        return exportarExcel("Resumen", resumen.toMap());
    }

    public byte[] exportarExcel(String hojaNombre, Map<String, Object> datos) {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            var sheet = workbook.createSheet(hojaNombre);

            int rowIndex = 0;
            Row header = sheet.createRow(rowIndex++);
            Cell c0 = header.createCell(0);
            c0.setCellValue("Métrica");
            Cell c1 = header.createCell(1);
            c1.setCellValue("Valor");

            for (Map.Entry<String, Object> entry : datos.entrySet()) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(entry.getKey());
                row.createCell(1).setCellValue(String.valueOf(entry.getValue()));
            }

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo exportar el reporte Excel", e);
        }
    }

    private void agregarFila(PdfPTable table, String titulo, String valor) {
        PdfPCell cell1 = new PdfPCell(new Phrase(titulo));
        PdfPCell cell2 = new PdfPCell(new Phrase(valor));
        table.addCell(cell1);
        table.addCell(cell2);
    }
}

