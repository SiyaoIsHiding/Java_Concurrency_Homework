package HW3_1;

import java.util.HashMap;
import java.util.Vector;

public class HashMapTest {

    private volatile boolean running = true;
    private HashMap<String, Integer> people = new HashMap<String, Integer>();

    private void addPerson() {
        people.put(RandomUtils.randomString(), RandomUtils.randomInteger());
    } // safe

    private void deletePeople(String pattern) { // check and act
        Vector<String> hasPattern = new Vector<String>(); // safe and confined
        synchronized (people) { // client side locking
            for (String key : people.keySet()) {
                if (key.contains(pattern)) hasPattern.add(key);
            }
        }
        for (String key : hasPattern)
            people.remove(key);
    }

    private void printPeople() { // iteration
        synchronized (people) { // client side locking
            for (HashMap.Entry<String, Integer> entry : people.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
        }
        System.out.println("-----------------------------------------");
    }

    public void run() {
        // Start printer thread
        new Thread(new Runnable() {
            public void run() {
                Thread.currentThread().setName("Printer");
                while (running) {
                    printPeople();
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }).start();

        // Start deleter thread
        new Thread(new Runnable() {
            public void run() {
                Thread.currentThread().setName("Deleter");
                while (running) {
                    deletePeople("a");
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }).start();

        // This thread adds entries
        for (int i = 0; i < 100; i++) {
            addPerson();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }
        }
        running = false;
    }

    public static void main(String[] args) {
        HashMapTest hm = new HashMapTest();
        hm.run();
    }

}
