package com.pustovalov.cloudstorage.utils;

public class PathUtils {

    public static final String PATH_SEPARATOR = "/";

    public static String parsePath(String requestURI) {
        return requestURI.replace("/folders", "");
    }

    public static String parsePath(String requestURI, String name) {
        String uriWithoutFolderName = requestURI.replace(PATH_SEPARATOR + name, "");
        return uriWithoutFolderName.replace("/folders", "");
    }

    public static String parseName(String uri) {
        String[] pathParts = uri.split(PATH_SEPARATOR);
        return pathParts[pathParts.length - 1];
    }

    public static String buildSubFoldersPath(String folderName, String fullPath) {
        return fullPath + PATH_SEPARATOR + folderName + PATH_SEPARATOR;
    }

    public static String buildFullPath(String username, String path) {
        return PATH_SEPARATOR + username + path;
    }
}