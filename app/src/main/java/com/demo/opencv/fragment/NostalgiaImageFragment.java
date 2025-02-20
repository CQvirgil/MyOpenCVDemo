package com.demo.opencv.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.demo.opencv.R;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

public class NostalgiaImageFragment extends BaseFragment {
    @Override
    protected int layout() {
        return R.layout.fragment_gary_image;
    }

    @Override
    protected void initView(View root) {
        Bitmap bitmap = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.bikaqui);
        ImageView img = root.findViewById(R.id.gray_image_view);
        Button btn = root.findViewById(R.id.gray_image_btn);
        btn.setText("怀旧滤镜");
        img.setImageBitmap(bitmap);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                img.setImageBitmap(modify(bitmap));
            }
        });
    }

    /**
     * 怀旧滤镜
     * @param srcBitmap
     * @return
     */
    private Bitmap modify(Bitmap srcBitmap){
        Mat mat = new Mat();
        Utils.bitmapToMat(srcBitmap, mat);

        //通道数
        int channels = mat.channels();
        //宽度
        int col = mat.cols();
        //高度 等同于srcMat.geight()
        int row = mat.rows();
        //类型
        int type = mat.type();
        Log.d("debugOpenCV", "通道数: " + channels + "宽度：" + col + "高度：" + row + "类型：" + type);

        //用来存储每个像素点的数据，数组长度对应图片的通道数
        //不同图片类型对应不同java数据类型，这里CV_8UC4，对应Java byte类型
        byte[] p = new byte[channels * col];
        int r = 0, g = 0, b = 0;

        for (int h = 0; h < row; h++){
            for (int w = 0; w < col; w++){
                //通过像素点位置得到该像素点的数据，并存入p数组中
//                mat.get(h,w,p);
                int index = channels * w;

                //得到一个像素点的RGB值
                r = p[index] & 0xff;
                g = p[index + 1] & 0xff;
                b = p[index + 2] & 0xff;

                //根据怀旧图片滤镜公式进行计算
                int AR = (int) (0.393 * r + 0.769 * g + 0.189 * b);
                int AG = (int) (0.349 * r + 0.686 * g + 0.168 * b);
                int AB = (int) (0.272 * r + 0.534 * g + 0.131 * b);

                //防越界判断，byte最大值是255
                AR = (AR > 255 ? 255 : (AR < 0 ? 0 : AR));
                AG = (AG > 255 ? 255 : (AG < 0 ? 0 : AG));
                AB = (AB > 255 ? 255 : (AB < 0 ? 0 : AB));

                p[0] = (byte) AR;
                p[1] = (byte) AG;
                p[2] = (byte) AB;

                mat.put(h,0,p);
            }
        }

        Bitmap dstBitmap = Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, dstBitmap);
        mat.release();
        return dstBitmap;
    }
}
