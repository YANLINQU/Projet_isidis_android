package com.example.yanlin.projet_isidis;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by yanlin on 2018/1/24.
 */

public class Menus extends Activity {
    //le lien de serveur
    private final static String LOCAL = "http://vps507764.ovh.net:8080/projet_isidis/";
    //user d'object
    private UserEntity userEntity;
    //http commande par un url
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
        //récupérer la data et id de la table  par intent de main
        dataJson = intention.getStringExtra("dataJson");
        id_table = intention.getStringExtra("id_table");
        //un objet singleton
        userEntity = UserEntity.getEntity();
        httpUrlCommande = new HttpUrlCommande();
        //réaliser une liste view
        mListView = (ListView)findViewById(R.id.networklist);
        try {
            analyserDataByJson(dataJson);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
    //analyser json data et afficher
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
        //utiliser une liste view adapter pour afficher les menu dans une liste
        listviewAdapter adapter = new listviewAdapter(this, list,id_group,Integer.valueOf(id_table));
        mListView.setAdapter(adapter);
    }


}
