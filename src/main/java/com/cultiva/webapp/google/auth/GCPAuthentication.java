package com.cultiva.webapp.google.auth;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.IdTokenCredentials;
import com.google.auth.oauth2.IdTokenProvider;
import com.google.auth.oauth2.IdTokenProvider.Option;
import java.io.IOException;
import java.util.Arrays;

public class GCPAuthentication {
  public static String getIdTokenFromMetadataServer(String url) throws IOException {
    // Construct the GoogleCredentials object which obtains the default
    // configuration from your
    // working environment.
    GoogleCredentials googleCredentials = GoogleCredentials.getApplicationDefault();

    IdTokenCredentials idTokenCredentials = IdTokenCredentials.newBuilder()
        .setIdTokenProvider((IdTokenProvider) googleCredentials)
        .setTargetAudience(url)
        // Setting the ID token options.
        .setOptions(Arrays.asList(Option.FORMAT_FULL, Option.LICENSES_TRUE))
        .build();

    // Get the ID token.
    // Once you've obtained the ID token, you can use it to make an authenticated
    // call to the
    // target audience.
    String idToken = idTokenCredentials.refreshAccessToken().getTokenValue();
    return idToken;
  }
}
