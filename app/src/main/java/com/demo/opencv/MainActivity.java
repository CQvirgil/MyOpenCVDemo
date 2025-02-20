package com.demo.opencv;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.demo.opencv.fragment.GaryImageFragment;
import com.demo.opencv.fragment.NostalgiaImageFragment;
import com.demo.opencv.fragment.SmoothProcessingFragment;
import com.google.android.material.navigation.NavigationView;

import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private FrameLayout flContent;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawer = findViewById(R.id.main_drawer);
        toolbar = findViewById(R.id.main_toolbar);
        flContent = findViewById(R.id.main_fl_content);
        navigationView = findViewById(R.id.main_navigation);
        initDrawerLayout();
    }

    @Override
    protected void onResume() {
        super.onResume();
        OpenCVLoader.initLocal();
    }

    private void changFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_fl_content, fragment);
        fragmentTransaction.commit();
    }

    private void initDrawerLayout(){
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.drawer_menu_1){
                    changFragment(new GaryImageFragment());
                }
                if (itemId == R.id.drawer_menu_2){
                    changFragment(new SmoothProcessingFragment());
                }
                if (itemId == R.id.drawer_menu_3){
                    changFragment(new NostalgiaImageFragment());
                }
                drawer.close();
                return true;
            }
        });
    }
}