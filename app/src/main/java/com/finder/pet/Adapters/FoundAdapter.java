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
        holder.txtType.setText("Tipo mascota: "+listFound.get(position).getFound_type()); //para traer un entero colocar .toString
        holder.txtLocation.setText("Dirección: "+listFound.get(position).getFound_address());
        holder.txtEmail.setText("Correo: "+listFound.get(position).getFound_email());
        holder.txtPhone.setText("Teléfono: "+listFound.get(position).getFound_phone());
        //holder.txtObservation.setText("Descripción: "+listFound.get(position).getFound_description());
        String url = listFound.get(position).getFound_image();
        Glide.with(mContext)
                .load(url)
                .placeholder(R.drawable.sin_imagen)
                .into(holder.mPhoto);

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

        TextView txtType, txtLocation, txtEmail, txtPhone, txtObservation;
        ImageView mPhoto;

        public FoundViewHolder(View itemView) {
            super(itemView);
            txtType=itemView.findViewById(R.id.typeFound);
            txtLocation=itemView.findViewById(R.id.locationFound);
            txtEmail=itemView.findViewById(R.id.emailContactFound);
            txtPhone=itemView.findViewById(R.id.phoneContactFound);
            //txtObservation=itemView.findViewById(R.id.observationsContactFound);
            mPhoto=itemView.findViewById(R.id.imgFound);
        }
    }
}
