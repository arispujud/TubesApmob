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
import android.widget.ImageView;
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

public class ProfilActivity extends AppCompatActivity {
    EditText txtNama,txtAlamat,txtTlp,txtLine;
    ImageView btnEdit;
    Button btnSubmit;
    private ProgressDialog progressDialog;
    SessionManager sessionManager = new SessionManager();
    String webPage=null;
    String postValue=null;
    UrlConfig myUrl = new UrlConfig();
    boolean statusSubmit=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);
        txtNama = (EditText) findViewById(R.id.txt_profile_nama);
        txtAlamat = (EditText) findViewById(R.id.txt_profile_alamat);
        txtTlp = (EditText) findViewById(R.id.txt_profile_tlp);
        txtLine = (EditText) findViewById(R.id.txt_profile_line);
        btnEdit = (ImageView) findViewById(R.id.btn_profile_edit);
        btnSubmit = (Button) findViewById(R.id.btn_profile);

        txtNama.setText(sessionManager.getPreferences(ProfilActivity.this,"NAMA"));
        txtAlamat.setText(sessionManager.getPreferences(ProfilActivity.this,"ALAMAT"));
        txtTlp.setText(sessionManager.getPreferences(ProfilActivity.this,"TLP"));
        txtLine.setText(sessionManager.getPreferences(ProfilActivity.this,"LINE"));

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSubmit.setText("Simpan");
                txtNama.setEnabled(true);
                txtAlamat.setEnabled(true);
                txtTlp.setEnabled(true);
                txtLine.setEnabled(true);
                statusSubmit=true;
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(statusSubmit==true){
                    postValue="api_key="+sessionManager.getPreferences(ProfilActivity.this,"ACCESS_TOKEN");
                    postValue+="&nama="+txtNama.getText().toString();
                    postValue+="&alamat="+txtAlamat.getText().toString();
                    postValue+="&tlp="+txtTlp.getText().toString();
                    postValue+="&line="+txtLine.getText().toString();
                    //Toast.makeText(getApplicationContext(),postValue, Toast.LENGTH_LONG).show();
                    prosesGet(UrlConfig.GET_EDIT_PROFILE_URL, postValue);
                }
                else{
                    startActivity(new Intent(ProfilActivity.this,GantiPasswordActivity.class));
                }
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
            //Toast.makeText(getApplicationContext(),strData, Toast.LENGTH_LONG).show();
            try{

                JSONObject jsonRootObject = new JSONObject(strData);

                String error = jsonRootObject.optString("Error").toString();
                if(error.equals("False")){
                    Toast.makeText(getApplicationContext(), "Update Berhasil", Toast.LENGTH_SHORT).show();
                    String nama = jsonRootObject.getString("Nama");
                    String alamat = jsonRootObject.optString("Alamat").toString();
                    String line = jsonRootObject.optString("Line").toString();
                    String tlp = jsonRootObject.optString("Tlp").toString();
                    sessionManager = new SessionManager();
                    sessionManager.setPreferences(ProfilActivity.this, "NAMA", nama);
                    sessionManager.setPreferences(ProfilActivity.this,"ALAMAT",alamat);
                    sessionManager.setPreferences(ProfilActivity.this, "TLP", tlp);
                    sessionManager.setPreferences(ProfilActivity.this,"LINE",line);
                    txtNama.setEnabled(false);
                    txtNama.setText(nama);
                    txtAlamat.setEnabled(false);
                    txtAlamat.setText(alamat);
                    txtTlp.setEnabled(false);
                    txtTlp.setText(tlp);
                    txtLine.setEnabled(false);
                    txtLine.setText(line);
                    btnSubmit.setText("Edit Password");
                }
                else {
                    Toast.makeText(getApplicationContext(), "Update Gagal", Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            progressDialog.dismiss();
        }
    };
}
