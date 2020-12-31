package com.example.application.dialog.flow;


import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.gax.rpc.ApiException;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.dialogflow.v2.DetectIntentResponse;
import com.google.cloud.dialogflow.v2.QueryInput;
import com.google.cloud.dialogflow.v2.QueryResult;
import com.google.cloud.dialogflow.v2.SessionName;
import com.google.cloud.dialogflow.v2.SessionsClient;
import com.google.cloud.dialogflow.v2.SessionsSettings;
import com.google.cloud.dialogflow.v2.TextInput;
import com.google.common.collect.Lists;
import java.io.FileInputStream;
import java.io.IOException;

public class DetectIntentTexts {

    // DialogFlow API Detect Intent sample with text inputs.
    public static QueryResult detectIntentTexts(String text)
        throws IOException, ApiException {
        String languageCode = "en";
        String sessionId = "123456789";
        String projectId = "chatbot-order-perf";
        QueryResult queryResult;

        // Instantiates a client
        String jsonPath = "src/main/resources/auth.json";
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(jsonPath))
            .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));

        SessionsSettings sessionsSettings = SessionsSettings.newBuilder().setCredentialsProvider(
            FixedCredentialsProvider.create(credentials)).build();

        try (SessionsClient sessionsClient = SessionsClient.create(sessionsSettings)) {
            // Set the session name using the sessionId (UUID) and projectID (my-project-id)
            SessionName session = SessionName.of(projectId, sessionId);
            System.out.println("Session Path: " + session.toString());

            // Detect intents for each text input
                // Set the text (hello) and language code (en-US) for the query
                TextInput.Builder textInput =
                    TextInput.newBuilder().setText(text).setLanguageCode(languageCode);

                // Build the query with the TextInput
                QueryInput queryInput = QueryInput.newBuilder().setText(textInput).build();

                // Performs the detect intent request
                DetectIntentResponse response = sessionsClient.detectIntent(session, queryInput);

                // Display the query result
                queryResult = response.getQueryResult();

                System.out.println("====================");
                System.out.format("Query Text: '%s'\n", queryResult.getQueryText());
                System.out.format(
                    "Detected Intent: %s (confidence: %f)\n",
                    queryResult.getIntent().getDisplayName(), queryResult.getIntentDetectionConfidence());
                System.out.format("Fulfillment Text: '%s'\n", queryResult.getFulfillmentText());


        }
        return queryResult;
    }
}
