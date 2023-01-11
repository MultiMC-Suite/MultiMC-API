package fr.multimc.api.commons.data.sources.rest;

import fr.multimc.api.commons.data.sources.DataSourceType;
import fr.multimc.api.commons.data.sources.IDataSource;
import fr.multimc.api.commons.data.sources.rest.models.requests.LoginRequestModel;
import fr.multimc.api.commons.data.sources.rest.models.responses.LoginResponseModel;
import fr.multimc.api.commons.tools.json.JSONUtils;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.logging.Logger;

public class RestAPI implements IDataSource {

    private final Logger logger;
    private final String apiURL;
    private final String username;
    private final String password;
    private String token = null;

    HttpClient httpClient = HttpClientBuilder.create().build();

    public RestAPI(Logger logger, String apiURL, String username, String password) {
        this.logger = logger;
        this.apiURL = apiURL;
        this.username = username;
        this.password = password;
    }

    public boolean login(){
        try {
            HttpResponse response = this.sendPostRequest("/api/token", JSONUtils.toJson(new LoginRequestModel(this.username, this.password)), false);
            if(response.getStatusLine().getStatusCode() == 200){
                String jsonResponse = EntityUtils.toString(response.getEntity());
                LoginResponseModel loginResponseModel = JSONUtils.fromJson(LoginResponseModel.class, jsonResponse);
                this.token = loginResponseModel.token();
                this.logger.info("Logged in to the API");
                return true;
            }else{
                this.logger.severe("Failed to login to the API, error %d".formatted(response.getStatusLine().getStatusCode()));
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void setAuthenticationHeader(HttpRequest request){
        request.setHeader("Authorization", "Bearer " + this.token);
    }

    public HttpResponse sendGetRequest(String urlPath, boolean authenticated) throws IOException {
        HttpGet request = new HttpGet(apiURL + urlPath);
        if(authenticated)
            this.setAuthenticationHeader(request);
        return httpClient.execute(request);
    }

    public HttpResponse sendPostRequest(String urlPath, String jsonContent, boolean authenticated) throws IOException {
        HttpPost request = new HttpPost(apiURL + urlPath);
        request.setHeader("Content-Type", "application/json");
        if(authenticated)
            this.setAuthenticationHeader(request);
        request.setEntity(new StringEntity(jsonContent));
        return httpClient.execute(request);
    }

    @Override
    public DataSourceType getDataSourceType() {
        return DataSourceType.REST_API;
    }
}
