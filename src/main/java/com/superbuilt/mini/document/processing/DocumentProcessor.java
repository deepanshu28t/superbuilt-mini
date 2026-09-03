package com.superbuilt.mini.document.processing;

import com.superbuilt.mini.ai.EmbeddingService;
import com.superbuilt.mini.document.Document;
import com.superbuilt.mini.document.DocumentChunk;
import com.superbuilt.mini.document.DocumentChunkRepository;
import com.superbuilt.mini.document.DocumentStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Service
public class DocumentProcessor {

    private final PdfTextExtractor pdfTextExtractor;
    private final TextChunker textChunker;
    private final DocumentChunkRepository documentChunkRepository;
    private final DocumentStorageProperties storageProperties;
    private final EmbeddingService embeddingService;

    public DocumentProcessor(
            PdfTextExtractor pdfTextExtractor,
            TextChunker textChunker,
            DocumentChunkRepository documentChunkRepository,
            DocumentStorageProperties storageProperties,
            EmbeddingService embeddingService
    ) {
        this.pdfTextExtractor = pdfTextExtractor;
        this.textChunker = textChunker;
        this.documentChunkRepository = documentChunkRepository;
        this.storageProperties = storageProperties;
        this.embeddingService = embeddingService;
    }

    @Transactional
    public int processPdf(Document document) throws IOException {

        document.setStatus(DocumentStatus.PROCESSING);

        Path pdfPath = Path.of(
                storageProperties.getDocumentStoragePath(),
                document.getFilePath()
        ).normalize();

        // 1. Extract text from PDF
        String text = pdfTextExtractor.extractText(pdfPath);

        // 2. Split extracted text into chunks
        List<String> chunks = textChunker.chunk(text);

        // 3. Remove old chunks if document is being reprocessed
        documentChunkRepository.deleteByDocumentId(document.getId());

        // 4. Process every chunk
        for (int i = 0; i < chunks.size(); i++) {

            String chunkContent = chunks.get(i);

            // 5. Generate embedding for this chunk
            float[] embedding =
                    embeddingService.embed(chunkContent);

            // 6. Create DocumentChunk
            DocumentChunk chunk = DocumentChunk.builder()
                    .content(chunkContent)
                    .chunkIndex(i)
                    .characterCount(chunkContent.length())
                    .embedding(embedding)
                    .document(document)
                    .build();

            // 7. Save chunk + embedding
            documentChunkRepository.save(chunk);
        }

        // 8. Mark document as processed
        document.setStatus(DocumentStatus.PROCESSED);

        return chunks.size();
    }
}