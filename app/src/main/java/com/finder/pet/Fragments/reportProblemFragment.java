package com.finder.pet.Fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.finder.pet.Authentication.LoginActivity;
import com.finder.pet.Entities.ReportProblem;
import com.finder.pet.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import static android.app.Activity.RESULT_OK;
import static androidx.navigation.Navigation.findNavController;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link reportProblemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class reportProblemFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextInputLayout inputTitle, inputEmail, inputDescription;
    private TextInputEditText fieldTitle, fieldEmail, fieldDescription;
    private Button btnSendReport;
    private ImageView imgError;
    private LinearLayout.LayoutParams params;
    private Uri imgErrorUri;
    boolean loadImg = false;
    private ProgressDialog progressDialog;
    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference databaseRef = ref.child("report_problem");
    private String urlImageError;


    public reportProblemFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static reportProblemFragment newInstance(String param1, String param2) {
        reportProblemFragment fragment = new reportProblemFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_report_problem, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupViews(view);

        imgError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadImage();
            }
        });
        btnSendReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog();
                if (formValidate()) {
                    progressDialog.dismiss();
                    return;
                }
                if (loadImg){
                    sendReportWithImage();
                }else {

                    sendReportWithoutImage();
                }
            }
        });
    }


    private void setupViews(View view) {

        imgError = view.findViewById(R.id.imgError);
        inputTitle = view.findViewById(R.id.textInputProblemTitle);
        inputEmail = view.findViewById(R.id.textInputProblemMail);
        inputDescription = view.findViewById(R.id.textInputProblemDescription);
        fieldTitle = view.findViewById(R.id.fieldProblemTitle);
        fieldEmail = view.findViewById(R.id.fieldProblemMail);
        fieldDescription = view.findViewById(R.id.fieldProblemDescription);
        btnSendReport = view.findViewById(R.id.btnSendReport);
        progressDialog = new ProgressDialog(getContext());
    }


    private void sendReportWithoutImage() {
        urlImageError = "No image";
        uploadReportToDataBase();
    }

    private void sendReportWithImage() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        final StorageReference imgReport = storageRef.child("report_problem/"+imgErrorUri.getLastPathSegment());
        UploadTask uploadTask = imgReport.putFile(imgErrorUri);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                // Continue with the task to get the download URL
                return imgReport.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    urlImageError = task.getResult().toString();
                    uploadReportToDataBase();
                } else {
                    // Handle failures
                    Toast.makeText(getContext(), R.string.image_not_loaded,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    private void loadImage(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, 10);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10 && resultCode == RESULT_OK) {
            //Bitmap thumbnail = data.getParcelableExtra("data");
            imgErrorUri = data.getData();
            // Do work with photo saved at fullPhotoUri
            imgError.setImageURI(imgErrorUri);
            imgError.setPadding(0,0,0,0);
            loadImg = true;
        }
    }

    private void uploadReportToDataBase(){

        String title = fieldTitle.getText().toString().trim();
        String email = fieldEmail.getText().toString().trim();
        String description = fieldDescription.getText().toString().trim();
        String image = urlImageError;

        //getting a unique id using push().getKey() method
        //it will create a unique id and we will use it as the Primary Key for our user
        String id = databaseRef.push().getKey();

        ReportProblem reportProblem = new ReportProblem(title, email, description, image);

        //Saving the lost pet
        databaseRef.child(id).setValue(reportProblem);

        clearFields();

        progressDialog.dismiss();
        //displaying a success dialog
        dialogReportSentCorrectly().show();
    }

    private boolean formValidate() {
        boolean valid = true;

        String title = fieldTitle.getText().toString();
        if (TextUtils.isEmpty(title)) {
            inputTitle.setError(getString(R.string.required_field));
            valid = false;
        } else {
            inputTitle.setError(null);
        }
        String email = fieldEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            inputEmail.setError(getString(R.string.required_field));
            valid = false;
        } else {
            inputEmail.setError(null);
        }
        String description = fieldDescription.getText().toString();
        if (TextUtils.isEmpty(description)) {
            inputDescription.setError(getString(R.string.required_field));
            valid = false;
        } else {
            inputDescription.setError(null);
        }
        return !valid;
    }

    private void clearFields(){
        fieldTitle.setText("");
        fieldEmail.setText("");
        fieldDescription.setText("");
        imgError.setImageResource(R.drawable.ic_baseline_image_search_24);
        imgError.setPadding(40,40,40,40);

    }

    private void showProgressDialog(){
        progressDialog.setCancelable(false);
        //progressDialog.setTitle("Guardando nuevo registro..."); // usamos esta linea cuando no utilizamos el setContenView
        progressDialog.show();
        progressDialog.setContentView(R.layout.layout_dialog_report); // No usamos esta linea cuando usamos setTitle
    }

    /**
     * Method to display the report sent correctly dialog
     * @return Window dialog
     */
    public AlertDialog dialogReportSentCorrectly(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle(R.string.dialog_report_sent_correctly_title)
                .setMessage(R.string.dialog_report_sent_correctly_message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        return builder.create();
    }


}