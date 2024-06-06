package com.example.nckh8;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText edtNumber;
    Button btnNumber;
    Button btnOpencame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtNumber = findViewById(R.id.edt_number);
        btnNumber = findViewById(R.id.btn_number);


        btnNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = edtNumber.getText().toString();

                if (number.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Vui lòng nhập số câu", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(MainActivity.this, ListCorrectAnswers.class);
                    intent.putExtra("number", number);

                    startActivity(intent);
                }
            }
        });
    }
}
