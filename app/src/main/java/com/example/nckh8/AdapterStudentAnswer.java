package com.example.nckh8;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterStudentAnswer extends BaseAdapter {
    Context context;
    String[] listNumericalOrder;
    ArrayList<String> listCorrectAnswers, listStudentAnswers;
    LayoutInflater inflater;


    public AdapterStudentAnswer(Context context, String[] listNumericalOrder, ArrayList<String> listCorrectAnswers, ArrayList<String> listStudentAnswers) {
        this.context = context;
        this.listNumericalOrder = listNumericalOrder;
        this.listCorrectAnswers = listCorrectAnswers;
        this.listStudentAnswers = listStudentAnswers;

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

        convertView = inflater.inflate(R.layout.item_student_answer, null);

        // Lấy tham chiếu của TextView và Button
        TextView numericalOrder = convertView.findViewById(R.id.tv_numerical_order);
        TextView correctAnswer = convertView.findViewById(R.id.tv_correct_answer);
        TextView studentAnswer = convertView.findViewById(R.id.tv_student_answer);


        // Đặt giá trị trong TextView
        numericalOrder.setText(listNumericalOrder[position]);
        correctAnswer.setText(listCorrectAnswers.get(position));
        studentAnswer.setText(listStudentAnswers.get(position));

        if (!listCorrectAnswers.get(position).equals(listStudentAnswers.get(position))) {
            studentAnswer.setTextColor(Color.RED);
        }

        return convertView;
    }
}
