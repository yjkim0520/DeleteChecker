package com.deletechecker;

import java.awt.Desktop;
import java.io.File;

public class FileActionService {

    /**
     * Safely moves the file to the OS Trash/Recycle Bin.
     * Returns true if successful, false otherwise.
     */
    public boolean trashFile(ReviewItem item) {
        File file = item.getFile();
        
        // Check if the current OS supports the Desktop API and the Move to Trash action
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.MOVE_TO_TRASH)) {
            return Desktop.getDesktop().moveToTrash(file);
        } else {
            // Fallback for environments that don't support the Trash (like some Linux distros)
            System.err.println("Trash not supported on this OS. Attempting permanent deletion: " + file.getName());
            return file.delete(); 
        }
    }

    /**
     * Keeping a file requires no OS action, but having this method
     * keeps our UI code clean and gives us a place to add logging later.
     */
    public void keepFile(ReviewItem item) {
        // We can add logging here later if needed
        System.out.println("Kept: " + item.getFileName());
    }
}
