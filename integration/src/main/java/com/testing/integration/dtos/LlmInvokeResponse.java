package com.testing.integration.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record LlmInvokeResponse(
        String sessionId,
        String type,
        String content,
        Metadata metadata
) {

    public record Metadata(
            String modelId,
            ModelKwargs modelKwargs,
            String sessionId,
            String userId,
            String workspaceId,
            List<Document> documents
    ) {
    }

    public record ModelKwargs(
            Integer maxTokens,
            Boolean streaming,
            Double temperature,
            Double topP,
            Double topK
    ) {
    }

    public record Document(
            @JsonProperty("page_content")
            String pageContent,

            DocumentMetadata metadata
    ) {
    }

    public record DocumentMetadata(
            String title,

            @JsonProperty("document_id")
            String documentId,

            @JsonProperty("chunk_id")
            String chunkId,

            Double score
    ) {
    }
}