package com.finder.pet.Main;

import android.os.Bundle;
import android.text.Html;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.finder.pet.Main.ui.tutorial.SectionsPagerAdapter;
import com.finder.pet.R;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

public class ContainerTutorialActivity extends AppCompatActivity {

    private LinearLayout linearDots;
    private TextView[] dotsSlide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container_tutorial);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);

        linearDots = findViewById(R.id.linearDots);
        addDotsIndicator(0);
        viewPager.addOnPageChangeListener(viewListener);

    }

    /**
     * Method of adding the indicator points
     * @param pos Position indicator
     */
    private void addDotsIndicator(int pos) {
        dotsSlide = new TextView[7];
        linearDots.removeAllViews();

        for (int i=0;i<dotsSlide.length;i++){
            dotsSlide[i] = new TextView(this);
            dotsSlide[i].setText(Html.fromHtml("&#8226;"));
            dotsSlide[i].setTextSize(35);
            dotsSlide[i].setTextColor(getResources().getColor(R.color.colorBlancoTransparente));
            linearDots.addView(dotsSlide[i]);
        }

        if (dotsSlide.length>0){
            dotsSlide[pos].setTextColor(getResources().getColor(R.color.colorWhite));
        }
    }

    // Capture the screen movement event
    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

}