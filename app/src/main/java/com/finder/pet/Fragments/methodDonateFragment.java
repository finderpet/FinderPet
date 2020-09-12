package com.finder.pet.Fragments;


import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.finder.pet.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class methodDonateFragment extends Fragment {

    MaterialButton btnDownloadQr;
    ImageView imgQR;
    private String urlQrBancolombia;

    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    DatabaseReference databaseRef = ref.child("urls_finder");

    public methodDonateFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_method_donate, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);

        loadImageQR();

        btnDownloadQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeDownload();
            }
        });

    }

    private void loadImageQR() {

        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("qr_bancolombia").exists()){
                    // Get firebase contact information
                    urlQrBancolombia = dataSnapshot.child("qr_bancolombia").getValue(String.class);
                    Glide.with(getContext())
                            .load(urlQrBancolombia)
                            .placeholder(R.drawable.sin_imagen)
                            .into(imgQR);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("URL code QR bancolombia", databaseError.getMessage());
            }
        });
    }

    private void executeDownload() {
        // registrer receiver in order to verify when download is complete
        getContext().registerReceiver(new DownloadCompleteReceiver(), new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(urlQrBancolombia));
        request.setDescription("Downloading...");
        request.setTitle("Code QR Bancolombia");
        //request.setMimeType("application/image");
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "QR_Bancolombia.jpg");

        DownloadManager manager = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }

    /**
     * Method to initialize the views
     * @param view View fragment
     */
    private void setupViews(View view) {

        btnDownloadQr = view.findViewById(R.id.btnDownloadQR);
        imgQR = view.findViewById(R.id.imgQR);

        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("qr_bancolombia").exists()){
                    // Get firebase contact information
                    urlQrBancolombia = dataSnapshot.child("qr_bancolombia").getValue(String.class);
                    //Log.e("URL code QR bancolombia", urlQrBancolombia);
                }else {
                    Toast.makeText(getContext(),"No encontro el codigo qr", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("URL code QR bancolombia", databaseError.getMessage());
            }
        });

    }

    /**
     * Class Download Complete Receiver
     */
    public static class DownloadCompleteReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)){
                Toast.makeText(context,R.string.download_completed, Toast.LENGTH_LONG).show();
                // DO SOMETHING WITH THIS FILE
            }
        }
    }
}
