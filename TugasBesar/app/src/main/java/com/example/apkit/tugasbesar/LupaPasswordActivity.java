package com.example.apkit.tugasbesar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class LupaPasswordActivity extends AppCompatActivity {
    EditText txtPass;
    Button btnSimpan;
    private ProgressDialog progressDialog;
    SessionManager sessionManager = new SessionManager();
    String webPage=null,email;
    String postValue=null;
    UrlConfig myUrl = new UrlConfig();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lupa_password);
        txtPass = (EditText) findViewById(R.id.txt_lupa_pass_baru);
        btnSimpan = (Button) findViewById(R.id.btn_lupa_pass_submit);
        Intent intent = getIntent();
        email = intent.getStringExtra("EMAIL");
       // btnSimpan.setText(email);
        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postValue="email="+email;
                postValue+="&pass="+txtPass.getText().toString();
                prosesGet(myUrl.LUPA_PASS_URL,postValue);
            }
        });
    }
    private void prosesGet(String strUrl, final String param){
        progressDialog = progressDialog.show(this,"","Connencting!!");
        final String url = strUrl;

        new Thread(){
            public void run(){
                InputStream in = null;
                Message msg = Message.obtain();
                msg.what=1;
                try{
                    in = openHttpConnection(url,param);
                    if(in==null){
                        progressDialog.dismiss();
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
            URL url = new URL(strUrl);
            URLConnection urlConn = url.openConnection();

            if (!(urlConn instanceof HttpURLConnection)) {
                throw new IOException("URL is not an Http URL");
            }
            HttpURLConnection httpConn = (HttpURLConnection) urlConn;
            //httpConn.setAllowUserInteraction(false);
            httpConn.setConnectTimeout(10000);
            httpConn.setReadTimeout(10000);
            httpConn.setDoInput(true);
            httpConn.setDoOutput(true);
            httpConn.setInstanceFollowRedirects(false);
            httpConn.setRequestMethod("POST");
            httpConn.setFixedLengthStreamingMode(param.getBytes().length);
            httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            PrintWriter out = new PrintWriter(httpConn.getOutputStream());
            out.print(param);
            out.close();
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
           // Toast.makeText(getApplicationContext(), strData, Toast.LENGTH_LONG).show();
            try{

                JSONObject jsonRootObject = new JSONObject(strData);

                String error = jsonRootObject.optString("Error").toString();
                if(error.equals("False")){
                    Toast.makeText(getApplicationContext(), "Password telah diganti", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LupaPasswordActivity.this, LoginActivity.class));
                }
                else {
                    Toast.makeText(getApplicationContext(), "Ganti Password Gagal", Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            progressDialog.dismiss();
        }
    };
}
