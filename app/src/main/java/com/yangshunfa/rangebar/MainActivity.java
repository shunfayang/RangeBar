package com.yangshunfa.rangebar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.yangshunfa.rangebar.view.RangeBar;

public class MainActivity extends AppCompatActivity {

    private RangeBar mRangeBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRangeBar = (RangeBar) findViewById(R.id.rangeBar);
        mRangeBar.setOnRangeSelectedListener(new RangeBar.OnRangeSelectedListener() {
            @Override
            public void onRangeSelected(int left, int right) {
                Toast.makeText(MainActivity.this, "left= " + left + " right= " + right, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mRangeBar.setRange(1, 3);

    }

    public void resetClick(View view) {
        mRangeBar.setRange(0, 6);
    }
}
