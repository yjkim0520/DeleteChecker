package com.deletechecker;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class SummaryPreviewStrategy implements PreviewStrategy {

    private static final Set<String> TEXT_EXTENSIONS = Set.of(
        "txt", "md", "csv", "json", "xml", "log", "java", "py", "html", "css"
    );

    @Override
    public Node generatePreview(ReviewItem item) {
        VBox container = new VBox(10); // Reduced spacing slightly to fit more data
        container.setAlignment(Pos.CENTER_LEFT);
        container.setStyle("-fx-padding: 20;");

        // 1. File Name & Path
        Label nameLabel = new Label(item.getFileName());
        nameLabel.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        Label pathLabel = new Label("Location: " + item.getFile().getParent());
        pathLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #636e72; -fx-wrap-text: true;");

        // 2. Deep Metadata (Type, Size, Read-Only, Hidden)
        String ext = item.getExtension().toUpperCase();
        String typeDesc = ext.isEmpty() ? "Unknown File Type" : ext + " File";
        
        double sizeInMB = item.getFileSize() / (1024.0 * 1024.0);
        String sizeStr = sizeInMB > 1.0 ? String.format("%.2f MB", sizeInMB) : String.format("%.2f KB", item.getFileSize() / 1024.0);
        
        String attributes = "";
        if (item.getFile().isHidden()) attributes += " [HIDDEN]";
        if (!item.getFile().canWrite()) attributes += " [READ-ONLY]";

        Label metaLabel = new Label(String.format("Type: %s  |  Size: %s %s", typeDesc, sizeStr, attributes));
        metaLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #b2bec3;");

        // 3. Last Modified Date
        String dateStr = "Unknown Date";
        try {
            BasicFileAttributes attr = Files.readAttributes(item.getFile().toPath(), BasicFileAttributes.class);
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy 'at' hh:mm a");
            dateStr = sdf.format(new Date(attr.lastModifiedTime().toMillis()));
        } catch (IOException e) { }
        
        Label dateLabel = new Label("Last Modified: " + dateStr);
        dateLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #b2bec3;");

        container.getChildren().addAll(nameLabel, pathLabel, metaLabel, dateLabel);

        // 4. Detailed Content Snippet (Now taller and reads more lines)
        boolean isTextFile = TEXT_EXTENSIONS.contains(item.getExtension().toLowerCase());
        boolean isPdf = item.getExtension().equalsIgnoreCase("pdf");

        if (isTextFile || isPdf) {
            Label snippetTitle = new Label("Content Preview:");
            snippetTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white; -fx-padding: 15 0 0 0;");
            
            TextArea snippetArea = new TextArea();
            snippetArea.setEditable(false);
            snippetArea.setWrapText(true);
            snippetArea.setPrefRowCount(18); // Increased from 12 to 18 lines for more detail
            snippetArea.setStyle("-fx-control-inner-background: #1e1e24; -fx-text-fill: #e0e0e0; -fx-font-family: monospace;");
            
            try {
                String previewText = "";
                if (isPdf) {
                    try (PDDocument document = PDDocument.load(item.getFile())) {
                        PDFTextStripper stripper = new PDFTextStripper();
                        stripper.setEndPage(2); // Now reads the first TWO pages of PDFs
                        previewText = stripper.getText(document).trim();
                        if (previewText.isEmpty()) previewText = "[This PDF contains mostly images/scans and no readable text]";
                    }
                } else {
                    // Reads up to 40 lines of text files instead of 15
                    List<String> lines = Files.lines(item.getFile().toPath()).limit(40).toList();
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
