package com.example.apkit.tugasbesar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

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


public class TagihanActivity extends AppCompatActivity {
    public long totalHarga;
    ListView listView;
    AdapterTagihan adapterTagihan;
    ProgressBar progressBar;
    SessionManager sessionManager = new SessionManager();
    String webPage=null;
    String postValue=null;
    TextView txtBayar;
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tagihan);
        listView = (ListView) findViewById(R.id.list_tagihan);
        progressBar = (ProgressBar) findViewById(R.id.tagihan_progress);
        txtBayar = (TextView) findViewById(R.id.txt_tagihan_bayar);
        btn = (Button) findViewById(R.id.btn_tagihan_bayar);
        adapterTagihan = new AdapterTagihan(TagihanActivity.this, R.layout.list_tagihan);
        listView.setAdapter(adapterTagihan);
        postValue="api_key="+sessionManager.getPreferences(TagihanActivity.this,"ACCESS_TOKEN");
        prosesGet(UrlConfig.GET_TAGIHAN_URL, postValue);

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
            //Toast.makeText(getApplicationContext(),strData,Toast.LENGTH_SHORT).show();
            try{
                JSONArray jsonArray= new JSONObject(strData).getJSONArray("data");
                for(int i=0; i<jsonArray.length(); i++){
                    DetailListOrder detail = new DetailListOrder();
                    JSONObject jsonObject=jsonArray.getJSONObject(i);
                    detail.setIdOrder(jsonObject.getString("id_order"));
                    detail.setSubject(jsonObject.getString("subject"));
                    detail.setJumlah(jsonObject.getString("jumlah"));
                    detail.setHarga(jsonObject.getString("harga"));
                    totalHarga+=Long.parseLong(jsonObject.getString("harga"));
                    adapterTagihan.add(detail);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            txtBayar.setText("Rp " + totalHarga + ",-");
            if(totalHarga==0){
                btn.setEnabled(false);
            }
            else{
                btn.setEnabled(true);
            }
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(TagihanActivity.this,BayarActivity.class);
                    i.putExtra("TOTAL",String.valueOf(totalHarga));
                    startActivity(i);
                }
            });
            progressBar.setVisibility(View.INVISIBLE);
        }
    };
}
