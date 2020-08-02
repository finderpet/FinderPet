package com.finder.pet.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.finder.pet.Entities.Advert;
import com.finder.pet.Entities.Found_Vo;
import com.finder.pet.R;

import java.util.ArrayList;

public class AdvertAdapter extends RecyclerView.Adapter<AdvertAdapter.AdvertViewHolder> implements View.OnClickListener{

    ArrayList<Advert> listAdvert;
    private View.OnClickListener listener;
    private Context mContext;

    public AdvertAdapter(ArrayList<Advert> listAdvert) {
        this.listAdvert = listAdvert;
    }

    @NonNull
    @Override
    public AdvertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from (parent.getContext()).inflate(R.layout.item_list_adverts, null, false);
        RecyclerView.LayoutParams layParams = new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layParams);

        mContext= parent.getContext();//Traer el contexto del fragment al adapter para trabajar con Glide

        view.setOnClickListener((View.OnClickListener) this);
        return new AdvertViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdvertViewHolder holder, int position) {
        final int pos = position;
        holder.txtTitle.setText(listAdvert.get(position).getName());
        holder.txtDescription.setText(listAdvert.get(position).getDescription());
        holder.txtPhone.setText("Contacto: "+listAdvert.get(position).getPhone());
        //holder.txtUrl.setText("Sitio web "+listAdvert.get(position).getUrlPage());
        holder.btnUrl.setText(listAdvert.get(position).getUrlPage());
        String url = listAdvert.get(position).getImage();
        Glide.with(mContext)
                .load(url)
                .placeholder(R.drawable.sin_imagen)
                //.centerCrop()// Para centrar la imagen y que ocupe el espacio completo de imageview
                .into(holder.mPhoto);

        holder.btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Llamando...", Toast.LENGTH_SHORT).show();
//                Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+listAdvert.get(pos).getPhone()));
//                mContext.startActivity(i)
            }
        });

    }

    @Override
    public int getItemCount() {
        return listAdvert.size();
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener=listener;
    }

    @Override
    public void onClick(View view) {
        if (listener!=null){
            listener.onClick(view);
        }
    }

    public static class AdvertViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtDescription, txtPhone, txtUrl;
        ImageView mPhoto;
        Button btnUrl;
        ImageButton btnCall;
        public AdvertViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle=itemView.findViewById(R.id.titleAdvert);
            txtDescription=itemView.findViewById(R.id.descriptionAdvert);
            txtPhone =itemView.findViewById(R.id.phoneAdvert);
            //txtUrl=itemView.findViewById(R.id.urlAdvert);
            mPhoto=itemView.findViewById(R.id.imageAdvert);
            btnUrl=itemView.findViewById(R.id.btnUrlAdvert);
            btnCall=itemView.findViewById(R.id.btnCall);


        }
    }
}
