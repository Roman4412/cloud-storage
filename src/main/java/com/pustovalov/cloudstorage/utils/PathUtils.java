package com.pustovalov.cloudstorage.utils;

import com.pustovalov.cloudstorage.entity.User;

public class PathUtils {

    public static final String SLASH = "/";

    public static String getRootNameFor(User user) {
        return "%d-%s-root".formatted(user.getId(), user.getUsername());
    }

    public static String removeRoot(String path) {
        String[] parts = path.split(SLASH);
        int length = (parts[1] + SLASH).length();
        int endIndex = path.length();
        return path.substring(length, endIndex);
    }

}