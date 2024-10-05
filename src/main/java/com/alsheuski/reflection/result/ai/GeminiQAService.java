package com.alsheuski.reflection.result.ai;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerationConfig;
import com.google.cloud.vertexai.api.HarmCategory;
import com.google.cloud.vertexai.api.SafetySetting;
import com.google.cloud.vertexai.generativeai.ChatSession;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import java.util.List;

public class GeminiQAService implements AnswerExtractor {

  @Override
  public String answer(String question) {
    var ss =
        SafetySetting.newBuilder()
            .setCategory(HarmCategory.HARM_CATEGORY_DANGEROUS_CONTENT)
            .setCategory(HarmCategory.HARM_CATEGORY_UNSPECIFIED)
            .setCategory(HarmCategory.HARM_CATEGORY_HARASSMENT)
            .setCategory(HarmCategory.HARM_CATEGORY_HATE_SPEECH)
            .setCategory(HarmCategory.HARM_CATEGORY_SEXUALLY_EXPLICIT)
            .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_NONE)
            .build();

    try (var vertexAi =
        new VertexAI.Builder()
            .setCredentials(GoogleCredentials.getApplicationDefault())
            .setLocation("us-central1")
            .setProjectId("qwiklabs-gcp-01-54d00e9dcaa8")
            .build(); ) {

      var generationConfig =
          GenerationConfig.newBuilder()
              .setMaxOutputTokens(8000)
              .setTemperature(0.4F)
              .setTopP(0.6F)
              .build();

      var model =
          new GenerativeModel("gemini-1.5-pro-002", vertexAi)
              .withGenerationConfig(generationConfig)
              .withSafetySettings(List.of(ss));
      var chatSession = new ChatSession(model);

      var response = chatSession.sendMessage(question);

      return ResponseHandler.getText(response);

    } catch (Exception e) {
      System.err.println(question);
      throw new RuntimeException(e);
    }
  }
}
