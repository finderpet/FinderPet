package com.finder.pet.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.finder.pet.Entities.Adopted_Vo;
import com.finder.pet.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdoptedAdapter extends RecyclerView.Adapter<AdoptedAdapter.AdoptedViewHolder> implements View.OnClickListener {

    ArrayList<Adopted_Vo> listAdopted;
    private View.OnClickListener listener;
    private Context mContext;

    public AdoptedAdapter(ArrayList<Adopted_Vo> listAdopted) {
        this.listAdopted = listAdopted;
    }

    @NonNull
    @Override
    public AdoptedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from (parent.getContext()).inflate(R.layout.item_list_adopted, null, false);
        RecyclerView.LayoutParams layParams = new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layParams);

        mContext= parent.getContext();//Traer el contexto del fragment al adapter para trabajar con Glide

        view.setOnClickListener((View.OnClickListener) this);
        return new AdoptedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdoptedViewHolder holder, int position) {
        holder.txtNombre.setText(listAdopted.get(position).getName()); //para traer un entero colocar .toString
        //holder.txtDescrip.setText(listFoods.get(position).getDescription());
        holder.txtTipo.setText("Ubicación: "+listAdopted.get(position).getType());
        holder.txtTiempo.setText("Email: "+listAdopted.get(position).getTime());
        holder.txtPrecio.setText("Teléfono: "+listAdopted.get(position).getPrice());
        String url = listAdopted.get(position).getImage();
        Glide.with(mContext)
                .load(url)
                .placeholder(R.drawable.sin_imagen)
                .into(holder.foto);

    }

    @Override
    public int getItemCount() {
        return listAdopted.size();
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

    public class AdoptedViewHolder extends RecyclerView.ViewHolder {

        TextView txtNombre, txtTipo, txtPrecio, txtTiempo;
        ImageView foto;

        public AdoptedViewHolder(View itemView) {
            super(itemView);
            txtNombre=itemView.findViewById(R.id.name_adopted);
            //txtDescrip=itemView.findViewById(R.id.id_nam)
            txtTipo=itemView.findViewById(R.id.address_adopted);
            txtPrecio=itemView.findViewById(R.id.phone_adopted);
            txtTiempo=itemView.findViewById(R.id.email_adopted);
            foto=itemView.findViewById(R.id.img_adopted);
        }
    }
}

