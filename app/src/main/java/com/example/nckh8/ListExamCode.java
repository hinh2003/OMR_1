package com.example.nckh8;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Objects;

public class ListExamCode extends AppCompatActivity {

    EditText edtAddCode;
    Button btnAddCode;
    ListView lvExamCode;
    Toolbar tb_back_main;
    ArrayList<String> answers;

    // Tạo bộ nhớ các mã đề
    public static ArrayList<ExamCode> examCodeArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_exam_code);

        edtAddCode = findViewById(R.id.edt_add_code);
        btnAddCode = findViewById(R.id.btn_add_code);
        lvExamCode = findViewById(R.id.lv_exam_code);
        tb_back_main = findViewById(R.id.tb_back_main);

        Intent intent = getIntent();
        String number = intent.getStringExtra("number");

        answers = new ArrayList<>();

        // Làm sạch bộ nhớ mỗi lần nhập số câu mới
        examCodeArrayList.clear();

        ArrayAdapter listAdapter = new AdapterExamCode(ListExamCode.this, examCodeArrayList);
        lvExamCode.setAdapter(listAdapter);

        // Thêm mã đề
        btnAddCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtAddCode.getText().toString().isEmpty()) {
                    Toast.makeText(ListExamCode.this, "Chưa nhập mã đề", Toast.LENGTH_SHORT).show();
                } else {
                    boolean check = true;
                    String code = edtAddCode.getText().toString();

                    // Kiểm mã đề trùng
                    for (int i = 0; i < examCodeArrayList.size(); i++) {
                        if (code.equals(examCodeArrayList.get(i).getCode())) {
                            Toast.makeText(ListExamCode.this, "Mã đề đã tồn tại", Toast.LENGTH_SHORT).show();
                            check = false;
                            break;
                        }
                    }

                    if (check) {
                        ExamCode examCode = new ExamCode(code, answers);
                        examCodeArrayList.add(examCode);
                        listAdapter.notifyDataSetChanged();
                        edtAddCode.setText("");
                    }
                }
            }
        });

        // Xóa mã đề
        lvExamCode.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(ListExamCode.this, "Đã xóa mã đề " + examCodeArrayList.get(position).getCode(), Toast.LENGTH_SHORT).show();
                examCodeArrayList.remove(position);
                listAdapter.notifyDataSetChanged();

                return false;
            }
        });

        // Chọn mã đề để xử lý
        lvExamCode.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ListExamCode.this, ListCorrectAnswers.class);

                ExamCode selectedExamCode = examCodeArrayList.get(position);
                intent.putExtra("index", position);
                intent.putExtra("code", selectedExamCode.code);
                intent.putExtra("answers", selectedExamCode.answers);
                intent.putExtra("number", number);
                startActivity(intent);
            }
        });

        // setup toolbar back
        setSupportActionBar(tb_back_main);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

    }

    // Khi ấn nút back sẽ quay lại màn hình trước
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}