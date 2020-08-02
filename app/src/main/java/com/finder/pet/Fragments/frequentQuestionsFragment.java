package com.finder.pet.Fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.finder.pet.R;
import com.google.android.material.button.MaterialButton;


/**
 * A simple {@link Fragment} subclass.
 */
public class frequentQuestionsFragment extends Fragment {

    MaterialButton btnQuestion1, btnQuestion2, btnQuestion3, btnQuestion4, btnQuestion5;
    TextView answer1, answer2, answer3, answer4, answer5;
    private Boolean q1, q2, q3, q4, q5;


    public frequentQuestionsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_frequent_questions, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupViews(view);

        btnQuestion1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!q1){
                    answer1.setVisibility(View.VISIBLE);
                    q1=true;
                }else {
                    answer1.setVisibility(View.GONE);
                    q1=false;
                }
            }
        });
        btnQuestion2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!q2){
                    answer2.setVisibility(View.VISIBLE);
                    q2=true;
                }else {
                    answer2.setVisibility(View.GONE);
                    q2=false;
                }
            }
        });
        btnQuestion3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!q3){
                    answer3.setVisibility(View.VISIBLE);
                    q3=true;
                }else {
                    answer3.setVisibility(View.GONE);
                    q3=false;
                }
            }
        });

    }


    private void setupViews(View view) {
        btnQuestion1 = view.findViewById(R.id.question1);
        btnQuestion2 = view.findViewById(R.id.question2);
        btnQuestion3 = view.findViewById(R.id.question3);

        answer1 = view.findViewById(R.id.answerQ1);
        answer2 = view.findViewById(R.id.answerQ2);
        answer3 = view.findViewById(R.id.answerQ3);

        q1=q2=q3=false;
    }
}
