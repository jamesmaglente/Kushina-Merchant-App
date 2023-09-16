package com.kushina.merchant.android.navigations.deposits;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.kushina.merchant.android.R;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ZoomImageActivity extends AppCompatActivity {

    @BindView(R.id.iv_zoomed_image)
    ImageView ivZoomedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_image);
        ButterKnife.bind(this);

        getSupportActionBar().hide();
        View mDecorView = this.getWindow().getDecorView();
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);


        try {
            Picasso.get()
                    .load(getIntent().getStringExtra("image"))
                    //.load(item.getItemImage())
                    .resize(600, 600)
                    .placeholder(R.drawable.applogo)
                    .into(ivZoomedImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
