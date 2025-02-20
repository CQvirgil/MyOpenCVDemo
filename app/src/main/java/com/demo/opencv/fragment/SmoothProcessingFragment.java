package com.demo.opencv.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.demo.opencv.R;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class SmoothProcessingFragment extends BaseFragment {
    private SeekBar sbBlur;
    private Bitmap bitmap;
    private ImageView imageView;
    private Mat src;//输入
    private Mat dst;//输出

    @Override
    protected int layout() {
        return R.layout.fragment_smooth_processing;
    }

    @Override
    protected void initView(View root) {
        imageView = root.findViewById(R.id.fgm_smooth_processing_iv);
        bitmap = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.bikaqui);
        src = new Mat();
        dst = new Mat();
        Utils.bitmapToMat(bitmap, src);
        Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2GRAY);
        threshold(125);

        sbBlur = root.findViewById(R.id.fgm_seek_bar_blur);
        sbBlur.setMax(125);
        sbBlur.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                threshold(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void threshold(double threshold){
        Imgproc.threshold(src, dst, threshold, 255, Imgproc.THRESH_BINARY);
        Utils.matToBitmap(dst, bitmap);
        imageView.setImageBitmap(bitmap);
    }
}
