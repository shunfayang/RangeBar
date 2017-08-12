package com.yangshunfa.rangebar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.yangshunfa.rangebar.view.RangeBar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        RangeBar mRangeBar = (RangeBar) findViewById(R.id.rangeBar);
        mRangeBar.setRange(1, 3);

    }
}
