package com.superbuilt.mini.document.processing;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TextChunker {

    private static final int CHUNK_SIZE = 1500;
    private static final int OVERLAP = 200;

    public List<String> chunk(String text) {

        if (text == null || text.isBlank()) {
            return List.of();
        }

        String normalizedText = normalize(text);

        List<String> chunks = new ArrayList<>();

        int start = 0;

        while (start < normalizedText.length()) {

            int end = Math.min(
                    start + CHUNK_SIZE,
                    normalizedText.length()
            );

            String chunk = normalizedText
                    .substring(start, end)
                    .trim();

            if (!chunk.isBlank()) {
                chunks.add(chunk);
            }

            if (end == normalizedText.length()) {
                break;
            }

            start = end - OVERLAP;
        }

        return chunks;
    }

    private String normalize(String text) {

        return text
                .replaceAll("\\r\\n", "\n")
                .replaceAll("[ \\t]+", " ")
                .replaceAll("\\n{3,}", "\n\n")
                .trim();
    }
}