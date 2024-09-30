package de.htwg.filehandler.utils;


import java.util.ArrayList;
import java.util.List;

public class TextUtils {

    public static List<String> splitText(String text, int maxChunkSize) {
        List<String> chunks = new ArrayList<>();
        int len = text.length();
        int start = 0;

        while (start < len) {
            int end = Math.min(start + maxChunkSize, len);
            while (end > start && end < len && text.charAt(end) != ' ' && text.charAt(end) != '.') {
                end--;
            }
            if (end == start) { // Fallback if we're in a very long word
                end = Math.min(start + maxChunkSize, len);
            }
            chunks.add(text.substring(start, end));
            start = end;
        }

        return chunks;
    }
}
