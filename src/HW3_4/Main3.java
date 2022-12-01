package HW3_4;

import java.util.concurrent.Semaphore;

public class Main3 {


    private static Semaphore sem = new Semaphore(1);
    private static void nap(int millisecs) {
        try {
            Thread.sleep(millisecs);
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
        }
    }


    private static void addRow(HighLevelDisplay d, String str) throws InterruptedException {
        sem.acquire();
        d.addRow(str);
        sem.release();
    }

    private static void deleteRow(HighLevelDisplay d, int row) throws InterruptedException {
        sem.acquire();
        d.deleteRow(row);
        sem.release();
    }
    private static void addProc(HighLevelDisplay d) throws InterruptedException {

        // Add a sequence of addRow operations with short random naps.
        for (int i = 0; i < 20; i++) {
            addRow(d,"AAAAAAAAAAAA  " + i);
            addRow(d,"BBBBBBBBBBBB  " + i);
            nap(500);
            nap(500);
        }
    }

    private static void deleteProc(HighLevelDisplay d) throws InterruptedException {

        // Add a sequence of deletions of row 0 with short random naps.
        for (int i = 0; i < 20; i++) {
            nap(2500);
            deleteRow(d,0);
            nap(500);
        }
    }

    public static void main(String[] args) {
        final HighLevelDisplay d = new JDisplay2();

        new Thread() {
            public void run() {
                try {
                    addProc(d);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }.start();


        new Thread() {
            public void run() {
                try {
                    deleteProc(d);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }.start();

    }
}