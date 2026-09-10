package com.superbuilt.mini;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.IOException;

public class TestPdfGenerator {

    public static void main(String[] args) throws IOException {

        createElectricalPdf();
        createFireSafetyPdf();

        System.out.println("PDFs created successfully!");
    }

    private static void createElectricalPdf() throws IOException {

        try (PDDocument document = new PDDocument()) {

            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream =
                         new PDPageContentStream(document, page)) {

                contentStream.setFont(
                        new PDType1Font(Standard14Fonts.FontName.HELVETICA),
                        12
                );

                contentStream.beginText();
                contentStream.newLineAtOffset(50, 750);

                String[] lines = {
                        "PROJECT: TOWER A",
                        "DRAWING: E-401",
                        "DISCIPLINE: ELECTRICAL",
                        "LEVEL: 8",
                        "",
                        "ELECTRICAL COORDINATION NOTE",
                        "The main electrical cable tray is planned",
                        "to pass through the Level 8 fire safety shaft.",
                        "The proposed routing must be reviewed before installation."
                };

                for (String line : lines) {
                    contentStream.showText(line);
                    contentStream.newLineAtOffset(0, -20);
                }

                contentStream.endText();
            }

            document.save(
                    "sample-data/drawings/E-401.pdf"
            );
        }
    }

    private static void createFireSafetyPdf() throws IOException {

        try (PDDocument document = new PDDocument()) {

            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream =
                         new PDPageContentStream(document, page)) {

                contentStream.setFont(
                        new PDType1Font(Standard14Fonts.FontName.HELVETICA),
                        12
                );

                contentStream.beginText();
                contentStream.newLineAtOffset(50, 750);

                String[] lines = {
                        "PROJECT: TOWER A",
                        "DRAWING: F-302",
                        "DISCIPLINE: FIRE SAFETY",
                        "LEVEL: 8",
                        "",
                        "FIRE SAFETY REQUIREMENT",
                        "The Level 8 fire safety shaft must remain clear.",
                        "Electrical cable trays must not pass through",
                        "the designated fire safety shaft."
                };

                for (String line : lines) {
                    contentStream.showText(line);
                    contentStream.newLineAtOffset(0, -20);
                }

                contentStream.endText();
            }

            document.save(
                    "sample-data/drawings/F-302.pdf"
            );
        }
    }
}