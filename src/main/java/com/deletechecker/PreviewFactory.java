package com.deletechecker;

import javafx.scene.Node;
import java.util.Set;

public class PreviewFactory {
    
    // Standard image formats natively supported by JavaFX
    private static final Set<String> IMAGE_EXTENSIONS = Set.of("png", "jpg", "jpeg", "gif", "bmp");

    public static Node createPreview(ReviewItem item) {
        String ext = item.getExtension().toLowerCase();

        if (IMAGE_EXTENSIONS.contains(ext)) {
            // Show standard images using JavaFX's built-in ImageView
            return new ImagePreviewStrategy().generatePreview(item);
            
        } else if (ext.equals("heic")) {
            // Handle Apple's HEIC format using our custom Mac conversion strategy
            return new HeicPreviewStrategy().generatePreview(item);
            
        } else {
            // For PDFs, text files, ZIPs, and everything else, use the smart summary card
            return new SummaryPreviewStrategy().generatePreview(item);
        }
    }
}
