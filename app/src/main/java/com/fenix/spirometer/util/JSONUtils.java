package com.fenix.spirometer.util;

import android.text.TextUtils;

import com.fenix.spirometer.model.BaseModel;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.List;

public class JSONUtils {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static <T> String model2Json(T model) {
        try {
            return mapper.writeValueAsString(model);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static <T> T json2Model(String jsonStr, Class<T> clazz) {
        if (TextUtils.isEmpty(jsonStr) || clazz == null) {
            return null;
        }
        try {
            return mapper.readValue(jsonStr, clazz);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> String modelList2Json(List<T> models) {
        try {
            return mapper.writeValueAsString(models);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}
