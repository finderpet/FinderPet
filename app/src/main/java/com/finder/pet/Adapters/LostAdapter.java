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
        holder.txtNombre.setText(listLost.get(position).getName()); //para traer un entero colocar .toString
        //holder.txtDescrip.setText(listFoods.get(position).getDescription());
        holder.txtTipo.setText("Ubicación: "+listLost.get(position).getType());
        holder.txtTiempo.setText("Email: "+listLost.get(position).getTime());
        holder.txtPrecio.setText("Teléfono: "+listLost.get(position).getPrice());
        String url = listLost.get(position).getImage();
        Glide.with(mContext)
                .load(url)
                .placeholder(R.drawable.sin_imagen)
                .into(holder.foto);

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

        TextView txtNombre, txtTipo, txtPrecio, txtTiempo;
        ImageView foto;

        public LostViewHolder(View itemView) {
            super(itemView);
            txtNombre=itemView.findViewById(R.id.name_lost);
            //txtDescrip=itemView.findViewById(R.id.id_nam)
            txtTipo=itemView.findViewById(R.id.address_lost);
            txtPrecio=itemView.findViewById(R.id.phone_lost);
            txtTiempo=itemView.findViewById(R.id.email_lost);
            foto=itemView.findViewById(R.id.img_lost);
        }
    }
}
