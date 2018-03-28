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
    //le lien de serveur
    private final static String LOCAL = "http://vps507764.ovh.net:8080/projet_isidis/";
    //login name
    private TextView ttInputUserName;
    //login password
    private TextView ttInputPWD;
    //login bouton
    private Button bt_login;
    //user d'object
    private UserEntity userEntity;
    //http commande par un url
    private HttpUrlCommande httpUrlCommande;
    //un intent scanner page
    private Intent intentScanner;
    //une reponse
    private int requestNomJoueurs=1;
    private String url,id_table,dataJson,nom;
    private ArrayList<HashMap<String,String>> list;
    private boolean flag=true;
    private TextView tt_error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //creer un objet login name
        ttInputUserName = (TextView)findViewById(R.id.input_username);
        //creer un objet login pwd
        ttInputPWD = (TextView)findViewById(R.id.input_pwd);
        //une message erreur
        tt_error = (TextView)findViewById(R.id.tt_error);
        //realiser http commande par un url
        httpUrlCommande = new HttpUrlCommande();
        //realiser un bouton login
        bt_login = (Button) findViewById(R.id.btn_login);
        //rajouter onClickListen sur le bouton login
        bt_login.setOnClickListener(new MyClickListener());
        //realiser un user d'objet
        userEntity = UserEntity.getEntity();
    }

    //button onClickListen
    class MyClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v){
            switch (v.getId()){
                //button login on click
                case R.id.btn_login:
                    //creer une thread pour communiquer au serveur
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
        //récupérer la chaîne de login name
        nom = ttInputUserName.getText().toString().trim();
        //récupérer la chaîne de login pwd
        String pwd = ttInputPWD.getText().toString().trim();
        //composer le lien de login avec nom et pwd
        String url = LOCAL+"userLogin/"+nom+"/"+pwd;
        String result=null;
        result = httpUrlCommande.commandeUrl(url).trim();
        if(result != null || !result.equals(null) || !result.equals("")){
            //déclaré un jsonabject pour analyser la réponse du serveur
            JSONObject jsonObj = null;
            try {
                //récupérer la réponse en json
                jsonObj = new JSONObject(result);
            } catch (JSONException e) {
                //tt_error.setText("incorrect username or password !");
            }
            //si reçu la réponse n'est pas null
            if(jsonObj != null){
                //tt_error.setText("");
                //System.out.println(jsonObj.optInt("id"));
                //récupérer l'id de user login
                userEntity.setId(jsonObj.optInt("id"));
                //récupérer le nom de user login
                userEntity.setNom(jsonObj.optString("nom"));
                //System.out.println(userEntity.toString());
                //scanner
                IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity.this);
                intentIntegrator.initiateScan();
            }
        }else{
            //message errer de login
            messageError("incorrect username or password !");
        }
    }

    @Override
    //rechercher la fonction onActivityResult de scanner
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //analyser le lien du qr code
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        //si un bien lien
        if (result != null) {
            if (result.getContents() == null) {
                messageError("");
            } else {

                //recuperer URL
                url = result.getContents();
                splitUrl();
                Thread thread = new Thread(runnableMenus);
                thread.start();
                //faire un flag pour attendre la réponse de la commande par le lien de qr code
                while(flag){
                    //rien fait
                }
                //réaliser un entent et transporter la data et l'id de la table au page menu
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

    //composer url
    public void splitUrl(){
        String[] lien = url.split("/");
        id_table = lien[lien.length-1];
        url = url.substring(0,url.length()-2);
        //Toast.makeText(this, url, Toast.LENGTH_LONG).show();
    }
    //récupérer les menus par le lien de commande
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
