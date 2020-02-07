package com.emitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;


import static com.emitter.utils.Utils.getValueFromRegioncomResponse;

public class PostCreateForeignIssue {
    private String summary;
    private String descr;
    private String jira_issue_id;
    private JdbcTemplate jdbcTemplate;

    private static final Logger logger = LoggerFactory.getLogger(PostCreateForeignIssue.class);

    public PostCreateForeignIssue(String summary, String descr, String jira_issue_id, JdbcTemplate jdbcTemplate) {
        this.summary = summary;
        this.descr = descr;
        this.jira_issue_id = jira_issue_id;
        this.jdbcTemplate = jdbcTemplate;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public void main() throws RuntimeException {
        logger.info("Creating task in Regioncom servicedesk....");
        String restUrl = WebApp.properties.getRegioncomJiraUri();
        String username = WebApp.properties.getRegioncomJiraUser();
        String password = WebApp.properties.getRegioncomJiraPassword();
        String jsonData = "{ \"serviceDeskId\": \"11\", \"requestTypeId\": \"184\", \"requestFieldValues\": { \"summary\": \"" + this.summary + "\", \"description\": \"" + this.descr + "\"}}";
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
        logger.info("Post parameters : " + jsonData);
        logger.info("Sending POST...");
        logger.info(response.toString());
        logger.info(response.getStatusLine().toString());
        logger.info("Response Code : {}", response.getStatusLine().getStatusCode());
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        while ((responseLine = reader.readLine()) != null) {
            result.append(responseLine);
            result.append("\n");
        }

        logger.info("Response body: {}", result.toString());
        if (response.getStatusLine().getStatusCode() != 201
                && response.getStatusLine().getStatusCode() != 200) {
            throw new RuntimeException(response.getStatusLine().toString());
        }
        try {
            //String x = "{\"_expands\":[\"participant\",\"status\",\"sla\",\"requestType\",\"serviceDesk\"],\"issueId\":\"77753\",\"issueKey\":\"SD-9147\",\"requestTypeId\":\"184\",\"serviceDeskId\":\"11\",\"createdDate\":{\"iso8601\":\"2019-07-11T17:00:31+0300\",\"jira\":\"2019-07-11T17:00:31.278+0300\",\"friendly\":\"11.07.2019 17:00\",\"epochMillis\":1562853631278},\"reporter\":{\"name\":\"domain_e_krasnoyarov\",\"key\":\"ekrasnoyarov@domain.ru\",\"emailAddress\":\"ekrasnoyarov@domain.ru\",\"displayName\":\"Эмиль Краснояров/domain\",\"active\":true,\"timeZone\":\"Europe/Moscow\",\"_links\":{\"jiraRest\":\"https://servicedesc.domain.ru/rest/api/2/user?username=domain_e_krasnoyarov\",\"avatarUrls\":{\"48x48\":\"https://www.gravatar.com/avatar/4c9bc35bb83fe9d9e4aeda0f43d51b1c?d=mm&s=48\",\"24x24\":\"https://www.gravatar.com/avatar/4c9bc35bb83fe9d9e4aeda0f43d51b1c?d=mm&s=24\",\"16x16\":\"https://www.gravatar.com/avatar/4c9bc35bb83fe9d9e4aeda0f43d51b1c?d=mm&s=16\",\"32x32\":\"https://www.gravatar.com/avatar/4c9bc35bb83fe9d9e4aeda0f43d51b1c?d=mm&s=32\"},\"self\":\"https://servicedesc.domain.ru/rest/api/2/user?username=domain_e_krasnoyarov\"}},\"requestFieldValues\":[{\"fieldId\":\"summary\",\"label\":\"Тема\",\"value\":\"Недоступен сервер [10.35.160.25]\"},{\"fieldId\":\"description\",\"label\":\"Описание\",\"value\":\"http://zabbix.ffoms.ru/zabbix/tr_events.php?triggerid=30627&eventid=645024\"},{\"fieldId\":\"attachment\",\"label\":\"Приложенные файлы\",\"value\":[],\"renderedValue\":[]}],\"currentStatus\":{\"status\":\"В работе\",\"statusDate\":{\"iso8601\":\"2019-07-11T17:00:31+0300\",\"jira\":\"2019-07-11T17:00:31.278+0300\",\"friendly\":\"11.07.2019 17:00\",\"epochMillis\":1562853631278}},\"_links\":{\"web\":\"https://servicedesc.domain.ru/servicedesk/customer/portal/11/SD-9147\",\"self\":\"https://servicedesc.domain.ru/rest/servicedeskapi/request/77753\"}}";
            logger.info("Updating regioncom_link, regioncom_response....");
            this.jdbcTemplate.update(
                    "Update jibbix_emitter set regioncom_link = ?, regioncom_response = ? where jira_issue_id = ?",
                    getValueFromRegioncomResponse(result.toString(), "_links", "web"),
                    result.toString(),
                    this.jira_issue_id
            );
        } catch (Exception e) {
            logger.error("executeHttpRequest.... strange moment, check the query conditions! ");
        }
    }
}