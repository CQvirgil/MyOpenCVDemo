package com.demo.opencv.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.demo.opencv.R;

public class FaceDetectorFragment extends Fragment {
    private ImageView imageView;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gary_image, container, false);
        initView(view);
        return view;
    }

   private void initView(View view){
       imageView = view.findViewById(R.id.face_detector_iv);
   }
}
