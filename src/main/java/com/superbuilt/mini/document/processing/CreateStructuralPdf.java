package com.superbuilt.mini.document.processing;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.File;

public class CreateStructuralPdf {

    public static void main(String[] args) throws Exception {

        String outputPath = "sample-data/drawings/S-208.pdf";
        File outputFile = new File(outputPath);
        outputFile.getParentFile().mkdirs();

        try (PDDocument document = new PDDocument()) {

            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream =
                         new PDPageContentStream(document, page)) {

                PDType1Font font =
                        new PDType1Font(
                                Standard14Fonts.FontName.HELVETICA
                        );

                contentStream.beginText();

                contentStream.setFont(font, 12);
                contentStream.setLeading(16);
                contentStream.newLineAtOffset(50, 750);

                contentStream.showText("PROJECT: TOWER A");
                contentStream.newLine();

                contentStream.showText("DRAWING: S-208");
                contentStream.newLine();

                contentStream.showText("DISCIPLINE: STRUCTURAL");
                contentStream.newLine();

                contentStream.showText("LEVEL: 8");
                contentStream.newLine();

                contentStream.newLine();

                contentStream.showText(
                        "STRUCTURAL COORDINATION NOTE"
                );
                contentStream.newLine();

                contentStream.newLine();

                contentStream.showText(
                        "Beam B-18 is located across the Level 8 ceiling zone."
                );
                contentStream.newLine();

                contentStream.showText(
                        "No mechanical ductwork should pass through the"
                );
                contentStream.newLine();

                contentStream.showText(
                        "structural beam zone without structural approval."
                );
                contentStream.newLine();

                contentStream.showText(
                        "Any modification to beam B-18 requires review by"
                );
                contentStream.newLine();

                contentStream.showText(
                        "the structural consultant."
                );
                contentStream.newLine();

                contentStream.endText();
            }

            document.save(outputFile);

            System.out.println(
                    "Created PDF: " +
                            outputFile.getAbsolutePath()
            );
        }
    }
}