package com.deletechecker;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class SummaryPreviewStrategy implements PreviewStrategy {

    // Files that we can safely extract text snippets from
    private static final Set<String> TEXT_EXTENSIONS = Set.of(
        "txt", "md", "csv", "json", "xml", "log", "java", "py", "html", "css"
    );

    @Override
    public Node generatePreview(ReviewItem item) {
        // VBox stacks our UI elements vertically
        VBox container = new VBox(15); 
        container.setAlignment(Pos.CENTER_LEFT);
        container.setStyle("-fx-padding: 30;"); // Gives it nice breathing room inside the card

        // 1. File Name
        Label nameLabel = new Label(item.getFileName());
        nameLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: white;");

        // 2. File Type Description
        String ext = item.getExtension().toUpperCase();
        String typeDesc = ext.isEmpty() ? "Unknown File Type" : ext + " File";
        Label typeLabel = new Label("Type: " + typeDesc);
        typeLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #b2bec3;");

        // 3. File Size (Formatted nicely into KB or MB)
        double sizeInMB = item.getFileSize() / (1024.0 * 1024.0);
        String sizeStr = sizeInMB > 1.0 
            ? String.format("Size: %.2f MB", sizeInMB)
            : String.format("Size: %.2f KB", item.getFileSize() / 1024.0);
        
        Label sizeLabel = new Label(sizeStr);
        sizeLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #b2bec3;");

        // 4. Last Modified Date
        String dateStr = "Unknown Date";
        try {
            BasicFileAttributes attr = Files.readAttributes(item.getFile().toPath(), BasicFileAttributes.class);
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy 'at' hh:mm a");
            dateStr = sdf.format(new Date(attr.lastModifiedTime().toMillis()));
        } catch (IOException e) {
            // Fails silently if OS blocks reading the date
        }
        Label dateLabel = new Label("Last Modified: " + dateStr);
        dateLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #b2bec3;");

        // Add the metadata to the screen
        container.getChildren().addAll(nameLabel, typeLabel, sizeLabel, dateLabel);

        // 5. Add a Content Snippet (Only if it's a safe text file)
        if (TEXT_EXTENSIONS.contains(item.getExtension().toLowerCase())) {
            Label snippetTitle = new Label("Content Preview:");
            snippetTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white; -fx-padding: 20 0 5 0;");
            
            TextArea snippetArea = new TextArea();
            snippetArea.setEditable(false);
            snippetArea.setWrapText(true);
            snippetArea.setPrefRowCount(12);
            snippetArea.setStyle("-fx-control-inner-background: #1e1e24; -fx-text-fill: #e0e0e0; -fx-font-family: monospace;");
            
            try {
                // Grab just the first 15 lines so the app doesn't freeze on massive logs
                List<String> lines = Files.lines(item.getFile().toPath()).limit(15).toList();
                String previewText = String.join("\n", lines);
                if (lines.size() == 15) {
                    previewText += "\n\n... [File truncated for preview] ...";
                }
                snippetArea.setText(previewText);
            } catch (Exception e) {
                snippetArea.setText("[Cannot read text content]");
            }
            
            container.getChildren().addAll(snippetTitle, snippetArea);
        }

        return container;
    }
}
