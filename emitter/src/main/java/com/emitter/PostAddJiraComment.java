package com.emitter;

import com.atlassian.jira.rest.client.api.domain.Issue;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;


public class PostAddJiraComment {
    public static final Logger logger = LoggerFactory.getLogger(PostAddJiraComment.class);
    private String issueGeneratedLink;
    private Issue oneissue;

    public PostAddJiraComment(String issueGeneratedLink, Issue oneissue) {
        this.issueGeneratedLink = issueGeneratedLink;
        this.oneissue = oneissue;
    }

    public PostAddJiraComment() {
    }

    public String getIssueGeneratedLink() {
        return issueGeneratedLink;
    }

    public void setIssueGeneratedLink(String issueGeneratedLink) {
        this.issueGeneratedLink = issueGeneratedLink;
    }

    public void main() throws RuntimeException {
        logger.info("Updating Jira Issue's description. Adding a comment with Regioncom link...");
        String restUrl = WebApp.properties.getJiraServerUri().toString().concat("/rest/api/2/issue/").concat(this.oneissue.getKey()).concat("/comment");
        String username = WebApp.properties.getJiraUSER();
        String password = WebApp.properties.getJiraPASS();
        String jsonData = "{\n" +
                "    \"body\": \"" + this.issueGeneratedLink + "\"\n" +
                "}";
        HttpPost httpPost = this.createConnectivity(restUrl, username, password);
        this.executeReq(jsonData, httpPost, oneissue);
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

    public void executeReq(String jsonData, HttpPost httpPost, Issue oneissue) throws RuntimeException {
        try {
            executeHttpRequest(jsonData, httpPost, oneissue);
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

    public void executeHttpRequest(String jsonData, HttpPost httpPost, Issue oneIssue) throws RuntimeException, UnsupportedEncodingException, IOException {
        String ackMessage = "Fixed by Jibbix-emitter. Here is the link: ";
        HttpResponse response = null;
        String responseLine = "";
        StringBuffer result = new StringBuffer();
        httpPost.setEntity(new StringEntity(jsonData, "UTF-8"));
        HttpClient client = HttpClientBuilder.create().build();
        response = client.execute(httpPost);
        logger.info("Post parameters : {}", jsonData);
        logger.info("Response Code : {}", response.getStatusLine().getStatusCode());
        if (response.getStatusLine().getStatusCode() != 201
                && response.getStatusLine().getStatusCode() != 200) {
            throw new RuntimeException(response.getStatusLine().toString());
        }
    }
}