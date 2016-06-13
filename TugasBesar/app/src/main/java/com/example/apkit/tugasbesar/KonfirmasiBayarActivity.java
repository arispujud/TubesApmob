package com.example.apkit.tugasbesar;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;

public class KonfirmasiBayarActivity extends AppCompatActivity {

    private Spinner spnVia;
    int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    ImageView ivImage;
    Button btnImage, btnSubmit;
    TextView txtUrlGambar;
    EditText txtJumlah,txtKeterangan;
    String picturePath="";
    public ProgressDialog progressDialog;
    public UrlConfig myUrl = new UrlConfig();
    long totalSize = 0;
    String via,jumlah,keterangan;
    String file;

    SessionManager sessionManager = new SessionManager();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_konfirmasi_bayar);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        spnVia = (Spinner) findViewById(R.id.spn_konfirmasi_via);
        ivImage = (ImageView) findViewById(R.id.img_konfirmasi_bukti);
        btnImage = (Button) findViewById(R.id.btn_konfirmasi_bukti);
        btnSubmit = (Button) findViewById(R.id.btn_konfirmasi_submit);
        txtUrlGambar = (TextView) findViewById(R.id.lbl_konfirmasi_url_gambar);
        txtJumlah = (EditText) findViewById(R.id.txt_konfirmasi_jumlah);
        txtKeterangan = (EditText) findViewById(R.id.txt_konfirmasi_keterangan);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.list_via, android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnVia.setAdapter(adapter);

        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                via = spnVia.getSelectedItem().toString();
                jumlah = txtJumlah.getText().toString();
                keterangan = txtKeterangan.getText().toString();
                new UploadFileToServer().execute();
            }
        });

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.

        savedInstanceState.putString("PICTURE_PATH", picturePath);
        // etc.
        super.onSaveInstanceState(savedInstanceState);
    }
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        picturePath = savedInstanceState.getString("PICTURE_PATH");
        txtUrlGambar.setText(picturePath);
        previewGambar(picturePath);
    }
    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(KonfirmasiBayarActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Mayers/";
                    File newdir = new File(dir);
                    newdir.mkdirs();
                    file = dir+System.currentTimeMillis() + ".jpg";
                    File newfile = new File(file);
                    try {
                        newfile.createNewFile();
                    } catch (IOException e) {

                    }
                    Uri outputFileUri = Uri.fromFile(newfile);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,outputFileUri);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"), SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                //Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                //Bitmap resized = Bitmap.createScaledBitmap(thumbnail, 500, 500, true);
//                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
//                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//                thumbnail.compress(Bitmap.CompressFormat.JPEG,100,bytes);
//                String namaFile= System.currentTimeMillis() + ".jpg";
//                File destination = new File(Environment.getExternalStorageDirectory(),namaFile);
//                FileOutputStream fo;
//                try {
//                    destination.createNewFile();
//                    fo = new FileOutputStream(destination);
//                    fo.write(bytes.toByteArray());
//                    fo.close();
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                picturePath = Environment.getExternalStorageDirectory().toString()+File.separator+namaFile;
//                //picturePath = data.getExtras().get("data").toString();
//                ivImage.setImageBitmap(thumbnail);
//                txtUrlGambar.setText(picturePath);
                String selectedImagePath = file;
                picturePath = selectedImagePath;

                Bitmap bm;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(selectedImagePath, options);
                final int REQUIRED_SIZE = 200;
                int scale = 1;
                while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                        && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                    scale *= 2;
                options.inSampleSize = scale;
                options.inJustDecodeBounds = false;
                bm = BitmapFactory.decodeFile(selectedImagePath, options);

                ivImage.setImageBitmap(bm);
                txtUrlGambar.setText(picturePath);


            } else if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                String[] projection = {MediaStore.MediaColumns.DATA};
                CursorLoader cursorLoader = new CursorLoader(this, selectedImageUri, projection, null, null, null);
                Cursor cursor = cursorLoader.loadInBackground();
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();

                String selectedImagePath = cursor.getString(column_index);
                picturePath = selectedImagePath;

                Bitmap bm;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(selectedImagePath, options);
                final int REQUIRED_SIZE = 200;
                int scale = 1;
                while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                        && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                    scale *= 2;
                options.inSampleSize = scale;
                options.inJustDecodeBounds = false;
                bm = BitmapFactory.decodeFile(selectedImagePath, options);

                ivImage.setImageBitmap(bm);
                txtUrlGambar.setText(picturePath);
            }
        }
    }

    /**
     * Uploading the file to server
     * */
    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {

        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            //progressBar.setProgress(0);
            showProgressDialog("","Please Wait!! \nUploading File to Server!");
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
            //progressBar.setVisibility(View.VISIBLE);

            // updating progress bar value
            //progressBar.setProgress(progress[0]);

            // updating percentage value
            //txtPercentage.setText(String.valueOf(progress[0]) + "%");
            //progressDialog.setProgress(progress[0]);
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(UrlConfig.KONFIRMASI_URL);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });
                if(picturePath=="") {

                }
                else{
                    File sourceFile = new File(picturePath);

                    // Adding file data to http body
                    entity.addPart("image", new FileBody(sourceFile));
                }
                // Extra parameters if you want to pass to server
                entity.addPart("api_key", new StringBody(sessionManager.getPreferences(KonfirmasiBayarActivity.this,"ACCESS_TOKEN")));
                entity.addPart("via", new StringBody(via));
                entity.addPart("jumlah", new StringBody(jumlah));
                entity.addPart("ket", new StringBody(keterangan));
                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;

        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            showAlert(result);
            super.onPostExecute(result);
        }
    }

    private void showProgressDialog(String judul, String isi){
        progressDialog = progressDialog.show(this,judul,isi);
    }

    private void closeProgressDialog(){
        progressDialog.dismiss();
    }
    /**
     * Method to show alert dialog
     * */
    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        message="Konfirmasi pembayaran berhasil. Silahkan tunggu verifikasi admin.";
        builder.setMessage(message).setTitle("Response from Servers")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(KonfirmasiBayarActivity.this,MainActivity.class));
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void previewGambar(String url){
        Bitmap bm;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(url, options);
        final int REQUIRED_SIZE = 200;
        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                && options.outHeight / scale / 2 >= REQUIRED_SIZE)
            scale *= 2;
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(url, options);

        ivImage.setImageBitmap(bm);
    }
}
