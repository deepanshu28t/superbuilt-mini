package com.superbuilt.mini.document.processing;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.File;

public class CreateSamplePdf {

    public static void main(String[] args) throws Exception {

        String outputPath = "sample-data/drawings/M-812.pdf";
        File outputFile = new File(outputPath);

        // Make sure the parent directory exists
        outputFile.getParentFile().mkdirs();

        try (PDDocument document = new PDDocument()) {

            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream =
                         new PDPageContentStream(document, page)) {

                PDType1Font font =
                        new PDType1Font(Standard14Fonts.FontName.HELVETICA);

                contentStream.beginText();

                contentStream.setFont(font, 12);

                contentStream.setLeading(16);

                contentStream.newLineAtOffset(50, 750);

                contentStream.showText("PROJECT: TOWER A");
                contentStream.newLine();

                contentStream.showText("DRAWING: M-812");
                contentStream.newLine();

                contentStream.showText("DISCIPLINE: MECHANICAL / HVAC");
                contentStream.newLine();

                contentStream.showText("LEVEL: 8");
                contentStream.newLine();

                contentStream.newLine();

                contentStream.showText("HVAC COORDINATION NOTE");
                contentStream.newLine();

                contentStream.newLine();

                contentStream.showText(
                        "The proposed HVAC duct route runs through the"
                );
                contentStream.newLine();

                contentStream.showText(
                        "Level 8 ceiling zone."
                );
                contentStream.newLine();

                contentStream.showText(
                        "The duct route must be coordinated with structural"
                );
                contentStream.newLine();

                contentStream.showText(
                        "beam B-18 before installation."
                );
                contentStream.newLine();

                contentStream.showText(
                        "The mechanical consultant must confirm the final"
                );
                contentStream.newLine();

                contentStream.showText(
                        "routing before site installation begins."
                );
                contentStream.newLine();

                contentStream.endText();
            }

            document.save(outputFile);

            System.out.println(
                    "Created PDF: " + outputFile.getAbsolutePath()
            );
        }
    }
}