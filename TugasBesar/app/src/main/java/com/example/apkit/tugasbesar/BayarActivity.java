package com.example.apkit.tugasbesar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
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

public class BayarActivity extends AppCompatActivity {
    ListView listView;
    AdapterRekening adapterRekening;
    private ProgressDialog progressDialog;
    TextView txtTotalBayar;
    SessionManager sessionManager = new SessionManager();
    String webPage=null;
    String postValue=null;
    Button btnKonfirmasi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bayar);
        listView = (ListView) findViewById(R.id.list_bayar_rekening);
        //progressBar.setVisibility(View.INVISIBLE);
        txtTotalBayar = (TextView) findViewById(R.id.txt_bayar_jumlah);
        btnKonfirmasi = (Button) findViewById(R.id.btn_bayar_konfirmasi);
        adapterRekening = new AdapterRekening(BayarActivity.this, R.layout.list_rekening);
        listView.setAdapter(adapterRekening);
        postValue="api_key="+sessionManager.getPreferences(BayarActivity.this,"ACCESS_TOKEN");
        prosesGet(UrlConfig.GET_BAYAR_URL, postValue);
        Intent intent = getIntent();
        txtTotalBayar.setText("Rp. " + intent.getStringExtra("TOTAL").toString() + ",-");
        btnKonfirmasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BayarActivity.this,KonfirmasiBayarActivity.class));
            }
        });
    }
    private void prosesGet(String strUrl, final String param){
        final String url = strUrl;
        progressDialog = progressDialog.show(this,"","Connencting!!");
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
                    DetailRekening detail = new DetailRekening();
                    JSONObject jsonObject=jsonArray.getJSONObject(i);
                    detail.setIdRekening(jsonObject.getString("id_rekening"));
                    detail.setNoRekening(jsonObject.getString("noRek"));
                    detail.setNamaRekening(jsonObject.getString("namaRek"));
                    detail.setKantorRekening(jsonObject.getString("kantorRek"));
                    detail.setUrlRekening(jsonObject.getString("urlRek"));
                    adapterRekening.add(detail);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressDialog.dismiss();
        }
    };
}
