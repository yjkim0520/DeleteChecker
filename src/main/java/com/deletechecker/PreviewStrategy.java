package com.deletechecker;

import javafx.scene.Node;

/**
 * The common interface for all file preview generators.
 */
public interface PreviewStrategy {
    
    /**
     * Generates a JavaFX UI component (Node) to display the file's preview.
     * * @param item The ReviewItem containing the file to preview.
     * @return A JavaFX Node (like an ImageView or TextArea) containing the preview.
     */
    Node generatePreview(ReviewItem item);
}
