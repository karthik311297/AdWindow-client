package com.example.karthik.adwindow_client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class UserDashboard extends AppCompatActivity implements PaymentHistory.OnFragmentInteractionListener,ContentOngoing.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        ImageButton generalMenu = findViewById(R.id.generalMenu);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.mContent);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.mapAd) {
                    Intent intent = new Intent(UserDashboard.this, AdMap.class);
                    startActivity(intent);
                }
                else if(menuItem.getItemId() == R.id.mpay)
                {
                    Fragment fragment = new PaymentHistory();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.dashboardFrame, fragment);
                    fragmentTransaction.commit();
                }
                else
                {
                    Fragment fragment = new ContentOngoing();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.dashboardFrame, fragment);
                    fragmentTransaction.commit();
                }

                return true;
            }
        });
        generalMenu.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    @Override
    protected void onResume() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.mContent);
        super.onResume();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
