package com.example.apkit.tugasbesar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class ListOrderActivity extends AppCompatActivity {
    ListView listView;
    AdapterListOrder adapterListOrder;
    ProgressBar progressBar;
    SessionManager sessionManager = new SessionManager();
    String webPage=null;
    String postValue=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_order);
        listView = (ListView) findViewById(R.id.list_order);
        progressBar = (ProgressBar) findViewById(R.id.list_order_progressBar);
        //progressBar.setVisibility(View.INVISIBLE);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_listorder_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //         .setAction("Action", null).show();
                startActivity(new Intent(ListOrderActivity.this, AddOrder.class));
            }
        });
        adapterListOrder = new AdapterListOrder(ListOrderActivity.this, R.layout.list_item);
        listView.setAdapter(adapterListOrder);
        postValue="api_key="+sessionManager.getPreferences(ListOrderActivity.this,"ACCESS_TOKEN");
        prosesGet(UrlConfig.GET_ORDER_URL, postValue);
    }
    private void prosesGet(String strUrl, final String param){
        final String url = strUrl;
        progressBar.setVisibility(View.VISIBLE);
        new Thread(){
            public void run(){
                InputStream in = null;
                Message msg = Message.obtain();
                msg.what=1;
                try{
                    in = openHttpConnection(url,param);
                    if(in==null){
                        Bundle b = new Bundle();
                        b.putString("str", "Timeout\n");
                        msg.setData(b);
                    }else {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        StringBuilder str = new StringBuilder();
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            str.append(line + "\n");
                        }
                        webPage = str.toString();
                        Bundle b = new Bundle();
                        b.putString("str", webPage);
                        msg.setData(b);
                        in.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                messageHandler.sendMessage(msg);
            }
        }.start();
    }

    private InputStream openHttpConnection(String strUrl, String param){
        InputStream in = null;
        int resCode = -1;
        try{
            URL url = new URL(strUrl+"?"+param);
            URLConnection urlConn = url.openConnection();

            if (!(urlConn instanceof HttpURLConnection)) {
                throw new IOException("URL is not an Http URL");
            }
            HttpURLConnection httpConn = (HttpURLConnection) urlConn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setConnectTimeout(10000);
            httpConn.setReadTimeout(10000);
            //httpConn.setDoOutput(true);
            httpConn.setInstanceFollowRedirects(false);
            httpConn.setRequestMethod("GET");
            //httpConn.setFixedLengthStreamingMode(param.getBytes().length);
            httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            //PrintWriter out = new PrintWriter(httpConn.getOutputStream());
            //out.print(param);
            //out.close();
            httpConn.connect();
            resCode = httpConn.getResponseCode();

            if (resCode == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();
            }
            else{
                in=null;
            }
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return in;
    }

    private Handler messageHandler = new Handler(){
        public  void handleMessage(Message msg){
            super.handleMessage(msg);
            final String strData = (String)(msg.getData().getString("str"));
            try{
                JSONArray jsonArray= new JSONObject(strData).getJSONArray("data");
                for(int i=0; i<jsonArray.length(); i++){
                    DetailListOrder detail = new DetailListOrder();
                    JSONObject jsonObject=jsonArray.getJSONObject(i);
                    detail.setIdOrder(jsonObject.getString("id_order"));
                    detail.setSubject(jsonObject.getString("subject"));
                    detail.setStatus(jsonObject.getString("status"));
                    detail.setProgres(jsonObject.getString("progres"));
                    detail.setHarga(jsonObject.getString("harga"));
                    detail.setTanggal(jsonObject.getString("tgl"));
                    detail.setJam(jsonObject.getString("jam"));
                    adapterListOrder.add(detail);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressBar.setVisibility(View.INVISIBLE);
        }
    };
}
