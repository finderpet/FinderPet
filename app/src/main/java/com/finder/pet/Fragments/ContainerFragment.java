package com.finder.pet.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.finder.pet.R;
import com.finder.pet.Utilities.Utilities;
import com.finder.pet.ui.account.AccountFragment;
import com.finder.pet.ui.donate.DonateFragment;
import com.finder.pet.ui.adverts.AdvertsFragment;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

import com.finder.pet.Adapters.ViewAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ContainerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ContainerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContainerFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    View view;
    private AppBarLayout appBar;
    private TabLayout tabs;
    private ViewPager viewPager;

    public ContainerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContainerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContainerFragment newInstance(String param1, String param2) {
        ContainerFragment fragment = new ContainerFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_container, container, false);

        /**Bandera para evitar la duplicación de las tabs en el appbar*/
        if (Utilities.rotation == 0){
            View parent = (View) container.getParent();
            if (appBar==null){
                appBar = parent.findViewById(R.id.id_appBar);
                tabs = new TabLayout(getActivity());
                tabs.setTabTextColors(Color.parseColor("#FFFFFF"), Color.parseColor("#F50057"));
                appBar.addView(tabs);

                viewPager = view.findViewById(R.id.id_ViewPager);

                fillViewPager(viewPager);
                viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                        super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                    }
                });
                tabs.setupWithViewPager(viewPager);
            }
            tabs.setTabGravity(TabLayout.GRAVITY_FILL);
        }else {
            Utilities.rotation = 1;
        }

        return view;
    }

    private void fillViewPager(ViewPager viewPager) {
        //ViewAdapter adapter = new ViewAdapter(getFragmentManager());
        ViewAdapter adapter = new ViewAdapter(getFragmentManager());
        adapter.addFragment(new DonateFragment(), "Encontrados");
        adapter.addFragment(new AdvertsFragment(), "Perdidos");
        adapter.addFragment(new AccountFragment(), "Adopciones");

        viewPager.setAdapter(adapter);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (Utilities.rotation == 0){
            appBar.removeView(tabs);
        }

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
