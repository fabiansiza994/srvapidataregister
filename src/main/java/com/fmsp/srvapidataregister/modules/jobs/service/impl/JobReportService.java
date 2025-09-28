package com.fmsp.srvapidataregister.modules.jobs.service.impl;

import com.fmsp.srvapidataregister.modules.jobs.entity.Trabajo;
import com.fmsp.srvapidataregister.modules.jobs.repository.TrabajoRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class JobReportService {
    private final TrabajoRepository repo;

    public JobReportService(TrabajoRepository repo) {
        this.repo = repo;
    }

    public byte[] buildExcel(LocalDate from, LocalDate to, Long empresaId) {
        List<Trabajo> items = repo.findByFechaBetweenAndClienteEmpresaId(from, to, empresaId);

        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Sheet sh = wb.createSheet("Trabajos");

            // Estilos
            CellStyle head = wb.createCellStyle();
            Font hf = wb.createFont();
            hf.setBold(true);
            head.setFont(hf);
            head.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            head.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            head.setBorderBottom(BorderStyle.THIN);

            CellStyle money = wb.createCellStyle();
            DataFormat df = wb.createDataFormat();
            money.setDataFormat(df.getFormat("#,##0")); // ajusta formato si usas decimales

            // Header
            int r = 0;
            Row H = sh.createRow(r++);
            String[] cols = {
                    "Fecha", "Cliente/Paciente", "Tipo", "Descripción",
                    "Mano de obra", "Materiales", "Total", "Estado", "Forma de pago"
            };
            for (int i = 0; i < cols.length; i++) {
                Cell c = H.createCell(i);
                c.setCellValue(cols[i]);
                c.setCellStyle(head);
            }

            BigDecimal sumTotal = BigDecimal.ZERO;

            for (Trabajo t : items) {
                Row row = sh.createRow(r++);

                // Cliente o Paciente
                String nombre = "-";
                String tipo = "-";
                if (t.getPacienteObj() != null) {
                    String na = safe(t.getPacienteObj().getNombre());
                    String aa = safe(t.getPacienteObj().getApellido());
                    nombre = (na + " " + aa).trim();
                    if (nombre.isEmpty()) nombre = "-";
                    tipo = "PACIENTE";
                } else if (t.getCliente() != null) {
                    String nc = safe(t.getCliente().getNombre());
                    String ac = safe(t.getCliente().getApellido());
                    nombre = (nc + " " + ac).trim();
                    if (nombre.isEmpty()) nombre = "-";
                    tipo = "CLIENTE";
                }

                BigDecimal mano = safeBD(t.getValorLabor());
                BigDecimal mat = safeBD(t.getValorMateriales());
                BigDecimal tot = safeBD(t.getValorTotal());
                sumTotal = sumTotal.add(tot);

                int c = 0;
                row.createCell(c++).setCellValue(String.valueOf(t.getFecha()));
                row.createCell(c++).setCellValue(nombre);
                row.createCell(c++).setCellValue(tipo);
                row.createCell(c++).setCellValue(safe(t.getDescripcionLabor()));

                Cell cm = row.createCell(c++);
                cm.setCellValue(mano.doubleValue());
                cm.setCellStyle(money);
                Cell cmm = row.createCell(c++);
                cmm.setCellValue(mat.doubleValue());
                cmm.setCellStyle(money);
                Cell ct = row.createCell(c++);
                ct.setCellValue(tot.doubleValue());
                ct.setCellStyle(money);

                row.createCell(c++).setCellValue(safe(t.getEstado())); // PAGO/PENDIENTE/CANCELADO
                row.createCell(c++).setCellValue(
                        (t.getFormaPago() != null) ? safe(t.getFormaPago().getFormaPago()) : "-"
                );
            }

            // Fila total
            Row totalR = sh.createRow(r++);
            Cell lbl = totalR.createCell(0);
            lbl.setCellValue("TOTAL GENERAL");
            lbl.setCellStyle(head);
            sh.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(totalR.getRowNum(), totalR.getRowNum(), 0, 5));

            Cell totalCell = totalR.createCell(6);
            totalCell.setCellValue(sumTotal.doubleValue());
            totalCell.setCellStyle(money);

            for (int i = 0; i < cols.length; i++) sh.autoSizeColumn(i);

            wb.write(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("No se pudo generar el Excel", e);
        }
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }

    private static BigDecimal safeBD(java.math.BigDecimal n) {
        return n == null ? BigDecimal.ZERO : n;
    }

}
