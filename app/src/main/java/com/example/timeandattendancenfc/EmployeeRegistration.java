package com.example.timeandattendancenfc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class EmployeeRegistration extends AppCompatActivity {

    public Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_registration);

        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //do validations here


                //save details to local db if exists


                //go back to dashboard
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }
}