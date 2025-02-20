package com.specimen.f1_camera;

import android.content.Context;
import android.content.SharedPreferences;

public class ConfigManager {
    private static ConfigManager instance;
    private final String NAME = "CameraConfig";
    private final String FRONT_DETECT_DIRECTION = "FrontDetectDirection";
    private final String BACK_DETECT_DIRECTION = "BackDetectDirection";

    private SharedPreferences sharedPreferences;

    public static ConfigManager get(Context context){
        synchronized (ConfigManager.class){
            if(instance == null){
                instance = new ConfigManager(context);
            }
        }
        return instance;
    }

    private ConfigManager(Context context){
        sharedPreferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    public int getFrontDetectDirection(){
        return sharedPreferences.getInt(FRONT_DETECT_DIRECTION, 0);
    }

    public int getBackDetectDirection(){
        return sharedPreferences.getInt(BACK_DETECT_DIRECTION, 0);
    }

    public void setFrontDetectDirection(int value){
        sharedPreferences.edit().putInt(FRONT_DETECT_DIRECTION, value).apply();
    }

    public void setBackDetectDirection(int value){
         sharedPreferences.edit().putInt(BACK_DETECT_DIRECTION, value).apply();
    }
}
