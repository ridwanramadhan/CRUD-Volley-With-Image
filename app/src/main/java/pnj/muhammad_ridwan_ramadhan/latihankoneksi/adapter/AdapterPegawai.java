package pnj.muhammad_ridwan_ramadhan.latihankoneksi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import pnj.muhammad_ridwan_ramadhan.latihankoneksi.R;
import pnj.muhammad_ridwan_ramadhan.latihankoneksi.model.ModelPegawai;

public class AdapterPegawai extends ArrayAdapter<ModelPegawai> {
    Context context;
    int resource;

    public AdapterPegawai(@NonNull Context context, int resource) {
        super(context, resource);
        this.context = context;
        this.resource = resource;
    }

    class Holder {
        TextView txtNama, txtGaji, txtJabatan;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Holder holder;

        if (convertView==null) {
            holder = new Holder();
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
            holder.txtNama = convertView.findViewById(R.id.txtNama);
            holder.txtGaji = convertView.findViewById(R.id.txtGaji);
            holder.txtJabatan = convertView.findViewById(R.id.txtJabatan);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        holder.txtNama.setText("Nama : "+getItem(position).getNama());
        holder.txtGaji.setText("Gaji : "+getItem(position).getGaji());
        holder.txtJabatan.setText("Jabatan : "+getItem(position).getJabatan());

        return convertView;
    }
}
