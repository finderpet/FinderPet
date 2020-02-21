package com.finder.pet.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.finder.pet.Entities.Found_Vo;
import com.finder.pet.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FoundAdapter extends RecyclerView.Adapter<FoundAdapter.FoundViewHolder>  implements View.OnClickListener{

    ArrayList<Found_Vo> listFound;
    private View.OnClickListener listener;
    private Context mContext;

    public FoundAdapter(ArrayList<Found_Vo> listFound) {
        this.listFound = listFound;
    }

    @NonNull
    @Override
    public FoundViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from (parent.getContext()).inflate(R.layout.item_list_found, null, false);
        RecyclerView.LayoutParams layParams = new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layParams);

        mContext= parent.getContext();//Traer el contexto del fragment al adapter para trabajar con Glide

        view.setOnClickListener((View.OnClickListener) this);
        return new FoundViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoundViewHolder holder, int position) {
        holder.txtNombre.setText(listFound.get(position).getName()); //para traer un entero colocar .toString
        //holder.txtDescrip.setText(listFoods.get(position).getDescription());
        holder.txtTipo.setText("Tipo comida: "+listFound.get(position).getType());
        holder.txtTiempo.setText("Tiempo preparación: "+listFound.get(position).getTime()+" min.");
        holder.txtPrecio.setText("Precio: $"+listFound.get(position).getPrice());
        String url = listFound.get(position).getImage();
        Glide.with(mContext)
                .load(url)
                .placeholder(R.drawable.sin_imagen)
                .into(holder.foto);

    }

    @Override
    public int getItemCount() {
        return listFound.size();
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

    public class FoundViewHolder extends RecyclerView.ViewHolder {

        TextView txtNombre, txtTipo, txtPrecio, txtTiempo;
        ImageView foto;

        public FoundViewHolder(View itemView) {
            super(itemView);
            txtNombre=itemView.findViewById(R.id.id_name_listPlate);
            //txtDescrip=itemView.findViewById(R.id.id_nam)
            txtTipo=itemView.findViewById(R.id.id_type_listPlate);
            txtPrecio=itemView.findViewById(R.id.id_price_listPlate);
            txtTiempo=itemView.findViewById(R.id.id_time_listPlate);
            foto=itemView.findViewById(R.id.id_photo_listPlate);
        }
    }
}
