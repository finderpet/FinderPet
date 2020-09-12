package com.finder.pet.ui.account;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.finder.pet.Main.MainActivity;
import com.finder.pet.R;
import com.finder.pet.Utilities.commonMethods;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;

public class AccountFragment extends Fragment {

    private AccountViewModel accountViewModel;

    // [START declare_auth ]
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    // [END declare_auth]
    private GoogleSignInClient mGoogleSignInClient;

    MainActivity mainActivity;

    Button btnSgnOut, btnDeleteAccount;
    ImageView iconName, iconPhone, iconPhoto;
    EditText txtName, txtEditText, txtEmail, txtPhone;
    TextView txtMsgEdit;
    private Uri imgUserUri;
    private String urlUserImg;
    private AlertDialog newDialog;
    private CircleImageView imgPhoto;
    private static final String TAG = "Update user";
    private static final int REQUEST_PERMISSIONS = 104;
    private String pathPhoto; // Photo path taken with the camera

    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference databaseRef;

    private ProgressDialog progressDialog;

    // Constants
    private final String ROOT_FOLDER="FinderPet/";
    private final String ROUTE_IMAGE=ROOT_FOLDER+"myPhotos";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        accountViewModel =
                ViewModelProviders.of(this).get(AccountViewModel.class);
        return inflater.inflate(R.layout.fragment_account, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupViews(view);

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(getContext());

        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);  // we received the google client

        setUserData();

        btnSgnOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //signOutUser();
                commonMethods.dialogSignOutUser(getContext()).show();
            }
        });

        btnDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commonMethods.dialogDeleteAccount(getContext()).show();
            }
        });

        iconName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogUpdateName().show();
            }
        });
        iconPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogUpdatePhone().show();
            }
        });
        iconPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Validate camera permissions and external write
                if(validatePermissions()){
                    //loadImage();
                    dialogUpdatePhoto().show();
                }
            }
        });
    }

    /**
     * Method to initialize the views
     * @param view View fragment
     */
    private void setupViews(View view) {
        txtName = view.findViewById(R.id.txtProfileName);
        txtEmail = view.findViewById(R.id.txtProfileEmail);
        txtPhone = view.findViewById(R.id.txtProfilePhone);
        imgPhoto = view.findViewById(R.id.imgUser);
        iconName = view.findViewById(R.id.iconEditName);
        iconPhone = view.findViewById(R.id.iconEditPhone);
        iconPhoto = view.findViewById(R.id.updatePhoto);
        btnSgnOut = view.findViewById(R.id.btnSignOut);
        btnDeleteAccount = view.findViewById(R.id.btnDeleteAccount);
    }

    @Override
    public void onStart() {
        super.onStart();
        //firebaseAuth.addAuthStateListener(firebaseAuthListener);
    }

    /**
     * Method to display current user information
     */
    private void setUserData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user!=null){
            txtName.setText(user.getDisplayName());
            txtEmail.setText(user.getEmail());
            //txtPhone.setText(user.getPhoneNumber());
            Glide.with(getContext())
                    .load(user.getPhotoUrl())
                    .placeholder(R.drawable.img_profile)
                    .into(imgPhoto);
        }
        assert user != null;
        String idUser = user.getUid();
        databaseRef = ref.child("users/"+idUser);
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("phone").exists()){
                    // Get firebase user phone
                    String phoneUser = dataSnapshot.child("phone").getValue(String.class);
                    txtPhone.setText(phoneUser);

                }else {
                    txtPhone.setText("No phone");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), R.string.could_not_get_information, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Method to update current user photo
     * @param urlPhoto to update in firebase
     */
    public void updatePhoto(final String urlPhoto) {
        // [START update_photo]
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(Uri.parse(urlPhoto))
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                            //Toast.makeText(getContext(), R.string.updated_profile,Toast.LENGTH_LONG).show();
                            mainActivity.setupHeaderView(); // Update user information in nav view
                            setUserVisibleHint(true); // Refresh fragment
                        }
                    }
                });
        // [END update_photo]
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // Refresh your fragment here
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
            Log.i("IsRefresh", "Yes");
            progressDialog.dismiss();
        }
    }

    /**
     * Method to update current user name
     * @param userName to update in firebase
     */
    public void updateName(final String userName) {
        // [START update_profile]
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(userName).build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                            //Toast.makeText(getContext(), R.string.updated_profile,Toast.LENGTH_LONG).show();
                            txtName.setText(userName);
                            mainActivity.setupHeaderView();
                        }
                    }
                });
        // [END update_profile]
    }

    /**
     * Method to update current user phone
     * @param userPhone to update in firebase
     */
    public void updatePhone(String userPhone) {
        // [START update_profile]
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String idUser = user.getUid();
        databaseRef = ref.child("users/"+idUser+"/phone");
        databaseRef.setValue(userPhone);
        txtPhone.setText(userPhone);
        // [END update_profile]
    }

    /**
     * Method to display the update name dialog
     * @return Window dialog
     */
    public AlertDialog dialogUpdateName(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_edit_profile, null);
        builder.setView(view);

        // Views
        txtMsgEdit = view.findViewById(R.id.txtMsgEdit);
        txtEditText = view.findViewById(R.id.txtEditText);

        txtMsgEdit.setText(R.string.write_your_name);
        txtEditText.setText(txtName.getText().toString().trim());
        txtEditText.requestFocus();
        //getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        builder.setPositiveButton(getResources().getString(R.string.save), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateName(txtEditText.getText().toString().trim());
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        return newDialog = builder.create();
    }// [End dialogUpdateName]

    /**
     * Method to display the update name dialog
     * @return Window dialog
     */
    public AlertDialog dialogUpdatePhone(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_edit_profile, null);
        builder.setView(view);

        // Views
        txtMsgEdit = view.findViewById(R.id.txtMsgEdit);
        txtEditText = view.findViewById(R.id.txtEditText);

        txtMsgEdit.setText(R.string.write_your_phone);
        txtEditText.setText(txtPhone.getText().toString().trim());
        txtEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        txtEditText.requestFocus();

        builder.setPositiveButton(getResources().getString(R.string.save), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updatePhone(txtEditText.getText().toString().trim());
                dialog.dismiss();
            }
        })
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        return newDialog = builder.create();
    }// [End dialogUpdateName]

    /**
     * Method to load a image
     */
    private void loadImage(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, 10);
        }
    }

    /**
     * Method to display update user photo dialog
     */
    private AlertDialog dialogUpdatePhoto() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final CharSequence[] options = {getString(R.string.upload_from_gallery), getString(R.string.take_photo)};
        builder.setTitle(R.string.select_an_option)
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(options[i].equals(getString(R.string.take_photo))){
                            makePhoto();
                        }else {
                            loadImage();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        return builder.create();
    }

    /**
     * Method to take the photo with the camera
     */
    private void makePhoto() {
        File fileImage=new File(Environment.getExternalStorageDirectory(),ROUTE_IMAGE);
        boolean isMake=fileImage.exists();
        String nameImage="";
        if(!isMake){
            isMake=fileImage.mkdirs();
        }
        if(isMake){
            nameImage=(System.currentTimeMillis()/1000)+".jpg";
        }

        pathPhoto=Environment.getExternalStorageDirectory()+
                File.separator+ROUTE_IMAGE+File.separator+nameImage;

        File newImage=new File(pathPhoto);

        Intent intent=null;
        intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
            String authorities=getContext().getApplicationContext().getPackageName()+".provider";
            Uri imageUri= FileProvider.getUriForFile(getContext(),authorities,newImage);
            imgUserUri=imageUri;
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        }else{
            imgUserUri=Uri.fromFile(newImage);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(newImage));
        }
        startActivityForResult(intent,103);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10 && resultCode == RESULT_OK) {
            try {
                imgUserUri = data.getData();
                imgPhoto.setImageURI(imgUserUri);
                uploadImgStorage();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        if (requestCode == 103 && resultCode == RESULT_OK) {
            try {
                //imgUserUri = data.getData();
                MediaScannerConnection.scanFile(getActivity(), new String[]{pathPhoto}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            @Override
                            public void onScanCompleted(String path, Uri uri) {
                                Log.i("Storage path","Path: "+path);
                            }
                        });
                Bitmap bitmap= BitmapFactory.decodeFile(pathPhoto);
                //imgPhoto.setImageBitmap(bitmap);
                uploadImgStorage();
                showProgressDialog();
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    /**
     * Method to send report with image
     */
    private void uploadImgStorage() {
        // Reference to firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        // Reference to current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String photoName = user.getUid();
        // File name with current user id
        String nameImage=photoName+".jpg";
        final StorageReference imgUser = storageRef.child("users/"+nameImage);
        UploadTask uploadTask = imgUser.putFile(imgUserUri);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                // Continue with the task to get the download URL
                return imgUser.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    urlUserImg = task.getResult().toString();
                    updatePhoto(urlUserImg);
                } else {
                    // Handle failures
                    Toast.makeText(getContext(), R.string.image_not_loaded,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Method to validate camera and external write permissions
     * @return Boolean with true if granted permissions or false if not
     */
    private boolean validatePermissions() {
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
            return true;
        }
        if((getContext().checkSelfPermission(CAMERA)== PackageManager.PERMISSION_GRANTED)
                && (getContext().checkSelfPermission(WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)){
            return true;
        }
        if((shouldShowRequestPermissionRationale(CAMERA))
                || (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE))){
            dialogRecommendationToGrantPermissionCameraStorage();
        }else{
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA},104);
        }
        return false;
    }

    /**
     * Method to display camera and storage permission recommendation dialog
     */
    private void dialogRecommendationToGrantPermissionCameraStorage() {
        AlertDialog.Builder dialogo=new AlertDialog.Builder(getActivity());
        dialogo.setTitle(R.string.camera_video_permissions_not_granted);
        dialogo.setMessage(R.string.accept_permissions_functioning_app);

        dialogo.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA},104);
            }
        });
        dialogo.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==REQUEST_PERMISSIONS){
            if(grantResults.length==2 && grantResults[0]==PackageManager.PERMISSION_GRANTED
                    && grantResults[1]==PackageManager.PERMISSION_GRANTED){
                    dialogUpdatePhoto().show();
            }else{
                manualPermitRequest().show();
            }
        }
    }

    /**
     * Method to display permission authorization dialog manually
     */
    private AlertDialog manualPermitRequest() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.dialog_permissions_manually)
                .setMessage(R.string.accept_permissions_functioning_app)
                .setPositiveButton(R.string.btn_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent=new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri=Uri.fromParts("package",getActivity().getPackageName(),null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(R.string.btn_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getContext(),R.string.permission_not_accepted,Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

        return builder.create();
    }

    /**
     * Method to display the new record progress dialog
     */
    private void showProgressDialog(){
        progressDialog.setCancelable(false);
        //progressDialog.setTitle("Actualizando..."); // usamos esta linea cuando no utilizamos el setContenView
        progressDialog.show();
        progressDialog.setContentView(R.layout.layout_dialog_update_photo); // No usamos esta linea cuando usamos setTitle
    };

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    //    // Cuando se presione el boton para regresar y el Navigation View esta abierto lo cerramos
//    @Override
//    public void onBackPressed() {
//        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
//            drawerLayout.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
//    }

}