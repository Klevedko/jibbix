package com.emitter;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class PostAddParticipant {
    public static final Logger logger = LoggerFactory.getLogger(PostAddParticipant.class);
    public String regioncomRequestIssueNumber;
    public String jira_issue_id;
    private JdbcTemplate jdbcTemplate;

    public PostAddParticipant(JdbcTemplate jdbcTemplate, String jira_issue_id) {
        this.jdbcTemplate = jdbcTemplate;
        this.jira_issue_id = jira_issue_id;
    }

    public void main(String parsedIssueNumber) throws RuntimeException {
        logger.info("Adding participant to new Servicedesk issue ....");
        regioncomRequestIssueNumber = parsedIssueNumber;
        logger.info("issueKey = {}", regioncomRequestIssueNumber);
        String restUrl = WebApp.properties.getRegioncomJiraUri().concat("/").concat(regioncomRequestIssueNumber).concat("/participant");
        String username = WebApp.properties.getRegioncomJiraUser();
        String password = WebApp.properties.getRegioncomJiraPassword();
        String jsonData = "{\"usernames\": [\"dhazeeva@domain.ru\"]}\n";
        HttpPost httpPost = this.createConnectivity(restUrl, username, password);
        this.executeReq(jsonData, httpPost);
    }

    public HttpPost createConnectivity(String restUrl, String username, String password) throws RuntimeException {
        try {
            HttpPost post = new HttpPost(restUrl);
            String auth = new StringBuffer(username).append(":").append(password).toString();
            byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
            String authHeader = "Basic " + new String(encodedAuth);
            post.setHeader("AUTHORIZATION", authHeader);
            post.setHeader("Content-Type", "application/json; charset=UTF-8");
            post.setHeader("Accept", "application/json");
            post.setHeader("X-Stream", "true");
            return post;
        } catch (RuntimeException r) {
            throw new RuntimeException(r);
        }
    }

    public void executeReq(String jsonData, HttpPost httpPost) throws RuntimeException {
        try {
            executeHttpRequest(jsonData, httpPost);
        } catch (UnsupportedEncodingException e) {
            logger.error("error while encoding api url : {}", e);
        } catch (RuntimeException r) {
            throw new RuntimeException(r);
        } catch (IOException e) {
            logger.error("ioException occured while sending http request : {}", e);
        } finally {
            httpPost.releaseConnection();
        }
    }

    public void executeHttpRequest(String jsonData, HttpPost httpPost) throws RuntimeException, IOException {
        HttpResponse response = null;
        String responseLine = "";
        StringBuffer result = new StringBuffer();
        httpPost.setEntity(new StringEntity(jsonData, "UTF-8"));
        HttpClient client = HttpClientBuilder.create().build();
        response = client.execute(httpPost);
        logger.info("Post parameters : {}", jsonData);
        logger.info("Sending POST...");
        logger.info("Response Code : {}", response.getStatusLine().getStatusCode());
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        while ((responseLine = reader.readLine()) != null) {
            result.append(responseLine);
            result.append("\n");
        }
        logger.info("Response body: {}", result.toString());
        if (response.getStatusLine().getStatusCode() != 201
                && response.getStatusLine().getStatusCode() != 200) {
            logger.error("response status code {}", response.getStatusLine().getStatusCode());
            throw new RuntimeException(response.getStatusLine().toString());
        }
        this.jdbcTemplate.update(
                "Update jibbix_emitter set participiants_added = true where jira_issue_id = ?",
                this.jira_issue_id
        );
    }
}