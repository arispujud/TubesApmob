package com.example.apkit.tugasbesar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class RegisterActivity extends AppCompatActivity {
    EditText txtNama, txtEmail, txtPassword, txtAlamat, txtPasswordConfirm,txtTelp,txtLine;
    CheckBox cbPersetujuan;
    Button btnRegister;

    private ProgressDialog progressDialog;
    String webPage=null;
    String postValue=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        txtNama= (EditText) findViewById(R.id.txt_regis_nama);
        txtEmail=(EditText) findViewById(R.id.txt_regis_email);
        txtPassword = (EditText) findViewById(R.id.txt_regis_password);
        txtAlamat = (EditText) findViewById(R.id.txt_regis_alamat);
        txtPasswordConfirm = (EditText)findViewById(R.id.txt_regis_password_confirm);
        txtTelp = (EditText) findViewById(R.id.txt_regis_telp);
        txtLine = (EditText) findViewById(R.id.txt_regis_line);
        cbPersetujuan = (CheckBox) findViewById(R.id.check_persyaratan);
        btnRegister = (Button) findViewById(R.id.btn_Daftar);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String nama = txtNama.getText().toString();
                final String password = txtPassword.getText().toString();
                final String email = txtEmail.getText().toString();
                final String password_confirm = txtPasswordConfirm.getText().toString();
                final String alamat = txtAlamat.getText().toString();
                final String tlp = txtTelp.getText().toString();
                final String line = txtLine.getText().toString();

                if (cbPersetujuan.isChecked() == true) {
                    if (!nama.equals(null) && !password.equals(null) && !email.equals(null) &&
                            !alamat.equals(null) && !password_confirm.equals(null)) {
                        if (password.equals(password_confirm)) {
                            postValue = "nama=" + nama + "&password=" + password + "&email=" + email + "&alamat=" + alamat + "&token=notset" + "&tlp=" + tlp + "&line=" + line;
                            prosesRegistrasi(UrlConfig.REGISTER_URL, postValue);
                        } else {
                            Toast.makeText(getApplicationContext(), "Konfirmasi Password Salah!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Tidak boleh ada yang kosong", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Checklist Persetujuan!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void prosesRegistrasi(String strUrl, final String param){
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
          //Toast.makeText(getApplicationContext(),(String)(msg.getData().getString("str")),Toast.LENGTH_SHORT).show();
          if(strData.equals("Success\n")){
              progressDialog.dismiss();
              Toast.makeText(getApplicationContext(), "Email Aktivasi Akun telah dikirim ke E-Mail Anda! Silahkan lakukan Aktivasi akun!", Toast.LENGTH_LONG).show();
              startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
          }
          else if(strData.equals("Timeout\n")){
              Toast.makeText(getApplicationContext(), "Timeout!!!", Toast.LENGTH_LONG).show();
          }
          else{
              Toast.makeText(getApplicationContext(), "Registrasi Gagal!", Toast.LENGTH_LONG).show();
          }
          progressDialog.dismiss();
      }
    };

    private boolean checkInternetConenction() {
        // get Connectivity Manager object to check connection
        ConnectivityManager connec =(ConnectivityManager)getSystemService(getBaseContext().CONNECTIVITY_SERVICE);

        // Check for network connections
        if ( connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||

                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED ) {
            Toast.makeText(this, " Connected ", Toast.LENGTH_LONG).show();
            return true;
        }else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED  ) {
            Toast.makeText(this, " Not Connected ", Toast.LENGTH_LONG).show();
            return false;
        }
        return false;
    }
}
