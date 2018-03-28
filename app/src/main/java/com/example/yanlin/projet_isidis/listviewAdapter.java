package com.example.yanlin.projet_isidis;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yanlin on 2018/1/24.
 */

public class listviewAdapter extends BaseAdapter implements View.OnClickListener{
    private final static String LOCAL = "http://vps507764.ovh.net:8080/projet_isidis/commandeMenu";
    //menuCommande
    public ArrayList<HashMap<String,String>> list;
    private Activity activity;
    private String[] id_group;
    HttpURLConnection conn = null;
    public static final String NETWORK_GET = "NETWORK_GET";
    private String requestHeader = null;
    private byte[] requestBody = null;
    private byte[] responseBody = null;
    private String responseHeader = null;
    private SocketClient socketClient;
    private int t,id_table;
    private UserEntity userEntity;
    private Handler handler = new Handler();
    private ViewHolder holder;
    private Bitmap bitmap;
    private Map<Integer,Bitmap> bitmaps;
    private boolean[] flags;

    public listviewAdapter(Activity activity, ArrayList<HashMap<String,String>> list,String[] group,int table) {
        super();
        this.activity = activity;
        this.list = list;
        bitmaps = new HashMap<Integer,Bitmap>();
        id_group = group;
        id_table = table;
//        for(String s:id_group)
//            System.out.println("id_group:"+s);
        userEntity = UserEntity.getEntity();

        flags =new boolean[list.size()];
        for(int i=0;i<list.size();i++){
            flags[i]=true;
        }


    }
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }


    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }


    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    public void getBitMapAll(){
        for(HashMap<String,String> hm : list){
            final HashMap<String, String> map = hm;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //chergerImage(compte,map);
                }
            }).start();
        }
    }
    //charger les images synchroniser
    public synchronized void chergerImage(int position,HashMap<String,String> map){
        bitmap = getImageFromNet(map.get("IMAGE").trim());
        bitmaps.put(position,bitmap) ;
        flags[position]=false;
        System.out.println("position:"+position+" flag:"+flags[position]);
    }
    //recharger les informations sur la liste view
    public View getView(final int position, View convertView, ViewGroup parent) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                chergerImage(position,list.get(position));
            }
        }).start();
        while(flags[position]){
            //donothing
            System.out.println("flags "+position+":"+flags[position]);
        }

        LayoutInflater inflater = activity.getLayoutInflater();

        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.listmenus, null);
            holder = new ViewHolder();
            holder.image_menu = (ImageView) convertView.findViewById(R.id.image_menu);
            holder.nomme = (TextView) convertView.findViewById(R.id.nomme);
            holder.prix = (TextView) convertView.findViewById(R.id.prix);
            holder.bt_commande = (Button) convertView.findViewById(R.id.bt_commande);
            convertView.setTag(holder);
        }
        holder = (ViewHolder) convertView.getTag();

        final HashMap<String, String> map = list.get(position);
        //final String url = map.get("IMAGE").trim();
        System.out.println("photot:"+bitmaps.get(position));
        holder.image_menu.setImageBitmap(bitmaps.get(position));
        //holder.image_menu.setImageBitmap(img);
        holder.nomme.setText(map.get("NOMME"));
        holder.prix.setText(map.get("PRIX"));
        holder.bt_commande.setTag(R.id.bt_commande,position);
        holder.bt_commande.setOnClickListener(this);
        return convertView;
    }

    @Override
    //les fonction de bouton commande
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_commande:
                t = (int)v.getTag(R.id.bt_commande);
                System.out.println("I click sur le button "+id_group[t]);
                new Thread(runnableCommande).start();

                break;
        }
    }

    Runnable runnableCommande = new Runnable(){
        @Override
        public void run() {
            // TODO: http request.
            netWorkGet(id_group[t]);
        }
    };

    private class ViewHolder {
        ImageView image_menu;
        TextView nomme;
        TextView prix;
        Button bt_commande;
    }
    //envoyer une requête GET au serveur
    public void netWorkGet(String id_menu){

        try {
            StringBuilder buf = new StringBuilder(LOCAL);
            buf.append("?");
            buf.append("id_table="+id_table+"&");
            buf.append("id_client="+userEntity.getId()+"&");
            buf.append("id_menu="+id_menu);
            URL url = new URL(buf.toString());
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            if(conn.getResponseCode()==200){
                SocketClient client = new SocketClient();
                client.send();
            } else
                System.out.println("faild");

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Bitmap getImageFromNet(String url) {
        HttpURLConnection conn = null;
        try {
            URL mURL = new URL(url);
            conn = (HttpURLConnection) mURL.openConnection();
            conn.setRequestMethod("GET"); //le type de requete
            conn.setConnectTimeout(10000); //le temp de connection timeout
            conn.setReadTimeout(5000);  //le temp de lire timeout

            conn.connect(); //se connecter

            int responseCode = conn.getResponseCode(); //récupérer l'état de connection
            if (responseCode == 200) {
                //succès
                InputStream is = conn.getInputStream(); //récupérer la réponse
                Bitmap bitmap = BitmapFactory.decodeStream(is); //déclarer un objet BITMAP
                return bitmap;

            } else {
                //confection d'échec
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect(); //déconnecter
            }
        }
        return null;
    }
}
