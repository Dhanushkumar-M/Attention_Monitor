package com.example.attentionmonitor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AboutPage extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);

        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_About);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.navigation_profile:
                        startActivity(new Intent(getApplicationContext(), AccountPage.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.navigation_home:
                        startActivity(new Intent(getApplicationContext(), DemoActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.navigation_About:
                        return true;
                }
                return false;
            }
        });
    }
}


