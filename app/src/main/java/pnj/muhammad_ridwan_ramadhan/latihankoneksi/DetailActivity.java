package pnj.muhammad_ridwan_ramadhan.latihankoneksi;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import pnj.muhammad_ridwan_ramadhan.latihankoneksi.utils.Config;
import pnj.muhammad_ridwan_ramadhan.latihankoneksi.utils.VolleyMultipartRequest;

public class DetailActivity extends AppCompatActivity {

    EditText edtNama, edtJabatan, edtGaji, edtImage;
    Button actionUpdate, actionImage;
    ImageView imageView;

    public static final int REQUEST_PERMISSION = 100;
    public static final int PICK_IMAGE_REQUEST = 1;
    public static final int CAMERA_REQUEST = 2;
    String filepath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        edtNama = findViewById(R.id.edtNama);
        edtJabatan = findViewById(R.id.edtJabatan);
        edtGaji = findViewById(R.id.edtGaji);
        edtImage = findViewById(R.id.edtImage);

        actionUpdate = findViewById(R.id.actionUpdate);
        imageView = findViewById(R.id.image);
        actionImage = findViewById(R.id.actionImage);

        getData();

        actionUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edtNama.getText().toString().length() > 0 &&
                        edtGaji.getText().toString().length() > 0 &&
                        edtJabatan.getText().toString().length() > 0) {
                    update();
                }
            }
        });


        actionImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) &&
                        (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                        && (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {

                    ActivityCompat.requestPermissions(DetailActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, REQUEST_PERMISSION);

                } else {

                    AlertDialog.Builder alert = new AlertDialog.Builder(DetailActivity.this);
                    alert.setItems(new String[]{"Pilih Gambar", "Camera"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                showFileChooser();
                            } else {
                                showCamera();
                            }
                        }
                    });
                    alert.show();

                }
            }
        });

        getSupportActionBar().setTitle("Detail Pegawai");

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {

            Picasso.get().load(data.getData()).into(imageView);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                uploadImage(bitmap);
            } catch (IOException e) {
                Toast.makeText(DetailActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            File file = new File(filepath);

            Uri photoURI = FileProvider.getUriForFile(this,
                    "pnj.muhammad_ridwan_ramadhan.latihankoneksi.fileprovider",
                    file);
            Picasso.get().load(photoURI).into(imageView);

            Bitmap bitmap = BitmapFactory.decodeFile(filepath);
            uploadBitmap(bitmap);
        }
    }

    void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Pilih Gambar"), PICK_IMAGE_REQUEST);
    }

    void showCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast.makeText(DetailActivity.this, "" + ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "pnj.muhammad_ridwan_ramadhan.latihankoneksi.fileprovider",
                        photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST);
            }
        }

    }


    //uri://
    String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,
                MediaStore.Images.Media._ID + "=?", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();
        return path;
    }

    byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    void getData() {

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                Config._DETAIL_PEGAWAI + "?id=" +
                        getIntent().getExtras().getString("id", "0"),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            edtNama.setText(jsonObject.getString("name"));
                            edtJabatan.setText(jsonObject.getString("position"));
                            edtGaji.setText(jsonObject.getString("salary"));
                            if (!jsonObject.getString("image").equals("")) {
                                Picasso.get().load(jsonObject.getString("image")).into(imageView);
                            }

                        } catch (JSONException e) {
                            Toast.makeText(DetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(DetailActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        requestQueue.add(stringRequest);


    }

    void update() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Config._UPDATE_PEGAWAI,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(DetailActivity.this, jsonObject.getString("message"),
                                    Toast.LENGTH_SHORT).show();

                            if (jsonObject.getString("status").equals("OK")) {
                                finish();
                            }


                        } catch (JSONException e) {
                            Toast.makeText(DetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(DetailActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> parameter = new HashMap<>();
                parameter.put("id", getIntent().getExtras().getString("id", "0"));
                parameter.put("name", edtNama.getText().toString());
                parameter.put("position", edtJabatan.getText().toString());
                parameter.put("salary", edtGaji.getText().toString());

                return parameter;
            }
        };

        requestQueue.add(stringRequest);
    }

    void uploadBitmap(final Bitmap bitmap) {

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, Config._UPLOAD_IMAGE, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {

                try {
                    JSONObject jsonObject = new JSONObject(new String(response.data));
                    Toast.makeText(DetailActivity.this, jsonObject.getString("message"),
                            Toast.LENGTH_SHORT).show();

                    if (jsonObject.getString("status").equals("OK")) {
//                        finish();
                    }

                } catch (JSONException ex) {
                    Toast.makeText(DetailActivity.this, "" + ex.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(DetailActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> parameter = new HashMap<>();
                parameter.put("id", getIntent().getExtras().getString("id", ""));
                return parameter;
            }

            @Override
            protected Map<String, DataPart> getByteData() throws AuthFailureError {
                HashMap<String, DataPart> data = new HashMap<>();
                data.put("image", new DataPart("image" + System.currentTimeMillis() + ".jpeg", getFileDataFromDrawable(bitmap)));
                return data;
            }
        };

        requestQueue.add(volleyMultipartRequest);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        filepath = image.getAbsolutePath();
        return image;
    }


    void uploadImage(final Bitmap bitmap) {

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        VolleyMultipartRequest request = new VolleyMultipartRequest(Request.Method.POST,
                Config._UPLOAD_IMAGE,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {

                        Log.e("Response", ""+new String(response.data));

                        try {
                            JSONObject jsonObject = new JSONObject(new String(response.data));
                            Toast.makeText(DetailActivity.this, jsonObject.getString("message"),
                                    Toast.LENGTH_SHORT).show();
                        } catch (JSONException ex) {
                            Toast.makeText(DetailActivity.this, ex.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(DetailActivity.this, error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> data = new HashMap<>();
                data.put("id", getIntent().getExtras().getString("id",""));
                return data;
            }

            @Override
            protected Map<String, DataPart> getByteData() throws AuthFailureError {
                HashMap<String, DataPart> datas = new HashMap<>();
                datas.put("image", new DataPart(System.currentTimeMillis()+".jpeg", getFileDataFromDrawable(bitmap)));
                return datas;
            }
        };

        requestQueue.add(request);

    }
}
