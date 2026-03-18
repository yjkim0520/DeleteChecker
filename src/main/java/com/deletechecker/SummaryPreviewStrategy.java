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
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

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
        // 5. Add a Content Snippet for Text OR PDF files
        boolean isTextFile = TEXT_EXTENSIONS.contains(item.getExtension().toLowerCase());
        boolean isPdf = item.getExtension().equalsIgnoreCase("pdf");

        if (isTextFile || isPdf) {
            Label snippetTitle = new Label("Content Preview:");
            snippetTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white; -fx-padding: 20 0 5 0;");
            
            TextArea snippetArea = new TextArea();
            snippetArea.setEditable(false);
            snippetArea.setWrapText(true);
            snippetArea.setPrefRowCount(12);
            snippetArea.setStyle("-fx-control-inner-background: #1e1e24; -fx-text-fill: #e0e0e0; -fx-font-family: monospace;");
            
            try {
                String previewText = "";
                if (isPdf) {
                    // --- NEW PDF READING LOGIC ---
                    try (PDDocument document = PDDocument.load(item.getFile())) {
                        PDFTextStripper stripper = new PDFTextStripper();
                        stripper.setEndPage(1); // Only read page 1 to keep it lightning fast
                        previewText = stripper.getText(document).trim();
                        if (previewText.isEmpty()) previewText = "[This PDF contains mostly images/scans and no readable text]";
                    }
                } else {
                    // --- EXISTING TEXT READING LOGIC ---
                    List<String> lines = Files.lines(item.getFile().toPath()).limit(15).toList();
                    previewText = String.join("\n", lines);
                }
                snippetArea.setText(previewText);
            } catch (Exception e) {
                snippetArea.setText("[Cannot extract content from this file]");
            }
            
            container.getChildren().addAll(snippetTitle, snippetArea);
        }

        return container;
    }
}
