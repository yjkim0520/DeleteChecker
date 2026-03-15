package com.deletechecker;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class Main extends Application {

    private ReviewQueue queue;
    private FileActionService actionService;
    
    // UI Elements
    private StackPane cardPane;
    private Label statusLabel;

    @Override
    public void start(Stage primaryStage) {
        // 1. Initialize our backend services
        queue = new ReviewQueue();
        actionService = new FileActionService();

        // 2. Set up the main layout (Top, Center, Bottom)
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root-pane"); // For CSS later

        // Top: Status Label (e.g., "0 files remaining")
        statusLabel = new Label("Drag and drop files here to begin!");
        statusLabel.getStyleClass().add("status-label");
        HBox topBox = new HBox(statusLabel);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(20));
        root.setTop(topBox);

        // Center: The "Card" where the preview goes
        cardPane = new StackPane();
        cardPane.getStyleClass().add("card");
        cardPane.setPrefSize(600, 600);
        root.setCenter(cardPane);

        // Bottom: The Action Buttons
        Button trashBtn = new Button("✗ Trash (Left Arrow)");
        trashBtn.getStyleClass().addAll("action-btn", "trash-btn");
        trashBtn.setOnAction(e -> handleAction(false));

        Button keepBtn = new Button("✓ Keep (Right Arrow)");
        keepBtn.getStyleClass().addAll("action-btn", "keep-btn");
        keepBtn.setOnAction(e -> handleAction(true));

        HBox bottomBox = new HBox(50, trashBtn, keepBtn);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(30));
        root.setBottom(bottomBox);

        // 3. Set up Drag-and-Drop
        setupDragAndDrop(root);

        // 4. Set up the Scene and Keyboard Shortcuts
        Scene scene = new Scene(root, 900, 800);
        
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.LEFT) {
                handleAction(false); // Swipe Left = Trash
            } else if (event.getCode() == KeyCode.RIGHT) {
                handleAction(true);  // Swipe Right = Keep
            }
        });

        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        primaryStage.setTitle("DeleteChecker - FileSwipe");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Handles dropping files onto the app window.
     */
    private void setupDragAndDrop(BorderPane root) {
        root.setOnDragOver(event -> {
            if (event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });

        root.setOnDragDropped(event -> {
            List<File> droppedFiles = event.getDragboard().getFiles();
            queue.addFiles(droppedFiles);
            updateUI(); // Refresh the screen to show the first file
            event.setDropCompleted(true);
            event.consume();
        });
    }

    /**
     * Executes the Keep or Trash action and loads the next file.
     */
    private void handleAction(boolean keep) {
        if (queue.isEmpty()) return;

        ReviewItem currentItem = queue.getNext(); // Pop it off the queue

        if (keep) {
            actionService.keepFile(currentItem);
        } else {
            actionService.trashFile(currentItem);
        }

        updateUI(); // Load the next one
    }

    /**
     * Refreshes the Center Card and Top Label based on the queue state.
     */
    private void updateUI() {
        cardPane.getChildren().clear(); // Clear the old preview

        if (queue.isEmpty()) {
            statusLabel.setText("All done! Drag more files to continue.");
            return;
        }

        ReviewItem nextItem = queue.peekCurrent();
        statusLabel.setText(queue.getRemainingCount() + " files remaining");

        // Use our Factory to get the right UI element!
        Node previewNode = PreviewFactory.createPreview(nextItem);
        cardPane.getChildren().add(previewNode);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
