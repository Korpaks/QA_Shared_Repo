package org.shared_utils;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.util.LinkedHashMap;
import java.util.List;

public class MicrosoftGraphUtils {

    static String baseURL = "https://graph.microsoft.com/v1.0";
    static String clientID = "dec2c757-add5-4d1d-b25f-6d11a1837e54";
    static String clientSecret = "91E8Q~tmkMgaIo9eP8TMFAYaJtmcsEPo-nvyEahg";
    static String tenantID = "67addedc-922e-4560-aecd-43bc42f84ed7";
    static String userID = "6c042211-ffb4-42d0-a570-ba3cd316a55b";

    /**
     * This method will obtain the access token fom Microsoft Graph so we can access emails using different API methods
     * Based on application flow using Java and Rest Assured
     * @return token
     */
    public static String microsoftGraphAccessToken() {

        Response response = RestAssured.given()
                .formParam("grant_type", "client_credentials")
                .formParam("client_id", clientID)
                .formParam("client_secret", clientSecret)
                .formParam("scope", "https://graph.microsoft.com/.default")
                .post("https://login.microsoftonline.com/" + tenantID + "/oauth2/v2.0/token");

        return response.path("access_token");
    }

    /**
     * This API method will retrieve by default first 10 emails from inbox
     * @return Linked Map with key as subject of the email and value as the content of the email
     */
    public static LinkedHashMap<String, String> readMessagesWithSubject() {

        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        Response response = RestAssured.given()
                .header("Authorization", "Bearer " + microsoftGraphAccessToken())
                .and().param("$top", 30)
                .get(baseURL + "/users/" + userID + "/messages");

        List<String> subjects = response.path("value.subject");
        List<String> content = response.path("value.body.content");

        for (int i = 0; i < subjects.size(); i++) {
            map.put(subjects.get(i), content.get(i));
        }
        return map;
    }


    /**
     * This API method will retrieve the last received email
     * @return email as a String
     */
    public static String readingFirstEmail() {

        Response response = RestAssured.given()
                .header("Authorization", "Bearer " + microsoftGraphAccessToken())
                .get(baseURL + "/users/" + userID + "/messages");

      return response.path("value.body[0].content");
    }


    /**
     * This API method will retrieve the last received email
     * @return email as a String
     */
    public static List<String> readingNumberOfEmails(int numberOfEmails) {

        Response response = RestAssured.given()
                .and().header("Authorization", "Bearer " + microsoftGraphAccessToken())
                .and().param("$top", numberOfEmails)
                .when().get(baseURL + "/users/" + userID + "/messages");

        return response.path("value.body.content");
    }
}
