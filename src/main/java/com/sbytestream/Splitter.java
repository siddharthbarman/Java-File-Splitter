package com.sbytestream;

import java.io.*;

public class Splitter {
    public static void help() {
        System.out.println("Splits or join files.");
        System.out.println();
        System.out.println("Syntax:");
        System.out.println("splitter -mode -n size_in_bytes -i filepath -o filepath");
        System.out.println();
        System.out.println("Split example:");
        System.out.println("splitter -s -n 1024 -i example.zip -o ./output/splitted");
        System.out.println("The example will split the input file example.zip into multiple files of 1 KB size.");
        System.out.println("The split files will be stored in a folder named output, the split files will be named");
        System.out.println("splitted.001, splitted.002, spitted.003 and so on.");
        System.out.println();
        System.out.println("Join example:");
        System.out.println("splitter -j -i splitted.001 -o ./output/joined.zip");
        System.out.println("The example will join all the files starting with 001 and create a new file named joined.zip in a folder named output");
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            help();
            return;
        }

        try {
            new Splitter().run(args);
        }
        catch(Exception e) {
            System.out.println("Something went wrong! Exception:");
            System.out.println(e.getMessage());
        }
    }

    public void run(String[] args) throws IOException, AppException {
        CmdLineParser cmd = new CmdLineParser(args);
        ValidationResult r = validate(cmd);
        if (!r.isResult()) {
            System.out.println(r.getMessage());
            return;
        }

        String input = cmd.getParamValue(FLAG_INPUT_FILE);
        String output = cmd.getParamValue(FLAG_OUTPUT_FILE);

        if (cmd.hasFlag(FLAG_MODE_SPLIT)) {
            File infile = new File(input);
            if (!infile.exists() || !infile.isFile()) {
                System.out.println("Input file does not exist or is not a file.");
                return;
            }

            if (infile.length() < INTERNAL_BUFFER_SIZE) {
                System.out.println("File too small! Size of the input file should at least 4096 bytes.");
                return;
            }

            File outfile = new File(output);
            if (outfile.exists()) {
                System.out.println("Output file already exists.");
                return;
            }

            int chunkSize = Integer.parseInt(cmd.getParamValue(FLAG_SIZE));
            if (infile.length() / chunkSize > 999) {
                System.out.println("Chunk size too small, maximum 999 chunks can be created.");
                return;
            }

            split(input , output, chunkSize);
        }
        else if (cmd.hasFlag(FLAG_MODE_JOIN)) {
            File infile = new File(input);
            if (!infile.exists() || !infile.isFile()) {
                System.out.println("Input file does not exist or is not a file.");
                return;
            }

            File outfile = new File(output);
            if (outfile.exists()) {
                System.out.println("Output file already exists.");
                return;
            }

            join(input, output);
        }
    }

    public ValidationResult validate(CmdLineParser cmd) {
        if (!cmd.hasFlag(FLAG_MODE_SPLIT) && !cmd.hasFlag(FLAG_MODE_JOIN)) {
            return new ValidationResult(false, "mode has not been specified. Use either -s or -j.");
        }

        if (cmd.hasFlag(FLAG_MODE_SPLIT)) {
            String sizeString = cmd.getParamValue(FLAG_SIZE);
            if (sizeString == null) {
                return new ValidationResult(false, "Size has not been specified. Use the -n option.");
            }

            try {
                int size = Integer.parseInt(sizeString);
                if (size < INTERNAL_BUFFER_SIZE) {
                    return new ValidationResult(false, "Size must be equal or greater than 4096 bytes.");
                }
                if (size % INTERNAL_BUFFER_SIZE != 0) {
                    return new ValidationResult(false, "Size must multiples of 4096 bytes.");
                }

            }
            catch (NumberFormatException e) {
                return new ValidationResult(false, "Invalid size has been specified.");
            }
        }

        if (cmd.getParamValue(FLAG_INPUT_FILE) == null) {
            return new ValidationResult(false,"Input file not specified.");
        }

        if (cmd.getParamValue(FLAG_OUTPUT_FILE) == null) {
            return new ValidationResult(false,"Output file not specified.");
        }

        return new ValidationResult(true, null);
    }

    public static String getFileName(String filename, int chunkNumber) {
        return String.format("%s.%03d", filename, chunkNumber);
    }

    private void split(String input, String output, int size) throws IOException {
        byte[] buffer = new byte[INTERNAL_BUFFER_SIZE];
        int bytesRead = 0;
        int bytesLeftForChunk = size;
        int chunkNo = 0;
        FileOutputStream fos = null;
        long totalBytesWritten = 0;

        try (FileInputStream fis = new FileInputStream(input)) {
            String outputPath = null;
            while ((bytesRead = fis.read(buffer)) != -1) {
                if (bytesLeftForChunk == size) {
                    chunkNo++;
                    outputPath = getFileName(output, chunkNo);
                    if (new File(outputPath).exists()) {
                        System.out.println(String.format("%s already exists. Quitting.", outputPath));
                        return;
                    }
                    fos = new FileOutputStream(outputPath);
                }

                if (bytesRead < INTERNAL_BUFFER_SIZE) {
                    byte[] lastChunkBuffer = java.util.Arrays.copyOf(buffer, bytesRead);
                    fos.write(lastChunkBuffer);
                    fos.close();
                    bytesLeftForChunk = bytesLeftForChunk - bytesRead;
                    System.out.println(String.format("%s (%d)", outputPath, size - bytesLeftForChunk));
                    bytesLeftForChunk = 0;
                    totalBytesWritten += bytesRead;
                }
                else {
                    fos.write(buffer);
                    totalBytesWritten += INTERNAL_BUFFER_SIZE;
                    bytesLeftForChunk = bytesLeftForChunk - bytesRead;
                    if (bytesLeftForChunk == 0) {
                        fos.close();
                        System.out.println(String.format("%s (%d)", outputPath, size - bytesLeftForChunk));
                        bytesLeftForChunk = size;
                    }
                }
            }
            System.out.println(String.format("Total bytes written: %d, chunks created: %d", totalBytesWritten, chunkNo));
        }

        if (chunkNo > 0) {
            int indexOfSlash =  output.lastIndexOf(File.separatorChar);
            String outputFilenameOnly;
            if (indexOfSlash == -1 ) {
                // we have just a filename
                outputFilenameOnly = output;
            }
            else {
                // take the filename only
                outputFilenameOnly = output.substring(indexOfSlash + 1);
            }

            writeManifest(output + ".split", new ManifestData(chunkNo, outputFilenameOnly));

        }
    }

    private void join(String input, String output) throws IOException, AppException {
        ManifestData manifest = readManifest(input);
        String pathWithoutExtension = PathUtils.getFilePathWithoutExtension(input);
        long totalSize = 0;

        try (FileOutputStream writer = new FileOutputStream(output)) {
            for (int chunk = 1; chunk <= manifest.getChunks(); chunk++) {
                String chunkFilename = getFileName(pathWithoutExtension, chunk);
                totalSize += appendContentTo(chunkFilename, writer);
                System.out.println(chunkFilename);
            }
        }

        System.out.println(String.format("Joined file: %s, bytes read and written: %d", output, totalSize));
    }

    private long appendContentTo(String sourceFile, FileOutputStream writer) throws IOException {
        try (FileInputStream reader = new FileInputStream(sourceFile)) {
            long totalBytesWritten = 0;
            byte[] buffer = new byte[INTERNAL_BUFFER_SIZE];
            int bytesRead = 0;
            while ((bytesRead = reader.read(buffer)) != -1) {
                if (bytesRead < INTERNAL_BUFFER_SIZE) {
                    byte[] lastChunkBuffer = java.util.Arrays.copyOf(buffer, bytesRead);
                    writer.write(lastChunkBuffer);
                    totalBytesWritten += bytesRead;
                }
                else {
                    writer.write(buffer);
                    totalBytesWritten += INTERNAL_BUFFER_SIZE;
                }
            }
            return totalBytesWritten;
        }
    }

    private void writeManifest(String filepath, ManifestData data) throws IOException {
        try (FileWriter manifest = new FileWriter(filepath)) {
            manifest.write(data.toString());
        }
    }

    private ManifestData readManifest(String filepath) throws IOException, AppException {
        try (BufferedReader manifest = new BufferedReader(new FileReader(filepath))) {
            String line = null;
            ManifestData data = new ManifestData();

            while ((line = manifest.readLine()) != null) {
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
    }

    public static final String FLAG_MODE_SPLIT = "s";
    public static final String FLAG_MODE_JOIN = "j";
    public static final String FLAG_SIZE = "n";
    public static final String FLAG_INPUT_FILE = "i";
    public static final String FLAG_OUTPUT_FILE = "o";
    public static int INTERNAL_BUFFER_SIZE = 1024 * 4;
}
