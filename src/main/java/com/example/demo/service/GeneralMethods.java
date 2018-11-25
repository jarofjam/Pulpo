package com.example.demo.service;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GeneralMethods {
    private static String[] getIgnoreProperties(@NotNull Object source, List<String> premadeIgnoreList) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> ignoreNames = new HashSet<>();
        ignoreNames.addAll(premadeIgnoreList);

        for(java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) ignoreNames.add(pd.getName());
        }

        String[] result = new String[ignoreNames.size()];
        return ignoreNames.toArray(result);
    }

    public static <T1, T2> T2 convert(@NotNull T1 input, T2 output, List<String> premadeIgnoreList) {
        String[] ignoreProperties = getIgnoreProperties(input, premadeIgnoreList);

        BeanUtils.copyProperties(input, output, ignoreProperties);

        return output;
    }
}
