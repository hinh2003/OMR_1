package com.example.nckh8;

import static com.example.nckh8.ListExamCode.examCodeArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterCorrectAnswer extends BaseAdapter {
    Context context;
    String[] listNumericalOrder;
    LayoutInflater inflater;


    TextView numericalOrder;
    RadioButton A, B, C, D;

    public static ArrayList<String> correctAnswers;
    String code;
    int index;

    public AdapterCorrectAnswer(Context context, String[] listNumericalOrder, String code,ArrayList<String> correctAnswers, int index) {
        this.context = context;
        this.listNumericalOrder = listNumericalOrder;
        AdapterCorrectAnswer.correctAnswers = correctAnswers;
        this.code = code;
        this.index = index;

        if (correctAnswers.isEmpty()) {
            for (int i = 0; i < listNumericalOrder.length; i++) {
                correctAnswers.add("Null ");
            }
        }

        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listNumericalOrder.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = inflater.inflate(R.layout.item_correct_answer, null);

        // Lấy tham chiếu của TextView và Button
        numericalOrder = convertView.findViewById(R.id.tv_numerical_order);
        A =  convertView.findViewById(R.id.rdo_a);
        B =  convertView.findViewById(R.id.rdo_b);
        C =  convertView.findViewById(R.id.rdo_c);
        D =  convertView.findViewById(R.id.rdo_d);

        // Thực hiện sự kiện setOnCheckedChangeListener trên nút "A"
        A.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                // Đặt giá trị "A" trong ArrayList nếu RadioButton được chọn
                if (isChecked) {
                    correctAnswers.set(position, "A");
                }
            }
        });

        // Thực hiện sự kiện setOnCheckedChangeListener trên nút "B"
        B.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                // Đặt giá trị "B" trong ArrayList nếu RadioButton được chọn
                if (isChecked) {
                    correctAnswers.set(position, "B");
                }
            }
        });

        // Thực hiện sự kiện setOnCheckedChangeListener trên nút "C"
        C.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                // Đặt giá trị "C" trong ArrayList nếu RadioButton được chọn
                if (isChecked) {
                    correctAnswers.set(position, "C");
                }
            }
        });

        // Thực hiện sự kiện setOnCheckedChangeListener trên nút "D"
        D.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                // Đặt giá trị "D" trong ArrayList nếu RadioButton được chọn
                if (isChecked) {
                    correctAnswers.set(position, "D");
                }
            }
        });

        // Xử lý sau khi cuộn lên/xuống không bị mất đáp án
        if(correctAnswers.get(position).equals("A")){
            A.setChecked(true);
        }else if(correctAnswers.get(position).equals("B")){
            B.setChecked(true);
        }else if(correctAnswers.get(position).equals("C")){
            C.setChecked(true);
        }else if(correctAnswers.get(position).equals("D")){
            D.setChecked(true);
        }

        // Đặt giá trị trong TextView
        numericalOrder.setText(listNumericalOrder[position]);
        ExamCode examCode = new ExamCode(code, correctAnswers);
        examCodeArrayList.set(index, examCode);

        return convertView;
    }
}