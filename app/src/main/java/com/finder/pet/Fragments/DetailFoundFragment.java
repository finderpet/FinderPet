package com.finder.pet.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.finder.pet.Entities.Found_Vo;
import com.finder.pet.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DetailFoundFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetailFoundFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailFoundFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    TextView txtType, txtEmail, txtLocation, txtPhone, txtObservations;
    ImageView imgPet1, imgPet2, imgPet3;

    public DetailFoundFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetailFoundFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailFoundFragment newInstance(String param1, String param2) {
        DetailFoundFragment fragment = new DetailFoundFragment();
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
        View view = inflater.inflate(R.layout.fragment_detail_found, container, false);

        txtType = view.findViewById(R.id.detailTypePet);
        txtLocation = view.findViewById(R.id.detailLocation);
        txtEmail = view.findViewById(R.id.detailEmailContact);
        txtPhone = view.findViewById(R.id.detailPhoneContact);
        txtObservations = view.findViewById(R.id.detailObservations);
        imgPet1 = view.findViewById(R.id.imgDetail1);
        imgPet2 = view.findViewById(R.id.imgDetail2);
        imgPet3 = view.findViewById(R.id.imgDetail3);

        Bundle objectFound=getArguments();
        Found_Vo found_vo;
        if (objectFound != null){
            found_vo = (Found_Vo) objectFound.getSerializable("objeto");

            txtType.setText("Tipo de mascota: "+found_vo.getFound_type());
            txtLocation.setText("Mascota vista en: "+found_vo.getFound_address());
            txtEmail.setText("Correo de contacto: "+found_vo.getFound_email());
            txtPhone.setText("Teléfono de contacto: "+found_vo.getFound_phone());
            txtObservations.setText("Observaciones: "+found_vo.getFound_description());
            Glide.with(getContext())
                    .load(found_vo.getFound_image())
                    .placeholder(R.drawable.sin_imagen)
                    .into(imgPet1);
            Glide.with(getContext())
                    .load(found_vo.getFound_image())
                    .placeholder(R.drawable.sin_imagen)
                    .into(imgPet2);
            Glide.with(getContext())
                    .load(found_vo.getFound_image())
                    .placeholder(R.drawable.sin_imagen)
                    .into(imgPet3);

        }

        return view;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
