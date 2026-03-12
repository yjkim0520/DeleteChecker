package com.deletechecker;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ReviewItem {
    private final File file;
    private final String fileName;
    private final long fileSize;
    private final String extension;

    public ReviewItem(File file) {
        this.file = file;
        this.fileName = file.getName();
        this.fileSize = calculateSize(file);
        this.extension = extractExtension(fileName);
    }

    // Helper method to safely get the file size
    private long calculateSize(File file) {
        try {
            return Files.size(file.toPath());
        } catch (IOException e) {
            System.err.println("Could not read size for: " + file.getName());
            return 0L;
        }
    }

    // Helper method to figure out if it's a .jpg, .txt, .pdf, etc.
    private String extractExtension(String name) {
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // No extension found
        }
        return name.substring(lastIndexOf + 1).toLowerCase();
    }

    // Getters so our UI and Preview Engine can read this data later
    public File getFile() { return file; }
    public String getFileName() { return fileName; }
    public long getFileSize() { return fileSize; }
    public String getExtension() { return extension; }
}
