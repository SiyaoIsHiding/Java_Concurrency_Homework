package HW1;
import java.time.LocalTime;

public class ReportTime implements Runnable{
    private int id;
    public volatile boolean shouldStop;
    public ReportTime(int id){
        this.id = id;
        this.shouldStop = false;
    }
    @Override
    public void run() {
        while (true){
            if (shouldStop){
                return;
            }
            try {
                String time = LocalTime.now().toString();
                synchronized (System.out){
                    System.out.println("Hello World! I'm thread " + this.id + ". The time is " + time);
                }
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
