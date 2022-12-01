package HW4_2;/*
 * @author Crista Lopes
 * Simple word frequency program
 */

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.stream.Stream;

public class FrequencyCount {
    private static final List<String> stop_words = new ArrayList<String>();
    private static CyclicBarrier barrier;
    private static Counter[] counters;


    static final class Counter implements Runnable {
        private HashMap<String, Integer> frequencies = new HashMap<String, Integer>();
        private Path filepath;

        private Counter(Path filepath) {
            this.filepath = filepath;
        }

        // Keep only the non stop words with 3 or more characters
        private void process(String line) {
            String[] words = line.split("\\W+");
            for (String word : words) {
                String w = word.toLowerCase();
                if (!stop_words.contains(w) && w.length() > 2) {
                    if (frequencies.containsKey(w))
                        frequencies.put(w, frequencies.get(w) + 1);
                    else
                        frequencies.put(w, 1);
                }
            }
        }

        private List<Map.Entry<String, Integer>> sort() {
            Set<Map.Entry<String, Integer>> set = frequencies.entrySet();
            List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(
                    set);
            Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
                public int compare(Map.Entry<String, Integer> o1,
                                   Map.Entry<String, Integer> o2) {
                    return o2.getValue().compareTo(o1.getValue());
                }
            });
            return list;
        }

        private HashMap<String, Integer> getFrequencies() {
            return frequencies;
        }

        public void merge(Counter other) { // should be used for barriar
            other.getFrequencies().forEach((k, v) -> frequencies.merge(k, v, Integer::sum));
        }

        // Only the top 40 words that are 3 or more characters
        public String toString() { // only used when all outcome are available
            List<Map.Entry<String, Integer>> sortedMap = sort();
            StringBuilder sb = new StringBuilder("---------- Word counts (top 40) -----------\n");
            int i = 0;
            for (Map.Entry<String, Integer> e : sortedMap) {
                String k = e.getKey();
                sb.append(k + ":" + e.getValue() + "\n");
                if (i++ > 40)
                    break;
            }
            return sb.toString();
        }

        @Override
        public void run() {
            try {
                try (Stream<String> lines = Files.lines(filepath /*Paths.get(filename)*/)) {
                    lines.forEach(line -> {
                        process(line);
                    });
                    System.out.println("Ended " + filepath);
                    barrier.await();
                } catch (BrokenBarrierException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static final class Merger implements Runnable {

        @Override
        public void run() {
            if (counters.length >= 1){
                Counter merged = counters[0];
                for (int i=1; i<counters.length;i++){
                    merged.merge(counters[i]);
                }
                System.out.println(merged);
            }

        }
    }

    private static void loadStopWords() {
        String str = "";
        try {
            byte[] encoded = Files.readAllBytes(Paths.get("src/HW4_2/stop_words.md"));
            str = new String(encoded);
        } catch (IOException e) {
            System.out.println("Error reading stop_words.md");
            e.printStackTrace();
        }
        String[] words = str.split(",");
        stop_words.addAll(Arrays.asList(words));
    }

    private static void countWords(Path p, Counter c) {
        System.out.println("Started " + p);
        new Thread(c).start();

    }

    public static void main(String[] args) {

        loadStopWords();

        long start = System.nanoTime();
        try {
            try (Stream<Path> paths = Files.walk(Paths.get("./src/HW4_2/"))) {

                ArrayList<Path> pathArr = new ArrayList<>();
                paths.filter(p -> p.toString().endsWith(".txt")).forEach(p -> {
                    pathArr.add(p);
                });

                barrier = new CyclicBarrier(pathArr.size(), new Merger());
                counters = new Counter[pathArr.size()];
                for (int i = 0; i < pathArr.size();i++) {
                    Counter c = new Counter(pathArr.get(i));
                    counters[i] = c;
                    countWords(pathArr.get(i), c);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        long end = System.nanoTime();

        long elapsed = end - start;


        System.out.println("Elapsed time: " + elapsed / 1000000 + "ms");

    }
}
