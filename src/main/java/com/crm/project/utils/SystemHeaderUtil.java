package com.crm.project.utils;

import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;
import java.util.*;

@UtilityClass
public class SystemHeaderUtil {

    public static Set<String> getSystemHeaders(Class<?> className) {
        Field[] fields = className.getDeclaredFields();
        Set<String> headers = new LinkedHashSet<>();
        for (Field field : fields) {
            headers.add(field.getName());
        }
        return headers;
    }
}
