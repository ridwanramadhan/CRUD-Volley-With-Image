package pnj.muhammad_ridwan_ramadhan.latihankoneksi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pnj.muhammad_ridwan_ramadhan.latihankoneksi.adapter.AdapterPegawai;
import pnj.muhammad_ridwan_ramadhan.latihankoneksi.model.ModelPegawai;
import pnj.muhammad_ridwan_ramadhan.latihankoneksi.utils.Config;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    AdapterPegawai adapterPegawai;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        adapterPegawai = new AdapterPegawai(this, R.layout.item_list);
        listView.setAdapter(adapterPegawai);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ModelPegawai modelPegawai = (ModelPegawai) parent.getAdapter().getItem(position);

                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("id", modelPegawai.getId());
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ModelPegawai modelPegawai = (ModelPegawai) parent.getAdapter().getItem(position);
                final String id_pegawai = modelPegawai.getId();
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Hapus data pegawai")
                        .setMessage("Apakah anda yakin ingin menghapus data ini?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                delete(id_pegawai);
                            }
                        }).setNegativeButton(android.R.string.no, null).show();
                return true;
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_tambah, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.actionTambahData) {
            Intent intent = new Intent(MainActivity.this, TambahData.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        RequestQueue queue = Volley.newRequestQueue(this);
//        String url = "http://192.168.1.4/aplikasipegawai/tampilsemuapgw.php";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Config._LIST_PEGAWAI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("RESPONSE = ", ""+response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("pegawai");
                    ArrayList<ModelPegawai> datas = new ArrayList<>();

                    for (int i=0; i<jsonArray.length(); i++){
                        JSONObject item = jsonArray.getJSONObject(i);
                        ModelPegawai modelPegawai = new ModelPegawai();
                        modelPegawai.setId(item.getString("id"));
                        modelPegawai.setNama(item.getString("name"));
                        modelPegawai.setGaji(item.getString("gaji"));
                        modelPegawai.setJabatan(item.getString("position"));

                        datas.add(modelPegawai);
                    }

                    adapterPegawai.clear();
                    adapterPegawai.addAll(datas);
                    adapterPegawai.notifyDataSetChanged();

                } catch (JSONException ex) {
                    Log.e("Error : ", ""+ex.getMessage());
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("RESPONSE = ", ""+error.getMessage());
            }
        });

        queue.add(stringRequest);
    }

    void delete(String id_pegawai) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Config._DELETE_PEGAWAI+"?id="
                +id_pegawai,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("RESPONSE",""+response);

                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.getString("status").equals("OK")){
                                Toast.makeText(MainActivity.this, jsonObject.getString("message"),Toast.LENGTH_SHORT).show();
                                onResume();
                            }else{
                                Toast.makeText(MainActivity.this, jsonObject.getString("message"),Toast.LENGTH_SHORT).show();
                            }
                        }catch (JSONException ex){
                            Toast.makeText(MainActivity.this, "" + ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> parameter = new HashMap<>();
                parameter.put("id", getIntent().getExtras().getString("id", "0"));

                return parameter;
            }
        };

        requestQueue.add(stringRequest);
    }
}
