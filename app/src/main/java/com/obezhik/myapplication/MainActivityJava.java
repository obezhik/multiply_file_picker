package com.obezhik.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.obezhik.multiply_file_picker.MultiplyFilePicker;

import java.io.File;

public class MainActivityJava extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_java);

        MultiplyFilePicker filePicker = new MultiplyFilePicker(this);

    }
}