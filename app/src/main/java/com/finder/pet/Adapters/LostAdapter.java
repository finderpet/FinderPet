package com.finder.pet.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.finder.pet.Entities.Lost_Vo;
import com.finder.pet.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LostAdapter extends RecyclerView.Adapter<LostAdapter.LostViewHolder> implements View.OnClickListener {

    ArrayList<Lost_Vo> listLost;
    private View.OnClickListener listener;
    private Context mContext;

    public LostAdapter(ArrayList<Lost_Vo> listLost) {
        this.listLost = listLost;
    }

    @NonNull
    @Override
    public LostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from (parent.getContext()).inflate(R.layout.item_list_lost, null, false);
        RecyclerView.LayoutParams layParams = new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layParams);

        mContext= parent.getContext();//Traer el contexto del fragment al adapter para trabajar con Glide

        view.setOnClickListener((View.OnClickListener) this);
        return new LostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LostViewHolder holder, int position) {
        holder.txtName.setText("Nombre: "+listLost.get(position).getName()); //para traer un entero colocar .toString
        holder.txtLocation.setText(listLost.get(position).getLocation());
        holder.txtPhone.setText("Teléfono: "+listLost.get(position).getPhone());
        holder.txtTimePost.setText("Publicado "+listLost.get(position).getDate());
        String url = listLost.get(position).getImage1();
        Glide.with(mContext)
                .load(url)
                .placeholder(R.drawable.sin_imagen)
                .centerCrop()// Para centrar la imagen y que ocupe el espacio completo de imageview
                .into(holder.mPhoto);

        if (listLost.get(position).getType().equals("Perro")){
            holder.markType.setImageResource(R.mipmap.ic_dog);
        }
        if (listLost.get(position).getType().equals("Gato")){
            holder.markType.setImageResource(R.mipmap.ic_cat);
        }

    }

    @Override
    public int getItemCount() {
        return listLost.size();
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

    public class LostViewHolder extends RecyclerView.ViewHolder {

        TextView txtName,  txtLocation, txtTimePost, txtPhone;
        ImageView mPhoto, markType;

        public LostViewHolder(View itemView) {
            super(itemView);
            txtName=itemView.findViewById(R.id.nameLost);
            txtLocation=itemView.findViewById(R.id.locationLost);
            txtTimePost =itemView.findViewById(R.id.timePostLost);
            txtPhone=itemView.findViewById(R.id.phoneContactLost);
            mPhoto=itemView.findViewById(R.id.imgLost);
            markType=itemView.findViewById(R.id.markLost);
        }
    }
}
