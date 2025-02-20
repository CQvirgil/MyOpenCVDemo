package com.specimen.f1_camera;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Size;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.camera.core.internal.utils.ImageUtil;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.ImageUtils;
import com.bumptech.glide.Glide;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CameraxActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageCapture imageCapture;
    private File outputDirectory;
    private ExecutorService cameraExecutor;
    private CameraSelector cameraSelector;

    private ImageButton btnCaptor;
    private ImageButton btnChange;

    private Button btnTakingFinish;
    private ImageButton btnTakingCancel;
    private View afterTakingRoot;
    private ImageView imageAfterTaking;
    private ImageButton btnBack;
    private ImageButton btnAfterTakingRotateRight;
    private ImageButton btnAfterTakingRotateLeft;
    private ImageButton btnCameraDirection;
    public final static int RESULT_PHOTO_PATH = 6583;
    public final static int RESULT_BACK = 9834;
    public final static String RESULT_DATA_PHOTO_PATH = "photo_path";
    private String savePath;
    ProcessCameraProvider provider = null;
    ListenableFuture<ProcessCameraProvider> future;
    private Bitmap bitmap;
    boolean isOpenCamera = false;
    Preview preview;
    String fileName;

    boolean isBackCamera = true; //判断是否是前置摄像头 ture: 高拍仪 false: 摄像头
    private int backDetectDirection;//高拍仪旋转角度
    private int frontDetectDirection;//前置摄像头旋转角度
    private PreviewView viewFinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camerax);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 888261);
            }
        }

        btnCaptor = findViewById(R.id.btn_captrue);
        btnChange = findViewById(R.id.btn_change_camera);
        btnTakingFinish = findViewById(R.id.after_taking_finish);
        btnTakingCancel = findViewById(R.id.after_taking_back);
        afterTakingRoot = findViewById(R.id.camerax_after_taking);
        imageAfterTaking = findViewById(R.id.after_taking_image);
        btnBack = findViewById(R.id.btn_taking_back);
        btnAfterTakingRotateLeft = findViewById(R.id.after_taking_btn_rotate_left);
        btnAfterTakingRotateRight = findViewById(R.id.after_taking_btn_rotate_right);
        btnCameraDirection = findViewById(R.id.btn_camera_direction);
        btnCameraDirection.setOnClickListener(this);

        backDetectDirection = ConfigManager.get(this).getBackDetectDirection();
        frontDetectDirection = ConfigManager.get(this).getFrontDetectDirection();

        getPermission();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");
        fileName = format.format(new Date()) + ".jpg";

        cameraExecutor = Executors.newSingleThreadExecutor();

        outputDirectory = getOutputDirectory();

        cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

        // 创建一个Preview 实例，并设置该实例的 surface 提供者（provider）。
        viewFinder = (PreviewView) findViewById(R.id.preview);

        preview = new Preview.Builder()
                //.setTargetRotation(Surface.ROTATION_180)
                .setTargetResolution(new Size(1920, 1080))
                .build();
        preview.setSurfaceProvider(viewFinder.getSurfaceProvider());

        startCamera();

        btnAfterTakingRotateLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //bitmap.recycle();
                bitmap = BitmapUtils.drawableToBitmap(imageAfterTaking.getDrawable());
                bitmap = BitmapUtils.adjustPhotoRotation(bitmap, -90);
                imageAfterTaking.setImageBitmap(bitmap);
            }
        });

        btnAfterTakingRotateRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //bitmap.recycle();
                bitmap = BitmapUtils.drawableToBitmap(imageAfterTaking.getDrawable());
                bitmap = BitmapUtils.adjustPhotoRotation(bitmap, 90);
                imageAfterTaking.setImageBitmap(bitmap);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                //intent.putExtra("photo_path", savePath);
                setResult(RESULT_BACK, intent);
                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                }
                finish();
            }
        });
        btnTakingCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                afterTakingRoot.setVisibility(View.GONE);
            }
        });

        btnTakingFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BitmapUtils.saveBitmap(outputDirectory.getPath(), fileName, BitmapUtils.drawableToBitmap(imageAfterTaking.getDrawable()));

                Intent intent = new Intent();
                intent.putExtra(RESULT_DATA_PHOTO_PATH, savePath);
                setResult(RESULT_PHOTO_PATH, intent);
                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                }
                finish();
            }
        });

        btnCaptor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isOpenCamera) {
                    Toast.makeText(CameraxActivity.this, "摄像头没有开启", Toast.LENGTH_SHORT).show();
                    return;
                }
                takePhoto();
            }
        });

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isBackCamera = !isBackCamera;
                startCamera();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void getPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                    return;
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }

    @SuppressLint("RestrictedApi")
    private void startCamera() {
        // 将Camera的生命周期和Activity绑定在一起（设定生命周期所有者），这样就不用手动控制相机的启动和关闭。
        future = ProcessCameraProvider.getInstance(this);

        future.addListener(() -> {
            try {
                // 将你的相机和当前生命周期的所有者绑定所需的对象
                provider = future.get();

                if (isBackCamera) {
                    cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
                } else {
                    cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;
                }

                //根据人脸设置的旋转角度配置预览方向

                //摄像头
                if (!isBackCamera) {
                    if (frontDetectDirection == 90) {
                        preview.setTargetRotation(Surface.ROTATION_90);
                    } else if (frontDetectDirection == 180) {
                        preview.setTargetRotation(Surface.ROTATION_180);
                    } else if (frontDetectDirection == 270) {
                        preview.setTargetRotation(Surface.ROTATION_270);
                    } else {
                        preview.setTargetRotation(Surface.ROTATION_0);
                    }
                }

                //高拍仪
                if (isBackCamera) {
                    if (backDetectDirection == 90) {
                        preview.setTargetRotation(Surface.ROTATION_90);
                    } else if (backDetectDirection == 180) {
                        preview.setTargetRotation(Surface.ROTATION_180);
                    } else if (backDetectDirection == 270) {
                        preview.setTargetRotation(Surface.ROTATION_270);
                    } else {
                        preview.setTargetRotation(Surface.ROTATION_0);
                    }
                }

                // 选择后置摄像头作为默认摄像头
                //CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
                //PreviewConfig config = new PreviewConfig();
                // 创建拍照所需的实例
                imageCapture = new ImageCapture.Builder()
//                        .setMaxResolution(new Size(2500,1400))
//                        .setTargetResolution(new Size(2592, 1458))//设置拍摄图片的分辨率
//                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)//提示照片质量
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)//提示照片质量
//                        .setTargetAspectRatio(AspectRatio.RATIO_16_9)
//                        .setTargetRotation(preview())//与预览保持一致
//                        .setCameraSelector(cameraSelector)
                        .build();


                // 重新绑定用例前先解绑
                provider.unbindAll();

                Thread.sleep(500);

                // 绑定用例至相机
                provider.bindToLifecycle(CameraxActivity.this, this.cameraSelector, preview);

                isOpenCamera = true;
            } catch (Exception e) {
                isOpenCamera = false;
                Toast.makeText(this, "打开摄像头失败", Toast.LENGTH_SHORT).show();
                //Log.e(Configuration.TAG, "用例绑定失败！" + e);
            }
        }, ContextCompat.getMainExecutor(this));

    }

    private File getOutputDirectory() {
        File mediaDir = new File(getExternalMediaDirs()[0], "EPicture");
        boolean isExist = mediaDir.exists() || mediaDir.mkdir();
        return isExist ? mediaDir : null;
    }

    Handler handler = new Handler(Looper.getMainLooper());

    @SuppressLint("RestrictedApi")
    private void takePhoto() {
        btnCaptor.setClickable(false);
        // 确保imageCapture 已经被实例化, 否则程序将可能崩溃
        fileName = "cache.jpg";
        File photoFile = new File(outputDirectory, fileName);

        //镜像保存的图片，与预览一致
        ImageCapture.Metadata metadata = new ImageCapture.Metadata();
        //metadata.setReversedHorizontal(true);

        //metadata.setReversedVertical(true);

        // 创建 output option 对象，用以指定照片的输出方式
        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions
                .Builder(photoFile)
                .setMetadata(metadata)
                .build();
        if (imageCapture != null) {
            // 创建带时间戳的输出文件以保存图片，带时间戳是为了保证文件名唯一
            String fileName = "cache.jpg";
            try {
                //绑定摄像机
                future = ProcessCameraProvider.getInstance(this);
                provider = future.get();

                provider.bindToLifecycle(this, cameraSelector, imageCapture);
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }

            //imageCapture.setTargetRotation(Surface.ROTATION_270);

            //imageCapture.setTargetRotation(Surface.ROTATION_180);

            handler.post(new Runnable() {
                @Override
                public void run() {
                    afterTakingRoot.setVisibility(View.VISIBLE);
                    //imageAfterTaking.setImageURI(savedUri);
                    Bitmap bitmap = viewFinder.getBitmap();
                    savePath = outputDirectory + "/" + fileName;
                    boolean save = ImageUtils.save(bitmap, savePath, Bitmap.CompressFormat.JPEG);
//                    if (!save) {
//                        Toast.makeText(CameraxActivity.this, "图片保存失败，请重试！", Toast.LENGTH_SHORT).show();
//                    }
                    if (!isBackCamera) {
                        Glide.with(CameraxActivity.this).load(bitmap)
                                .transform(new RotateTransformation(frontDetectDirection))//图片翻转
                                //.transform(new RotateTransformation(90))
                                .into(imageAfterTaking);
                    } else {
                        Glide.with(CameraxActivity.this).load(bitmap)
                                .transform(new RotateTransformation(backDetectDirection))
                                //.transform(new RotateTransformation(90))
                                .into(imageAfterTaking);
                    }

                }
            });

            //保存的图片不够清晰，暂时找到的设置方法无效，直接使用预览的截图2023-06-14
//             执行takePicture（拍照）方法
//            imageCapture.takePicture(outputFileOptions,
//                    ContextCompat.getMainExecutor(this),
//                    new ImageCapture.OnImageSavedCallback() {// 保存照片时的回调
//                        @Override
//                        public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
//                            Uri savedUri = Uri.fromFile(photoFile);
//                            String msg = "照片捕获成功! " + savedUri;
//                            savePath = outputDirectory + "/" + fileName;
//                            //Glide.with(CameraxActivity.this).load(savePath).into(imageAfterTaking);
//                            handler.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    afterTakingRoot.setVisibility(View.VISIBLE);
//                                    //imageAfterTaking.setImageURI(savedUri);
//                                    if (!isBackCamera) {
//                                        Glide.with(CameraxActivity.this).load(viewFinder.getBitmap())
//                                                .transform(new RotateTransformation(frontDetectDirection))//图片翻转
//                                                //.transform(new RotateTransformation(90))
//                                                .into(imageAfterTaking);
//                                    } else {
//                                        Glide.with(CameraxActivity.this).load(viewFinder.getBitmap())
//                                                .transform(new RotateTransformation(backDetectDirection))
//                                                //.transform(new RotateTransformation(90))
//                                                .into(imageAfterTaking);
//                                    }
//
//                                }
//                            });
//                            Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
//                            Log.d("virgil", msg);
//                            btnCaptor.setClickable(true);
//                        }
//
//                        @Override
//                        public void onError(@NonNull ImageCaptureException exception) {
//                            Log.e("virgil", "Photo capture failed: " + exception.getMessage());
//                        }
//                    });

        }
    }

    private void setCameraDirection() {
        //摄像头
        if (!isBackCamera) {
            if (frontDetectDirection == 90) {
                preview.setTargetRotation(Surface.ROTATION_90);
            } else if (frontDetectDirection == 180) {
                preview.setTargetRotation(Surface.ROTATION_180);
            } else if (frontDetectDirection == 270) {
                preview.setTargetRotation(Surface.ROTATION_270);
            } else {
                preview.setTargetRotation(Surface.ROTATION_0);
            }
        }

        //高拍仪
        if (isBackCamera) {
            if (backDetectDirection == 90) {
                preview.setTargetRotation(Surface.ROTATION_90);
            } else if (backDetectDirection == 180) {
                preview.setTargetRotation(Surface.ROTATION_180);
            } else if (backDetectDirection == 270) {
                preview.setTargetRotation(Surface.ROTATION_270);
            } else {
                preview.setTargetRotation(Surface.ROTATION_0);
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_camera_direction) {
            if (!isOpenCamera) {
                return;
            }
            if (isBackCamera) {
                if (backDetectDirection == 180) {
                    backDetectDirection = 0;
                } else {
                    backDetectDirection = 180;
                }

                ConfigManager.get(this).setBackDetectDirection(backDetectDirection);
            } else {
                if (frontDetectDirection == 180) {
                    frontDetectDirection = 0;
                } else {
                    frontDetectDirection = 180;
                }

                ConfigManager.get(this).setFrontDetectDirection(frontDetectDirection);
            }
            setCameraDirection();
        }
    }
}