package com.deletechecker;

import javafx.scene.Node;
import javafx.scene.control.Label;
import java.util.Set;

public class PreviewFactory {
    
    // Using Sets makes looking up extensions lightning fast (O(1) time complexity)
    private static final Set<String> IMAGE_EXTENSIONS = Set.of("png", "jpg", "jpeg", "gif", "bmp");
    private static final Set<String> TEXT_EXTENSIONS = Set.of("txt", "md", "csv", "json", "xml", "log", "java");

    /**
     * Inspects the ReviewItem and delegates it to the correct Strategy.
     */
    public static Node createPreview(ReviewItem item) {
        String ext = item.getExtension();

        if (IMAGE_EXTENSIONS.contains(ext)) {
            return new ImagePreviewStrategy().generatePreview(item);
            
        } else if (TEXT_EXTENSIONS.contains(ext)) {
            return new TextPreviewStrategy().generatePreview(item);
            
        } else {
            // The "I don't know what this file is" fallback
            String fallbackText = String.format(
                "No preview available for this file type.\n\nFile: %s\nSize: %d KB", 
                item.getFileName(), 
                (item.getFileSize() / 1024)
            );
            return new Label(fallbackText);
        }
    }
}
