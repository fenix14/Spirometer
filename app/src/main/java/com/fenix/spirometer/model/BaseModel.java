package com.fenix.spirometer.model;

import androidx.annotation.NonNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BaseModel {
    public static List<Method> getOutputGetters(@NonNull String[] methodNames) {
        List<Method> methods = new ArrayList<>();
        try {
            for (String methodName : methodNames) {
                methods.add(Member.class.getMethod(methodName));
            }
        } catch (NoSuchMethodException e) {
            return Collections.emptyList();
        }
        return methods;
    }
}
