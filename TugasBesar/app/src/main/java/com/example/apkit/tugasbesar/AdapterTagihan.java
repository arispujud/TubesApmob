package com.example.apkit.tugasbesar;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by APKIT on 4/7/2016.
 */
public class AdapterTagihan extends ArrayAdapter<DetailListOrder> {
    public Activity context;
    public int textViewResourceId;
    public AdapterTagihan(Activity context, int textViewResourceId){
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
            viewholder.subject=(TextView) view.findViewById(R.id.txt_tagihan_Subject);
            viewholder.jumlah=(TextView) view.findViewById(R.id.txt_tagihan_jumlah);
            viewholder.harga=(TextView) view.findViewById(R.id.txt_tagihan_harga);
            view.setTag(viewholder);
        }
        else{
            view = convertView;
        }
        ViewHolder viewHolder=(ViewHolder) view.getTag();
        final DetailListOrder detail = getItem(position);
        viewHolder.subject.setText(detail.getSubject());
        viewHolder.harga.setText("Harga : Rp." + detail.getHarga() +",-");
        viewHolder.jumlah.setText("Jumlah : "+detail.getJumlah());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getContext(),DetailOrderActivity.class);
                i.putExtra("ID_ORDER", detail.getIdOrder().toString());
                context.startActivity(i);
            }
        });
        return view;
    }
    static class ViewHolder{
        public TextView subject;
        public TextView jumlah;
        public TextView harga;
    }

}
