package com.example.nckh8;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class AdapterExamCode extends ArrayAdapter<ExamCode> {

    public AdapterExamCode(Context context, ArrayList<ExamCode> examCodeArrayList) {
        super(context, R.layout.item_exam_code, examCodeArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ExamCode examCode = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_exam_code, parent, false);
        }

        TextView code = convertView.findViewById(R.id.code);
        if (examCode != null) {
            code.setText("Mã đề " + examCode.code);
        }

        return convertView;
    }
}
