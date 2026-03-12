package com.deletechecker;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ReviewQueue {
    // We use a Queue interface with a LinkedList implementation for standard FIFO behavior
    private final Queue<ReviewItem> queue;

    public ReviewQueue() {
        this.queue = new LinkedList<>();
    }

    /**
     * Takes a list of raw Files (e.g., from a drag-and-drop event),
     * converts them to ReviewItems, and adds them to the end of the line.
     */
    public void addFiles(List<File> files) {
        for (File file : files) {
            // We only want to process actual files, not entire folders (for now)
            if (file.isFile()) {
                queue.offer(new ReviewItem(file));
            }
        }
    }

    /**
     * Removes and returns the next file in line.
     * Returns null if the queue is empty.
     */
    public ReviewItem getNext() {
        return queue.poll(); 
    }

    /**
     * Looks at the current file without removing it from the queue.
     * Useful for updating the UI preview.
     */
    public ReviewItem peekCurrent() {
        return queue.peek();
    }

    /**
     * Checks if we are done reviewing all files.
     */
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    /**
     * Gets the number of files left. Great for updating a "5 files remaining" UI label.
     */
    public int getRemainingCount() {
        return queue.size();
    }
}
