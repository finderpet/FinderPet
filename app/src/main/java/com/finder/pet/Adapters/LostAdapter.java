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
        holder.txtType.setText("Tipo mascota: "+listLost.get(position).getType()); //para traer un entero colocar .toString
        holder.txtLocation.setText("Dirección: "+listLost.get(position).getLocation());
        holder.txtEmail.setText("Correo: "+listLost.get(position).getEmail());
        holder.txtPhone.setText("Teléfono: "+listLost.get(position).getPhone());
        //holder.txtObservation.setText("Descripción: "+listFound.get(position).getFound_description());
        String url = listLost.get(position).getImage1();
        Glide.with(mContext)
                .load(url)
                .placeholder(R.drawable.sin_imagen)
                .centerCrop()// Para centrar la imagen y que ocupe el espacio completo de imageview
                .into(holder.mPhoto);

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

        TextView txtName, txtType, txtLocation, txtEmail, txtPhone, txtObservation;
        ImageView mPhoto;

        public LostViewHolder(View itemView) {
            super(itemView);
            txtType=itemView.findViewById(R.id.typeLost);
            txtLocation=itemView.findViewById(R.id.locationLost);
            txtEmail=itemView.findViewById(R.id.emailContactLost);
            txtPhone=itemView.findViewById(R.id.phoneContactLost);
            //txtObservation=itemView.findViewById(R.id.observationsContactFound);
            mPhoto=itemView.findViewById(R.id.imgLost);
        }
    }
}
