package com.example.apkit.tugasbesar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class LoginActivity extends AppCompatActivity {
    EditText txtEmail, txtPassword;
    TextView txtRegister, txtLupaPassword;
    Button btnLogin;

    private ProgressDialog progressDialog;
    String webPage=null;
    String postValue=null;

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        txtEmail=(EditText) findViewById(R.id.txt_login_email);
        txtPassword=(EditText) findViewById(R.id.txt_login_password);
        txtRegister=(TextView) findViewById(R.id.txt_login_regis);
        txtLupaPassword=(TextView) findViewById(R.id.txt_login_forgot);
        btnLogin=(Button) findViewById(R.id.btn_login);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        sessionManager = new SessionManager();
        /*try{
            String status = sessionManager.getPreferences(LoginActivity.this,"STATUS");
            Log.d("STATUS", status);
            if(status.equals("1")){
                startActivity(new Intent(LoginActivity.this,MainActivity.class));
            }
        }catch (Exception e){

        }*/
        txtLupaPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, VerifikasiKodeActivity.class));
            }
        });
        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = txtEmail.getText().toString();
                final String password = txtPassword.getText().toString();
                if(!email.equals(null) && !password.equals(null)){
                    postValue="email="+email+"&password="+password;
                    prosesLogin(UrlConfig.LOGIN_URL,postValue);
                }
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        try{
            String status = sessionManager.getPreferences(LoginActivity.this,"STATUS");
            Log.d("STATUS", status);
            if(status.equals("1")){
                startActivity(new Intent(LoginActivity.this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            }
        }catch (Exception e){

        }
    }

    private void prosesLogin(String strUrl, final String param){
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
            try{
                JSONObject jsonRootObject = new JSONObject(strData);
                String status = jsonRootObject.optString("Status").toString();
                String token = jsonRootObject.optString("Token").toString();
                String id_user = jsonRootObject.optString("Id").toString();
                String nama = jsonRootObject.optString("Name").toString();
                String email = jsonRootObject.optString("Email").toString();
                String alamat = jsonRootObject.optString("Alamat").toString();
                String tlp = jsonRootObject.optString("Tlp").toString();
                String line = jsonRootObject.optString("Line").toString();
                if(status.equals("Success")){
                    if(token.equals("notset")){
                        Toast.makeText(getApplicationContext(), "Akun belum teraktivasi, silahkan aktivasi di email anda!", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Login Berhasil", Toast.LENGTH_SHORT).show();
                        sessionManager = new SessionManager();
                        sessionManager.setPreferences(LoginActivity.this,"STATUS","1");
                        sessionManager.setPreferences(LoginActivity.this,"ACCESS_TOKEN",token);
                        sessionManager.setPreferences(LoginActivity.this,"ID_USER",id_user);
                        sessionManager.setPreferences(LoginActivity.this,"NAMA",nama);
                        sessionManager.setPreferences(LoginActivity.this,"EMAIL",email);
                        sessionManager.setPreferences(LoginActivity.this,"ALAMAT",alamat);
                        sessionManager.setPreferences(LoginActivity.this,"TLP",tlp);
                        sessionManager.setPreferences(LoginActivity.this,"LINE",line);
                        Intent i = new Intent(LoginActivity.this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        finish();
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "Login Gagal", Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            progressDialog.dismiss();
        }
    };

}
