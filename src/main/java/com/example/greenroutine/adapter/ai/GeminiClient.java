package com.example.greenroutine.adapter.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@ConditionalOnBean(WebClient.class)
public class GeminiClient {
    private final WebClient geminiWebClient;
    private final ObjectMapper om = new ObjectMapper();

    @Value("${gemini.api-key:}")
    private String apiKey;

    @Value("${gemini.model:gemini-1.5-flash}")
    private String model;

    public Map<String, List<String>> getTips(String prompt) {
        if (apiKey == null || apiKey.isBlank()) return null;

        String url = "https://generativelanguage.googleapis.com/v1beta/models/"
                + model + ":generateContent?key=" + apiKey;

        Map<String, Object> body = Map.of(
                "contents", List.of(Map.of("parts", List.of(Map.of("text", prompt)))) ,
                "generationConfig", Map.of("response_mime_type", "application/json")
        );

        try {
            String raw = geminiWebClient.post()
                    .uri(url)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (raw == null) return null;

            JsonNode root = om.readTree(raw);
            JsonNode textNode = root.path("candidates").path(0)
                    .path("content").path("parts").path(0).path("text");
            if (!textNode.isTextual()) return null;

            JsonNode json = om.readTree(textNode.asText());
            List<String> elec = toList(json.get("ELEC"));
            List<String> gas  = toList(json.get("GAS"));

            Map<String, List<String>> out = new LinkedHashMap<>();
            out.put("ELEC", elec == null ? List.of() : elec);
            out.put("GAS",  gas  == null ? List.of() : gas);
            return out;
        } catch (Exception e) {
            return null; // 실패 시 폴백 용
        }
    }

    private List<String> toList(JsonNode n) {
        if (n == null || !n.isArray()) return null;
        List<String> r = new ArrayList<>();
        n.forEach(x -> { if (x.isTextual()) r.add(x.asText()); });
        return r;
    }
}
