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
        holder.txtType.setText("Tipo mascota: "+listAdopted.get(position).getType()); //para traer un entero colocar .toString
        holder.txtLocation.setText("Dirección: "+listAdopted.get(position).getLocation());
        holder.txtEmail.setText("Correo: "+listAdopted.get(position).getEmail());
        holder.txtPhone.setText("Teléfono: "+listAdopted.get(position).getPhone());
        //holder.txtObservation.setText("Descripción: "+listFound.get(position).getFound_description());
        String url = listAdopted.get(position).getImage1();
        Glide.with(mContext)
                .load(url)
                .placeholder(R.drawable.sin_imagen)
                .centerCrop()// Para centrar la imagen y que ocupe el espacio completo de imageview
                .into(holder.mPhoto);

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

        TextView txtName, txtType, txtLocation, txtEmail, txtPhone, txtObservation;
        ImageView mPhoto;

        public AdoptedViewHolder(View itemView) {
            super(itemView);
            txtType=itemView.findViewById(R.id.typeAdopted);
            txtLocation=itemView.findViewById(R.id.locationAdopted);
            txtEmail=itemView.findViewById(R.id.emailContactAdopted);
            txtPhone=itemView.findViewById(R.id.phoneContactAdopted);
            //txtObservation=itemView.findViewById(R.id.observationsContactFound);
            mPhoto=itemView.findViewById(R.id.imgAdopted);
        }
    }
}

