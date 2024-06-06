package com.example.nckh8;

import java.util.ArrayList;

public class ExamCode {
    String code;
    ArrayList<String> answers;

    public ExamCode(String code, ArrayList<String> answers) {
        this.code = code;
        this.answers = answers;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ArrayList<String> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<String> answers) {
        this.answers = answers;
    }
}
