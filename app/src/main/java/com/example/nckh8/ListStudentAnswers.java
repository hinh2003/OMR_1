package com.example.nckh8;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListStudentAnswers extends AppCompatActivity {

    ListView lvStudentAnswers;
    Button btnSelectAnotherImage;
    ImageView imageView;
    Toolbar tb_back_correct;
    Button btnOpencame;

    private static final int CAMERA_REQUEST_CODE = 1002;

    // Biến nhận Intent
    ArrayList<String> studentAnswers, correctAnswers;
    String[] numericalOrder;
    String imagePath;
    float totalScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_student_answers);


        lvStudentAnswers = findViewById(R.id.lv_student_answers);
        btnSelectAnotherImage = findViewById(R.id.btn_select_another_image);
        tb_back_correct = findViewById(R.id.tb_back_correct);
        imageView = findViewById(R.id.imageView);
        btnOpencame = findViewById(R.id.Opencame_1);
        Intent intent = getIntent();

        // Nhận đường dẫn ảnh từ Intent
        imagePath = intent.getStringExtra("image_path");
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        studentAnswers = intent.getStringArrayListExtra("student_answers");
        correctAnswers = intent.getStringArrayListExtra("correct_answers");
        totalScore = intent.getFloatExtra("total_score", totalScore);
        numericalOrder = intent.getStringArrayExtra("numerical_order");

        imageView.setImageBitmap(bitmap);
        tb_back_correct.setTitle("Phiếu được " + String.format("%.2f", totalScore) + " điểm");

        AdapterStudentAnswer adapterStudentAnswer = new AdapterStudentAnswer(getApplicationContext(), numericalOrder, correctAnswers, studentAnswers);
        lvStudentAnswers.setAdapter(adapterStudentAnswer);
        btnOpencame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListStudentAnswers.this, CameraActivity.class);
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            }
        });
        btnSelectAnotherImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,1001);
            }
        });

        // setup toolbar back
        setSupportActionBar(tb_back_correct);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            // Xử lý ảnh được chọn từ thư viện
            Uri selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                handleImage(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Xử lý ảnh được chụp từ camera
            Uri capturedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), capturedImageUri);
                handleImage(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void handleImage(Bitmap bitmap) {
        try{
        // Xoay ảnh thành dọc
        if (bitmap.getWidth() > bitmap.getHeight()) {
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }

        Mat img = new Mat();
        Utils.bitmapToMat(bitmap, img);

        List<Mat> cropImages = OMR2.cropImage(img);
        List<Mat> listAnswers = OMR2.processAnsBlocks(cropImages);
        List<Mat> processListAnswers = OMR2.processListAns(listAnswers);
        ArrayList<String> studentAnswers = new ArrayList<>();

        float totalScore;
        int numberCorrectAnswers = 0;
        int multipleAnswer = 0;
        boolean correctAnswer = false;

        for (int i = 0; i < numericalOrder.length * 4; i++) {
            Mat ans = processListAnswers.get(i);
            int tes = Core.countNonZero(ans);
            String mappedAnswer = OMR2.mapAnswer(i);

            // Duyệt hết 1 câu
            if (i % 4 == 0) {
                studentAnswers.add("Null");
                multipleAnswer = 0;
                correctAnswer = false;
            }

            if (tes > 140) {
                if (multipleAnswer > 0) {
                    studentAnswers.set(i / 4, "Null");
                    if (correctAnswer) {
                        numberCorrectAnswers--;
                        correctAnswer = false;
                    }
                } else {
                    studentAnswers.set(i / 4, mappedAnswer);
                    if (AdapterCorrectAnswer.correctAnswers.get(i / 4).equals(mappedAnswer)) {
                        numberCorrectAnswers++;
                        correctAnswer = true;
                    }
                }
                multipleAnswer++;
            }
            Log.d("check : "+ i,"d : "+tes);

        }

        totalScore = (float) numberCorrectAnswers / numericalOrder.length * 10;
        // Hiển thị hình ảnh và điểm số tổng cộng trên giao diện người dùng
        imageView.setImageBitmap(bitmap);
        tb_back_correct.setTitle("Phiếu được " + String.format("%.2f", totalScore) + " điểm");

        // Hiển thị danh sách câu trả lời của học sinh
        AdapterStudentAnswer adapterStudentAnswer = new AdapterStudentAnswer(getApplicationContext(), numericalOrder, correctAnswers, studentAnswers);
        lvStudentAnswers.setAdapter(adapterStudentAnswer);

    }
    catch (Exception e) {
        e.printStackTrace();

        Toast.makeText(ListStudentAnswers.this, "Phiếu không hợp lệ", Toast.LENGTH_SHORT).show();
    }
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