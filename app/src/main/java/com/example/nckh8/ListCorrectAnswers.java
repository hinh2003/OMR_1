package com.example.nckh8;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListCorrectAnswers extends AppCompatActivity {

    String[] numericalOrder;
    ListView lvCorrectAnswers;
    Button btnSelectImage;
    Toolbar tb_back_exam;
    Button btnOpencame;

    private static final int CAMERA_REQUEST_CODE = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_correct_answers);

        lvCorrectAnswers = findViewById(R.id.lv_correct_answers);
        btnSelectImage = findViewById(R.id.btn_select_image);
        tb_back_exam = findViewById(R.id.tb_back_exam);
        btnOpencame = findViewById(R.id.Opencame);

        // Tải thư viện OpenCV
        if (!OpenCVLoader.initDebug()) {
            Log.e("TAG", "OpenCV library not loaded");
        } else {
            Log.d("TAG", "OpenCV library loaded successfully");
        }

        String code = getIntent().getStringExtra("code");
        ArrayList<String> answers = getIntent().getStringArrayListExtra("answers");
        String number = getIntent().getStringExtra("number");

        int index = 0;
        index = getIntent().getIntExtra("index", index);

        // Đặt tiêu đề cho trang chọn đáp án
        tb_back_exam.setTitle("Mã đề " + code);

        if (number != null) {
            numericalOrder = new String[Integer.parseInt(number)];

            for (int i = 0; i < numericalOrder.length; i++) {
                if (i<9) {
                    numericalOrder[i] = "0" + (i+1);
                    continue;
                }
                numericalOrder[i] = String.valueOf((i+1));
            }

            // Đặt bộ điều hợp để điền dữ liệu vào ListView
            AdapterCorrectAnswer adapterCorrectAnswer = new AdapterCorrectAnswer(getApplicationContext(), numericalOrder, code, answers, index);
            lvCorrectAnswers.setAdapter(adapterCorrectAnswer);
            btnOpencame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ListCorrectAnswers.this, CameraActivity.class);
                    startActivityForResult(intent, CAMERA_REQUEST_CODE);
                }
            });
            btnSelectImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean check = true;

                    // Kiểm tra chọn hết đán án chưa
                    for (int i = 0; i < Integer.parseInt(number); i++) {
                        if(AdapterCorrectAnswer.correctAnswers.get(i).equals("Null ")) {
                            Toast.makeText(ListCorrectAnswers.this, "Chưa chọn đáp án câu " + (i+1), Toast.LENGTH_SHORT).show();
                            check =false;
                            break;
                        }
                        Log.d("check", AdapterCorrectAnswer.correctAnswers.get(i));
                    }
                    if (check) {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        startActivityForResult(intent,1001--);
                    }
                }
            });

            // setup toolbar back
            setSupportActionBar(tb_back_exam);
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        } else {
            finish();
        }
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

        Intent intent = new Intent(ListCorrectAnswers.this, ListStudentAnswers.class);

        // Lưu Bitmap vào bộ nhớ
        String imagePath = saveBitmapToStorage(bitmap);

        // Gửi đường dẫn hình ảnh thay vì Bitmap qua Intent
        intent.putExtra("image_path", imagePath);
        intent.putExtra("numerical_order", numericalOrder);
        intent.putExtra("total_score", totalScore);
        intent.putExtra("student_answers", studentAnswers);
        intent.putExtra("correct_answers", AdapterCorrectAnswer.correctAnswers);

        startActivity(intent);
    }



    private String saveBitmapToStorage(Bitmap bitmap) {
        String path = ""; // Đường dẫn lưu trữ ảnh

        try {
            // Tạo thư mục lưu trữ (nếu chưa tồn tại)
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File imageFile = File.createTempFile("image", ".jpg", storageDir);

            // Ghi Bitmap vào tệp
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

            // Giải phóng bộ nhớ của Bitmap
            bitmap.recycle();

            path = imageFile.getAbsolutePath(); // Lấy đường dẫn tệp
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(ListCorrectAnswers.this, "Phiếu không hợp lệ", Toast.LENGTH_SHORT).show();
        }

        return path;
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