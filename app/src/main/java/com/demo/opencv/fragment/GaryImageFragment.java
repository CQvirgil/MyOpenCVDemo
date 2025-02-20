package com.demo.opencv.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.demo.opencv.R;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class GaryImageFragment extends Fragment {
    private ImageView imageView;
    private Button btnGaryImage;
    private Bitmap bitmap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gary_image, container, false);
        imageView = view.findViewById(R.id.gray_image_view);
        btnGaryImage = view.findViewById(R.id.gray_image_btn);
        initView();
        return view;
    }

    private void initView(){
        bitmap = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.bikaqui);
        imageView.setImageBitmap(bitmap);
        btnGaryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bitmap = garyImage(bitmap);
                imageView.setImageBitmap(bitmap);
            }
        });
    }

    private Bitmap garyImage(Bitmap bitmap){
        Mat mat = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC3);
        Utils.bitmapToMat(bitmap, mat);
        Bitmap garyImage = Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.ARGB_8888);
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2GRAY);
        Utils.matToBitmap(mat, garyImage);
        return garyImage;
    }
}
