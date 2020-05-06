package com.finder.pet.ui.home;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.finder.pet.Authentication.LoginActivity;
import com.finder.pet.Entities.Found_Vo;
import com.finder.pet.Fragments.DetailFoundFragment;
import com.finder.pet.Interfaces.IComunicaFragments;
import com.finder.pet.Main.CloseActivity;
import com.finder.pet.Main.MainActivity;
import com.finder.pet.R;
import com.finder.pet.Utilities.Utilities;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static androidx.navigation.Navigation.findNavController;

public class HomeFragment extends Fragment{

    private HomeViewModel homeViewModel;
    private Button btn;
    private CardView cardViewFound, cardViewLost, cardViewAdopted;
    // [START declare_auth ]
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    // [END declare_auth]

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //Acá podemos colocar la lógica que queremos cuando cambie a este fragment
            }
        });
        firebaseAuth = FirebaseAuth.getInstance();
        //FirebaseUser user = firebaseAuth.getCurrentUser();

        // This callback will only be called when MyFragment is at least Started.
        //Con este método manejamos el evento de boton atras solamente en este fragment
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                final CharSequence[] opciones={"Aceptar","Cancelar"};
                final AlertDialog.Builder alertOpciones=new AlertDialog.Builder(getContext());
                alertOpciones.setTitle("¿Desea salir de la aplicación?");
                alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (opciones[i].equals("Aceptar")){
                            //firebaseAuth.signOut();
//                            getActivity().finish();
//                            Intent intent = new Intent(getContext(), LoginActivity.class);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
//                                    | Intent.FLAG_ACTIVITY_CLEAR_TASK
//                                    | Intent.FLAG_ACTIVITY_NEW_TASK); // Cerramos sesión de usuario de firebase, de google y eliminamos la pila de actividades y fragments
//                            startActivity(intent);
//                            startActivity(new Intent(getContext(), LoginActivity.class)
//                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
//                                            | Intent.FLAG_ACTIVITY_SINGLE_TOP
//                                            | Intent.FLAG_ACTIVITY_CLEAR_TASK
//                                            | Intent.FLAG_ACTIVITY_NEW_TASK));
                            Intent intent = new Intent(getContext(), CloseActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                            | Intent.FLAG_ACTIVITY_SINGLE_TOP
                                            | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            | Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("EXIT", true);
                            startActivity(intent);
                        }else{
                            dialogInterface.dismiss();
                        }
                    }
                });
                alertOpciones.show();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);


        btn = root.findViewById(R.id.btn_pager);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //findNavController(view).navigate(R.id.action_nav_home_to_tabsActivity);
                Toast.makeText(getContext(), "¡No tenemos promociones activas!", Toast.LENGTH_SHORT).show();
            }
        });

        cardViewFound = root.findViewById(R.id.id_cardViewFound);
        cardViewFound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findNavController(view).navigate(R.id.action_nav_home_to_foundFragment);
            }
        });

        cardViewLost = root.findViewById(R.id.id_cardViewLost);
        cardViewLost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findNavController(view).navigate(R.id.action_nav_home_to_lostFragment);
            }
        });

        cardViewAdopted = root.findViewById(R.id.id_cardViewAdopted);
        cardViewAdopted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findNavController(view).navigate(R.id.action_nav_home_to_adoptedFragment);
            }
        });

        return root;
    }


}