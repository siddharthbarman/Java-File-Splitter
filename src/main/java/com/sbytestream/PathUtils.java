package com.sbytestream;

import java.io.File;

public class PathUtils {
    public static String getFilename(String filePath) {
        if (filePath == null || filePath == "") {
            return filePath;
        }

        int indexOfSlash =  filePath.lastIndexOf(File.separatorChar);

        if (indexOfSlash == -1 ) {
            return filePath; // we have just a filename
        }
        else {
            return filePath.substring(indexOfSlash + 1); // take the filename only
        }
    }

    public static String getFilenameWithoutExtention(String filePath) {
        if (filePath == null || filePath == "") {
            return filePath;
        }

        String filename = getFilename(filePath);
        int indexOfSlash =  filename.lastIndexOf(".");

        if (indexOfSlash == -1 ) {
            return filename; // there is no extension
        }
        else {
            return filename.substring(0, indexOfSlash); // take the filename only
        }
    }

    public static String getFilePathWithoutExtension(String filePath) {
        if (filePath == null || filePath == "") {
            return filePath;
        }

        int indexOfSlash =  filePath.lastIndexOf(".");

        if (indexOfSlash == -1 ) {
            return filePath; // there is no extension
        }
        else {
            return filePath.substring(0, indexOfSlash); // take the filename only
        }
    }

    public static String combinePaths(String ... parts) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for(String part : parts) {
            if (!first) {
                sb.append(File.separatorChar);
            }
            sb.append(part);
            first = false;
        }
        return sb.toString();
    }
}
