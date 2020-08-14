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
import androidx.core.text.BidiFormatter;
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

        mContext= parent.getContext();// Get the context of the fragment to the adapter



        view.setOnClickListener((View.OnClickListener) this);
        return new AdoptedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdoptedViewHolder holder, int position) {

        String name = mContext.getString(R.string.post_name).concat(listAdopted.get(position).getName());
        holder.txtName.setText(name);
        String age = mContext.getString(R.string.post_age).concat(listAdopted.get(position).getAge());
        holder.txtAge.setText(age);
        String posted = mContext.getString(R.string.post_posted).concat(listAdopted.get(position).getDate());
        holder.txtTimePost.setText(posted);
        String phone = mContext.getString(R.string.post_phone).concat(listAdopted.get(position).getPhone());
        holder.txtPhone.setText(phone);
        String url = listAdopted.get(position).getImage1();
        Glide.with(mContext)
                .load(url)
                .placeholder(R.drawable.sin_imagen)
                .centerCrop()// center the image and take up the entire imageView space
                .into(holder.mPhoto);

        if (listAdopted.get(position).getType().equals("dog")){
            holder.markType.setImageResource(R.mipmap.ic_dog);
        }
        if (listAdopted.get(position).getType().equals("cat")){
            holder.markType.setImageResource(R.mipmap.ic_cat);
        }
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

        TextView txtName, txtTimePost, txtAge, txtPhone;
        ImageView mPhoto, markType;

        public AdoptedViewHolder(View itemView) {
            super(itemView);
            txtName=itemView.findViewById(R.id.nameAdopted);
            txtTimePost =itemView.findViewById(R.id.timePostAdopted);
            txtAge =itemView.findViewById(R.id.AgeAdopted);
            txtPhone=itemView.findViewById(R.id.phoneContactAdopted);
            mPhoto=itemView.findViewById(R.id.imgAdopted);
            markType=itemView.findViewById(R.id.markAdopted);
        }
    }
}

