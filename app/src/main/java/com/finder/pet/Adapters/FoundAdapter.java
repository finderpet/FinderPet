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

        mContext= parent.getContext();// Get the context of the fragment to the adapter

        view.setOnClickListener((View.OnClickListener) this);
        return new FoundViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoundViewHolder holder, int position) {

        String type;
        if (listFound.get(position).getType().equals("dog")){
            type = mContext.getString(R.string.post_type).concat(mContext.getString(R.string.dog));
        }else if (listFound.get(position).getType().equals("cat")){
            type = mContext.getString(R.string.post_type).concat(mContext.getString(R.string.cat));
        }else {
            type = mContext.getString(R.string.post_type).concat(mContext.getString(R.string.other));
        }
        holder.txtType.setText(type);
        String location = mContext.getString(R.string.post_location_found).concat(listFound.get(position).getLocation());
        holder.txtLocation.setText(location);
        String phone = mContext.getString(R.string.post_phone).concat(listFound.get(position).getPhone());
        holder.txtPhone.setText(phone);
        String posted = mContext.getString(R.string.post_posted).concat(listFound.get(position).getDate());
        holder.txtTimePost.setText(posted);
        String url = listFound.get(position).getImage1();
        Glide.with(mContext)
                .load(url)
                .placeholder(R.drawable.sin_imagen)
                .centerCrop()// center the image and take up the entire imageView space
                .into(holder.mPhoto);

        if (listFound.get(position).getType().equals("dog")){
            holder.markType.setImageResource(R.mipmap.ic_dog);
        }
        if (listFound.get(position).getType().equals("cat")){
            holder.markType.setImageResource(R.mipmap.ic_cat);
        }
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

        TextView txtType, txtLocation, txtTimePost, txtPhone;
        ImageView mPhoto, markType;

        public FoundViewHolder(View itemView) {
            super(itemView);
            txtType=itemView.findViewById(R.id.typeFound);
            txtLocation=itemView.findViewById(R.id.locationFound);
            txtTimePost =itemView.findViewById(R.id.timePostFound);
            txtPhone=itemView.findViewById(R.id.phoneContactFound);
            mPhoto=itemView.findViewById(R.id.imgFound);
            markType=itemView.findViewById(R.id.markFound);
        }
    }
}
