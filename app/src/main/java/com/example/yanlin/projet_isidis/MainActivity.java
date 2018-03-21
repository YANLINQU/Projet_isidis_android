package com.example.yanlin.projet_isidis;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private final static String LOCAL = "http://vps507764.ovh.net:8080/projet_isidis/";
    private TextView ttInputUserName;
    private TextView ttInputPWD;
    private Button bt_login;
    private UserEntity userEntity;
    private HttpUrlCommande httpUrlCommande;
    private Intent intentScanner;
    private int requestNomJoueurs=1;
    private String url,id_table,dataJson,nom;
    private ArrayList<HashMap<String,String>> list;
    private boolean flag=true;
    private TextView tt_error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ttInputUserName = (TextView)findViewById(R.id.input_username);
        ttInputPWD = (TextView)findViewById(R.id.input_pwd);
        tt_error = (TextView)findViewById(R.id.tt_error);

        httpUrlCommande = new HttpUrlCommande();
        bt_login = (Button) findViewById(R.id.btn_login);
        bt_login.setOnClickListener(new MyClickListener());
        userEntity = UserEntity.getEntity();
    }

    //button onClickListen
    class MyClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v){
            switch (v.getId()){
                //button login
                case R.id.btn_login:
                    new Thread(runnable).start();
                    break;
                default:
                    break;
            }
        }
    }
    //login in
    Runnable runnable = new Runnable(){
        @Override
        public void run() {
            // TODO: http request.
            onclickLogin();
        }
    };

    Runnable runnableMenus = new Runnable(){
        @Override
        public void run() {
            // TODO: http request.
            requestMenus();
        }
    };

    public void onclickLogin(){
        nom = ttInputUserName.getText().toString().trim();
        String pwd = ttInputPWD.getText().toString().trim();
        String url = LOCAL+"userLogin/"+nom+"/"+pwd;
        String result=null;
        result = httpUrlCommande.commandeUrl(url).trim();
        if(result != null || !result.equals(null) || !result.equals("")){
            JSONObject jsonObj = null;
            try {
                jsonObj = new JSONObject(result);
            } catch (JSONException e) {
                //tt_error.setText("incorrect username or password !");
            }
            if(jsonObj != null){
                //tt_error.setText("");
                //System.out.println(jsonObj.optInt("id"));
                userEntity.setId(jsonObj.optInt("id"));
                userEntity.setNom(jsonObj.optString("nom"));
                //System.out.println(userEntity.toString());
                //scanner
                IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity.this);
                intentIntegrator.initiateScan();
            }
        }else{
            messageError("incorrect username or password !");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 获取解析结果
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                messageError("");
            } else {

                //recuperer URL
                url = result.getContents();
                splitUrl();
                Thread thread = new Thread(runnableMenus);
                thread.start();

                while(flag){
                    //rien fait
                }
                intentScanner = new Intent();
                intentScanner.setClass(MainActivity.this, Menus.class);
                intentScanner.putExtra("dataJson", dataJson);
                intentScanner.putExtra("id_table", id_table);
                //intentScanner.putExtra("id_table", id_table);
                //verifier qu'une application externe existe ou non
                if(intentScanner.resolveActivity(getPackageManager())!=null){
                    //oui existe
                    startActivityForResult(intentScanner,requestNomJoueurs);
                }else{
                    //non existe pas
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void splitUrl(){
        String[] lien = url.split("/");
        id_table = lien[lien.length-1];
        url = url.substring(0,url.length()-2);
        //Toast.makeText(this, url, Toast.LENGTH_LONG).show();
    }

    public void requestMenus(){
        dataJson = httpUrlCommande.commandeUrl(url);
        System.out.println("dataJson:"+dataJson);
        flag=false;
    }

    //afficher une message ERROR
    public void messageError(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
