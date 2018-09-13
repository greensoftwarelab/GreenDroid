package GreenSourceBridge;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class GSUtils {



    public static Pair<Integer,String> sendJSONtoDB(String url, String JSONMessage) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        Integer httpRes = 0;
        String responseContent="";
        try {
            HttpPost request = new HttpPost(url);
            StringEntity params = new StringEntity(JSONMessage);
            request.addHeader("content-type", "application/json");
            request.setEntity(params);
            HttpResponse httpResponse = httpClient.execute(request);
            HttpEntity responseEntity = httpResponse.getEntity();
            httpRes = httpResponse.getStatusLine().getStatusCode();
            if (responseEntity != null) {
                responseContent = EntityUtils.toString(responseEntity);
            }

        } catch (Exception ex) {
            // handle exception here
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new Pair<Integer, String>(httpRes,responseContent);
    }
}
