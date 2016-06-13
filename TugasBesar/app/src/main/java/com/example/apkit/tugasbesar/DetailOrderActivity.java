package com.example.apkit.tugasbesar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;

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

public class DetailOrderActivity extends AppCompatActivity {
    ProgressBar progressBar;
    ScrollView isiDetail;
    TextView subject,bahan,jenis,harga,jumlah,alamat,progress,waktu;
    Button btnSetuju, btnBatal;
    SmartImageView imageView;
    SessionManager sessionManager = new SessionManager();
    String webPage=null;
    String postValue=null;
    int statusOrder;
    boolean statusDelete=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_order);
        isiDetail = (ScrollView) findViewById(R.id.isi_detail);
        progressBar = (ProgressBar) findViewById(R.id.detail_progress);
        subject = (TextView) findViewById(R.id.txt_detail_Subject);
        bahan = (TextView) findViewById(R.id.txt_detail_bahan);
        jenis = (TextView) findViewById(R.id.txt_detail_jenis);
        harga = (TextView) findViewById(R.id.txt_detail_harga);
        jumlah = (TextView) findViewById(R.id.txt_detail_jumlah);
        alamat = (TextView) findViewById(R.id.txt_detail_alamat);
        progress = (TextView) findViewById(R.id.txt_detail_progress);
        waktu = (TextView) findViewById(R.id.txt_detail_waktu);
        imageView = (SmartImageView) findViewById(R.id.img_detail_desain);
        btnSetuju = (Button) findViewById(R.id.btn_detail_setuju);
        btnBatal = (Button) findViewById(R.id.btn_detail_batal);
        Intent i = getIntent();
        //Toast.makeText(getApplicationContext(),i.getStringExtra("ID_ORDER"),Toast.LENGTH_SHORT).show();
        postValue="api_key="+sessionManager.getPreferences(DetailOrderActivity.this,"ACCESS_TOKEN")+"&id_order="+i.getStringExtra("ID_ORDER");
        prosesGet(UrlConfig.GET_ORDER_DETAIL_URL,postValue);
        btnBatal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlert();
            }
        });
    }
    private void prosesGet(String strUrl, final String param){
        final String url = strUrl;
        isiDetail.setVisibility(View.INVISIBLE);
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
                JSONObject jsonRootObject = new JSONObject(strData);
                subject.setText(jsonRootObject.optString("subject").toString());
                waktu.setText(jsonRootObject.optString("tgl").toString()+" "+jsonRootObject.optString("jam").toString());
                jenis.setText(jsonRootObject.optString("jenis"));
                bahan.setText(jsonRootObject.optString("bahan").toString());
                jumlah.setText(jsonRootObject.optString("jumlah").toString());
                harga.setText("Rp "+jsonRootObject.optString("harga").toString()+",-");
                alamat.setText(jsonRootObject.optString("alamat").toString());
                progress.setText(jsonRootObject.optString("progres").toString());
                imageView.setImageUrl(jsonRootObject.optString("desain").toString());
                if(!jsonRootObject.optString("status").toString().equals("1")){
                    if(jsonRootObject.optString("status").toString().equals("2")){
                        btnSetuju.setEnabled(true);
                        btnSetuju.setText("Konfirmasi Pembayaran");
                        statusOrder=2;
                    }
                    else {
                        btnSetuju.setEnabled(false);
                    }
                }
                else{
                    statusOrder=1;
                    btnSetuju.setEnabled(true);
                }
                if(jsonRootObject.optString("status").toString().equals("7")){
                    btnBatal.setText("HAPUS");
                    statusDelete=true;
                }
                if(jsonRootObject.optString("isUpdated").toString().equals("True")){
                    startActivity(new Intent(DetailOrderActivity.this,ListOrderActivity.class));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            btnSetuju.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(statusOrder==1){
                        startActivity(new Intent(DetailOrderActivity.this,TagihanActivity.class));
                    }
                    if(statusOrder==2){
                        startActivity(new Intent(DetailOrderActivity.this,KonfirmasiBayarActivity.class));
                    }
                }
            });
            progressBar.setVisibility(View.INVISIBLE);
            isiDetail.setVisibility(View.VISIBLE);
        }
    };
    private void showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String message="Apakah anda yakin akan membatalkan pesanan?";
        if(statusDelete==true){
            message="Data pesanan akan dihapus secara permanen apakah anda yakin?";
        }
        builder.setMessage(message).setTitle("Perhatian!")
                .setCancelable(true)
                .setPositiveButton("YA", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        prosesGet(UrlConfig.GET_ORDER_CENCEL_URL, postValue);
                    }
                })
                .setNegativeButton("TIDAK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
