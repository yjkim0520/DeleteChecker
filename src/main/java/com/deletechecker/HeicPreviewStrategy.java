package com.deletechecker;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.File;

public class HeicPreviewStrategy implements PreviewStrategy {

    @Override
    public Node generatePreview(ReviewItem item) {
        try {
            // Create a temporary hidden file to store the converted JPEG
            File tempJpg = File.createTempFile("heic_preview_", ".jpg");
            tempJpg.deleteOnExit(); // Ensure it gets cleaned up later

            // Use Mac's built-in 'sips' tool to convert the HEIC to our temp JPG
            ProcessBuilder pb = new ProcessBuilder(
                "sips", "-s", "format", "jpeg", 
                item.getFile().getAbsolutePath(), 
                "--out", tempJpg.getAbsolutePath()
            );
            Process process = pb.start();
            process.waitFor(); // Wait for the tiny fraction of a second it takes to convert

            // Now display the temporary JPG just like a normal image
            Image image = new Image(tempJpg.toURI().toString());
            ImageView imageView = new ImageView(image);
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(600);
            imageView.setFitHeight(600);
            
            return imageView;

        } catch (Exception e) {
            return new Label("Unable to process HEIC file:\n" + e.getMessage());
        }
    }
}
