package com.pdf.pdf.controller


import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayOutputStream

@RestController
@CrossOrigin("*")
class ConvertController {
    @PostMapping("/convert")
    fun convertPdfToExcel(@RequestParam("file") file: MultipartFile): ResponseEntity<ByteArray> {
        val pdfBytes = file.bytes
        val pdfDocument = PDDocument.load(pdfBytes)
        val pdfStripper = PDFTextStripper()
        val text = pdfStripper.getText(pdfDocument)
        val lines = text.split("\r\n")

        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Sheet1")
        for ((index, line) in lines.withIndex()) {
            val row = sheet.createRow(index)
            val cell = row.createCell(0)
            cell.setCellValue(line)
        }

        val outputStream = ByteArrayOutputStream()
        workbook.write(outputStream)

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
            .contentLength(outputStream.size().toLong())
            .body(outputStream.toByteArray())
    }
}

