package com.finder.pet.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.finder.pet.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FoundAdapter extends RecyclerView.Adapter<FoundAdapter.FoundViewHolder> {

    ArrayList<String> listFound;

    public FoundAdapter(ArrayList<String> listFound) {
        this.listFound = listFound;
    }

    @NonNull
    @Override
    public FoundViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from (parent.getContext()).inflate(R.layout.item_list_found, null, false);
        return new FoundViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoundViewHolder holder, int position) {
        holder.assignData(listFound.get(position));

    }

    @Override
    public int getItemCount() {
        return listFound.size();
    }

    public class FoundViewHolder extends RecyclerView.ViewHolder {

        TextView dato;

        public FoundViewHolder(@NonNull View itemView) {
            super(itemView);
            dato = itemView.findViewById(R.id.id_name_listPlate);
        }

        public void assignData(String s) {
            dato.setText(s);
        }
    }
}
