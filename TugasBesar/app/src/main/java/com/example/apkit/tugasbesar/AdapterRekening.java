package com.example.apkit.tugasbesar;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;

/**
 * Created by APKIT on 4/23/2016.
 */
public class AdapterRekening extends ArrayAdapter<DetailRekening> {
    public Activity context;
    public int textViewResourceId;
    public AdapterRekening(Activity context, int textViewResourceId){
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
            viewholder.txtNoRekening=(TextView) view.findViewById(R.id.txt_bayar_norek);
            viewholder.txtNamaRekening=(TextView) view.findViewById(R.id.txt_bayar_namarek);
            viewholder.txtKantorRekening=(TextView) view.findViewById(R.id.txt_bayar_kantorrek);
            viewholder.imgLogoBank = (SmartImageView) view.findViewById(R.id.img_bayar_logo);
            view.setTag(viewholder);
        }
        else{
            view = convertView;
        }
        ViewHolder viewHolder=(ViewHolder) view.getTag();
        final DetailRekening detail = getItem(position);
        viewHolder.txtNoRekening.setText(detail.getNoRekening());
        viewHolder.txtNamaRekening.setText("a/n." + detail.getNamaRekening());
        viewHolder.txtKantorRekening.setText(detail.getKantorRekening());
        viewHolder.imgLogoBank.setImageUrl(detail.getUrlRekening());

//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i=new Intent(getContext(),DetailOrderActivity.class);
//                i.putExtra("ID_ORDER", detail.getIdOrder().toString());
//                context.startActivity(i);
//            }
//        });
        return view;
    }
    static class ViewHolder{
        public TextView txtNoRekening;
        public TextView txtNamaRekening;
        public TextView txtKantorRekening;
        public SmartImageView imgLogoBank;
    }
}
