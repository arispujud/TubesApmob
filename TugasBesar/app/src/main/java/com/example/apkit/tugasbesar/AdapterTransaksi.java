package com.example.apkit.tugasbesar;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by APKIT on 5/1/2016.
 */
public class AdapterTransaksi extends ArrayAdapter<DetailTransaksi> {
    public Activity context;
    public int textViewResourceId;
    public AdapterTransaksi(Activity context, int textViewResourceId){
        super(context, textViewResourceId);
        this.context=context;
        this.textViewResourceId=textViewResourceId;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View view=null;
        if (convertView==null) {
            LayoutInflater layoutinflanter = context.getLayoutInflater();
            view = layoutinflanter.inflate(textViewResourceId, null);
            ViewHolder viewholder = new ViewHolder();
            viewholder.tglTransaksi=(TextView) view.findViewById(R.id.txt_transaksi_tgl);
            viewholder.jumlahTransaksi=(TextView) view.findViewById(R.id.txt_transaksi_jumlah);
            viewholder.via=(TextView) view.findViewById(R.id.txt_transaksi_via);
            view.setTag(viewholder);
        }
        else{
            view = convertView;
        }
        ViewHolder viewHolder=(ViewHolder) view.getTag();
        final DetailTransaksi detail = getItem(position);
        viewHolder.tglTransaksi.setText(detail.getTglTransaksi());
        viewHolder.via.setText("Pembayaran melalui " + detail.getVia());
        viewHolder.jumlahTransaksi.setText("Jumlah dibayarkan : Rp "+detail.getJumlahTransfer()+",-");

        return view;
    }
    static class ViewHolder{
        public TextView tglTransaksi;
        public TextView jumlahTransaksi;
        public TextView via;
        public TextView idTransaksi;
        public TextView buktiTransfer;
    }
}
