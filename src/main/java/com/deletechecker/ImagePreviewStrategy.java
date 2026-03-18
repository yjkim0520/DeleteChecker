package com.deletechecker;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class ImagePreviewStrategy implements PreviewStrategy {

    @Override
    public Node generatePreview(ReviewItem item) {
        VBox container = new VBox(10);
        container.setAlignment(Pos.CENTER);
        
        try {
            String fileUri = item.getFile().toURI().toString();
            Image image = new Image(fileUri);
            
            ImageView imageView = new ImageView(image);
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(550);  
            imageView.setFitHeight(450); // Slightly smaller to make room for text
            
            // Extract the exact width and height of the image
            int width = (int) image.getWidth();
            int height = (int) image.getHeight();
            
            // Calculate file size
            double sizeInMB = item.getFileSize() / (1024.0 * 1024.0);
            String sizeStr = sizeInMB > 1.0 ? String.format("%.2f MB", sizeInMB) : String.format("%.2f KB", item.getFileSize() / 1024.0);

            // Create a nice detail label
            Label detailLabel = new Label(String.format("%s  |  %d x %d px  |  %s", 
                item.getFileName(), width, height, sizeStr));
            detailLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #b2bec3; -fx-font-weight: bold;");
            
            container.getChildren().addAll(imageView, detailLabel);
            return container;
            
        } catch (Exception e) {
            return new Label("Unable to load image:\n" + e.getMessage());
        }
    }
}
