package com.example.timeandattendancenfc;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.google.android.material.card.MaterialCardView;

public class MainActivity extends AppCompatActivity {

    Activity act;
    Context ctx;
    public MaterialCardView report, clock, register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        act = this;
        ctx = this;

        report = (MaterialCardView) findViewById(R.id.report);
        register = (MaterialCardView) findViewById(R.id.register);
        clock = (MaterialCardView) findViewById(R.id.clock);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), EmployeeRegistration.class));
            }
        });

        clock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), EmployeesListActivity.class));
            }
        });

        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ReportsActivity.class));
            }
        });
    }

}