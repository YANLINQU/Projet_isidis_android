package com.example.yanlin.projet_isidis;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by yanlin on 2018/1/24.
 */

public class Menus extends Activity {
    private final static String LOCAL = "http://vps507764.ovh.net:8080/projet_isidis/";
    private UserEntity userEntity;
    private HttpUrlCommande httpUrlCommande;
    private String dataJson,id_table;
    private ArrayList<HashMap<String,String>> list;
    private ListView mListView;
    private Handler mHandler = new Handler();
    private String[] id_group;
    private boolean flag=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menus);

        Intent intention = getIntent();
        dataJson = intention.getStringExtra("dataJson");
        id_table = intention.getStringExtra("id_table");
        userEntity = UserEntity.getEntity();
        httpUrlCommande = new HttpUrlCommande();
        mListView = (ListView)findViewById(R.id.networklist);
        //envoyer requete pour mettre les Menus(json) dans la string data
        try {
            analyserDataByJson(dataJson);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void analyserDataByJson(String str) throws URISyntaxException {
        list = new ArrayList<HashMap<String,String>>();
        JSONArray jsonArray = null;
        System.out.println(str);
        try {
            jsonArray = new JSONArray(str);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        id_group = new String[jsonArray.length()];
        for (int i= 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.optJSONObject(i);
            String id = jsonObject.optString("id");
            String nomme = jsonObject.optString("nomme");
            String adresse = jsonObject.optString("imageadresse");
            String prix = jsonObject.optString("prix");
            HashMap<String,String> temp = new HashMap<String,String>();
            temp.put("IMAGE",LOCAL+adresse);
            temp.put("NOMME",nomme);
            temp.put("PRIX",prix);
            list.add(temp);
            id_group[i]=id;
            //System.out.println("id:"+id+",nomme:"+nomme+",adresse:"+adresse+",prix:"+prix);
        }
        listviewAdapter adapter = new listviewAdapter(this, list,id_group,Integer.valueOf(id_table));
        mListView.setAdapter(adapter);
    }


}
