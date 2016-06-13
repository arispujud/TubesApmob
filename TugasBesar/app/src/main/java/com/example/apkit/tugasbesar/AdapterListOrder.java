package com.example.apkit.tugasbesar;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by APKIT on 4/1/2016.
 */
public class AdapterListOrder extends ArrayAdapter<DetailListOrder> {
    public Activity context;
    public int textViewResourceId;

    public AdapterListOrder(Activity context, int textViewResourceId){
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
            viewholder.subject=(TextView) view.findViewById(R.id.txt_list_Subject);
            viewholder.waktu=(TextView) view.findViewById(R.id.txt_list_tgl);
            viewholder.progres=(TextView) view.findViewById(R.id.txt_list_status);
            viewholder.harga=(TextView) view.findViewById(R.id.txt_list_harga);
            view.setTag(viewholder);
        }
        else{
            view = convertView;
        }
        ViewHolder viewHolder=(ViewHolder) view.getTag();
        final DetailListOrder detail = getItem(position);
        viewHolder.subject.setText(detail.getSubject());
        viewHolder.waktu.setText(detail.getTanggal()+" "+detail.getJam());
        viewHolder.progres.setText(detail.getProgres());
        viewHolder.harga.setText("Rp." + detail.getHarga() +",-");

        if(detail.getStatus().equals("0")){
            viewHolder.waktu.setBackgroundResource(R.color.blue);
        }
        if(detail.getStatus().equals("1")){
            viewHolder.waktu.setBackgroundResource(R.color.blue1);
        }
        if(detail.getStatus().equals("2")){
            viewHolder.waktu.setBackgroundResource(R.color.green);
        }
        if(detail.getStatus().equals("3")){
            viewHolder.waktu.setBackgroundResource(R.color.green2);
        }
        if(detail.getStatus().equals("4")){
            viewHolder.waktu.setBackgroundResource(R.color.yellow);
        }
        if(detail.getStatus().equals("5")){
            viewHolder.waktu.setBackgroundResource(R.color.orange);
        }
        if(detail.getStatus().equals("6")){
            viewHolder.waktu.setBackgroundResource(R.color.green1);
        }
        if(detail.getStatus().equals("6")){
            viewHolder.waktu.setBackgroundResource(R.color.green1);
        }
        if(detail.getStatus().equals("7")){
            viewHolder.waktu.setBackgroundResource(R.color.red);
        }
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
        public TextView tanggal;
        public TextView waktu;
        public TextView progres;
        public TextView harga;
    }
}
