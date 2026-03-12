package com.deletechecker;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImagePreviewStrategy implements PreviewStrategy {

    @Override
    public Node generatePreview(ReviewItem item) {
        try {
            // JavaFX Image requires a URI string, not a raw File object
            String fileUri = item.getFile().toURI().toString();
            Image image = new Image(fileUri);
            
            ImageView imageView = new ImageView(image);
            
            // Ensure the image doesn't stretch weirdly and fits nicely in our window
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(600);  // We can tweak these dimensions later
            imageView.setFitHeight(600);
            
            return imageView;
            
        } catch (Exception e) {
            // Fallback in case the image is corrupted or unreadable
            return new Label("Unable to load image:\n" + e.getMessage());
        }
    }
}
