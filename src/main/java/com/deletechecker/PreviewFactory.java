package com.deletechecker;

import javafx.scene.Node;
import java.util.Set;

public class PreviewFactory {
    
    // We only explicitly check for images now
    private static final Set<String> IMAGE_EXTENSIONS = Set.of("png", "jpg", "jpeg", "gif", "bmp");

    public static Node createPreview(ReviewItem item) {
        String ext = item.getExtension().toLowerCase();

        if (IMAGE_EXTENSIONS.contains(ext)) {
            // Show the actual picture
            return new ImagePreviewStrategy().generatePreview(item);
        } else {
            // Show the smart summary card for EVERYTHING else (PDFs, ZIPs, TXTs, etc.)
            return new SummaryPreviewStrategy().generatePreview(item);
        }
    }
}
