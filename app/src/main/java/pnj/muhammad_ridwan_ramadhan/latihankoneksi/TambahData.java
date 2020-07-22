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

public class TambahData extends AppCompatActivity {

    EditText edtNama, edtJabatan, edtGaji, edtImage;
    Button actionTambah, actionImage;
    ImageView imageView;

    public static final int REQUEST_PERMISSION = 100;
    public static final int PICK_IMAGE_REQUEST = 1;
    public static final int CAMERA_REQUEST = 2;
    private String filepath;
    private Bitmap image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_data);

        edtNama = findViewById(R.id.edtNama);
        edtJabatan = findViewById(R.id.edtJabatan);
        edtGaji = findViewById(R.id.edtGaji);
        edtImage = findViewById(R.id.edtImage);

        actionTambah = findViewById(R.id.actionTambah);
        actionImage = findViewById(R.id.actionImage);
        imageView = findViewById(R.id.image);

        actionTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtNama.getText().toString().length() >0 && edtJabatan.getText().toString().length() >0 && edtGaji.getText().toString().length() >0){
                    //insert
                    insert();
                }
            }
        });

        actionImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) &&
                        (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                        && (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {

                    ActivityCompat.requestPermissions(TambahData.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, REQUEST_PERMISSION);

                } else {

                    AlertDialog.Builder alert = new AlertDialog.Builder(TambahData.this);
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

        getSupportActionBar().setTitle("Tambah Data");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {

            Picasso.get().load(data.getData()).into(imageView);
            try {
                image = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
            } catch (IOException e) {
                Toast.makeText(TambahData.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            File file = new File(filepath);

            Uri photoURI = FileProvider.getUriForFile(this,
                    "pnj.muhammad_ridwan_ramadhan.latihankoneksi.fileprovider",
                    file);
            Picasso.get().load(photoURI).into(imageView);

            image = BitmapFactory.decodeFile(filepath);
        }
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Pilih Gambar"), PICK_IMAGE_REQUEST);
    }

     private void showCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast.makeText(TambahData.this, "" + ex.getMessage(), Toast.LENGTH_SHORT).show();
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

    private byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    void uploadBitmap(final Bitmap bitmap) {

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, Config._UPLOAD_IMAGE, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {

                try {
                    JSONObject jsonObject = new JSONObject(new String(response.data));
                    Toast.makeText(TambahData.this, jsonObject.getString("message"),
                            Toast.LENGTH_SHORT).show();

                    if (jsonObject.getString("status").equals("OK")) {
//                        finish();
                    }

                } catch (JSONException ex) {
                    Toast.makeText(TambahData.this, "" + ex.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(TambahData.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
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


    void insert() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        VolleyMultipartRequest request = new VolleyMultipartRequest(Request.Method.POST,
                Config._TAMBAH_PEGAWAI,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject jsonObject = new JSONObject(new String(response.data));
                            Toast.makeText(TambahData.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                            if (jsonObject.getString("status").equals("OK")) {
                                finish();
                            }

                        } catch (JSONException ex) {
                            Toast.makeText(TambahData.this, "" + ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(TambahData.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> parameter = new HashMap<>();
                parameter.put("name", edtNama.getText().toString());
                parameter.put("position", edtJabatan.getText().toString());
                parameter.put("salary", edtGaji.getText().toString());

                return parameter;
            }

            @Override
            protected Map<String, DataPart> getByteData() throws AuthFailureError {
                HashMap<String, DataPart> datas = new HashMap<>();
                datas.put("image", new DataPart(System.currentTimeMillis()+".jpeg", getFileDataFromDrawable(image)));
                return datas;
            }
        };

        requestQueue.add(request);
    }
}
