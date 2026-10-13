package com.testing.integration.dtos;

import java.util.List;

public record LlmInvokeRequest(
        String action,
        String modelInterface,
        LlmData data
) {

    public record LlmData(
            String mode,
            String text,
            List<String> files,
            String modelName,
            String provider,
            String systemPrompt,
            String sessionId,
            String workspaceId,
            ModelKwargs modelKwargs
    ) {
    }

    public record ModelKwargs(
            int maxTokens,
            double temperature,
            boolean streaming,
            double topP
    ) {
    }
}