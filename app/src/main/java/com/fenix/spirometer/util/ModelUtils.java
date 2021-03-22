package com.fenix.spirometer.util;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.util.StringUtil;

import com.fenix.spirometer.model.BaseModel;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModelUtils {
    public static List<Method> getGetters(Class<? extends BaseModel> clazz, @NonNull String[] methodNames) {
        List<Method> methods = new ArrayList<>();
        try {
            for (String methodName : methodNames) {
                methods.add(clazz.getMethod(methodName));
            }
        } catch (NoSuchMethodException e) {
            return Collections.emptyList();
        }
        return methods;
    }

    public static <T extends BaseModel> Method getSetter(Class<? extends BaseModel> clazz, String methodName, Class<?> enterParam) {
        Method method;
        try {
            method = clazz.getMethod(methodName, enterParam);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            method = null;
        }
        return method;
    }

    public static <T extends BaseModel> boolean contains(@NonNull T t, List<Method> getters, @NonNull String str) {
        if (str.equals("")) {
            return true;
        }
        if (getters == null || getters.isEmpty()) {
            Field[] fields = t.getClass().getDeclaredFields();
            if (fields == null || fields.length == 0) {
                return false;
            }
            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    if (field.get(t) != null && String.valueOf(field.get(t)).contains(str)) {
                        return true;
                    }
                } catch (IllegalAccessException e) {
                    Log.e("hff", field.getName() + " not found! " + e.getMessage());
                }
            }
            return false;
        }

        for (Method getter : getters) {
            try {
                return String.valueOf(getter.invoke(t)).contains(str);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
