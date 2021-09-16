package com.sbytestream;

import java.util.List;

public class ManifestData {

    private Integer chunks;
    private String filename;

    public ManifestData() {}

    public ManifestData(Integer chunks, String filename) {
        this.chunks = chunks;
        this.filename = filename;
    }

    public Integer getChunks() {
        return chunks;
    }

    public void setChunks(int chunks) {
        this.chunks = chunks;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public static ManifestData parse(List<String> lineList) throws AppException {
        ManifestData data = new ManifestData();

        if (lineList == null) {
            return data;
        }

        for (String line : lineList) {
            String[] lines = line.split(":");

            if (lines.length != 2) {
                throw new AppException("Invalid manifest file");
            }

            String key = lines[0].trim();
            String val = lines[1].trim();

            if (key.equalsIgnoreCase("chunks")) {
                data.setChunks(Integer.parseInt(val));
            }
            else if (key.equalsIgnoreCase("filename")) {
                data.setFilename(val);
            }
        }
        return data;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("chunks: %s", chunks));
        sb.append(System.getProperty("line.separator"));
        sb.append(String.format("filename: %s", filename));
        return sb.toString();
    }
}
