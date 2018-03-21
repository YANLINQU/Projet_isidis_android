package com.example.yanlin.projet_isidis;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by yanlin on 2018/1/24.
 */

public class HttpUrlCommande {

    
    // http response
    private HttpResponse mHttpResponse = null;
    // http entity
    private HttpEntity mHttpEntity = null;

    public void HttpUrlCommande(){

    }

    public String commandeUrl(String url){
        // creer une requete http
        HttpGet httpGet = new HttpGet(url);
        // http client
        HttpClient httpClient = new DefaultHttpClient();
        InputStream inputStream = null;
        try {
            //  executer une requete
            mHttpResponse = httpClient.execute(httpGet);
            mHttpEntity = mHttpResponse.getEntity();
            // recuperer un stream plux
            inputStream = mHttpEntity.getContent();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String result = "";
            String line = "";

            while (null != (line = bufferedReader.readLine()))
            {
                result += line;
            }
            // out result json
            System.out.println("result:"+result);

            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*
        finally
        {
            try
            {
                //close flux
                inputStream.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        */
        return null;
    }
}
