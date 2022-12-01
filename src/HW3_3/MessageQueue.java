package HW3_3;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class MessageQueue {
    private static int n_ids;

    public static void main(String[] args) {
		BlockingQueue<Message> queue = new ArrayBlockingQueue<>(20);
		int n = 0;
		int m = 0;
		if (args.length >= 2) {
			n = Integer.parseInt(args[0]);
			m = Integer.parseInt(args[1]);
		} else {
			System.out.println("please provide producer number and consumer number");
			return;
		}

		Producer[] producers = new Producer[n];
		Thread[] threads = new Thread[n];

		for (int i = 0; i < n; i++) {
			producers[i] = new Producer(queue, n_ids++);
			Thread t = new Thread(producers[i]);
			t.start();
			threads[i] = t;
		}

		for (int i = 0; i < m; i++) {
			Consumer c1 = new Consumer(queue, n_ids++);
			new Thread(c1).start();
		}


		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < n; i++) {
			producers[i].stop();
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		if (n > 0 && m > n){
			producers[0].sendAdditionalStop(m-n);
		}
	}
}
