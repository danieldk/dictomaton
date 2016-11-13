package eu.danieldk.dictomaton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.SortedSet;
import java.util.TreeSet;

public class Util {
    public static SortedSet<String> loadWordList(String resourceName) throws IOException {
        InputStream in = ClassLoader.getSystemResourceAsStream(resourceName);

        TreeSet<String> words = new TreeSet<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            String line;
            while ((line = reader.readLine()) != null) {
                words.add(line.trim());
            }

        }

        return words;
    }
}
