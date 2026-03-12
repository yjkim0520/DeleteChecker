package com.deletechecker;

import javafx.scene.Node;
import javafx.scene.control.TextArea;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class TextPreviewStrategy implements PreviewStrategy {

    private static final int MAX_LINES = 50; // Only read the beginning of the file

    @Override
    public Node generatePreview(ReviewItem item) {
        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setWrapText(true);

        try {
            // Read up to MAX_LINES to avoid lagging the UI on massive text files
            List<String> lines = Files.lines(item.getFile().toPath())
                                      .limit(MAX_LINES)
                                      .toList();
            
            String previewText = String.join("\n", lines);
            if (lines.size() == MAX_LINES) {
                previewText += "\n\n... [File truncated for preview] ...";
            }
            
            textArea.setText(previewText);

        } catch (IOException e) {
            textArea.setText("Unable to read text file:\n" + e.getMessage());
        }

        return textArea;
    }
}
