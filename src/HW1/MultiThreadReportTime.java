package HW1;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadReportTime {
    public static void main(String[] args) {
        String instruction = "Here are your options:\n" +
                "a - Create a new thread\n" +
                "b - Stop a given thread (e.g. \"b 2\" kills thread 2)\n" +
                "c - Stop all threads and exit this program";

        int lastId = 0;
        HashMap<Integer, ReportTime> threads = new HashMap<Integer, ReportTime>();
        Scanner sc = new Scanner(System.in);
        ExecutorService pool = Executors.newFixedThreadPool(150);

        while (true){
            System.out.println(instruction);
            String command = sc.next();
            if (command.equals("a")){
                int newId = lastId+1;
                ReportTime thread = new ReportTime(newId);
                pool.submit(thread);
                threads.put(newId, thread);
                lastId = newId;
            } else if (command.equals("b")) {
                try{
                    int idToKill = sc.nextInt();
                    ReportTime threadToKill = threads.get(idToKill);
                    threadToKill.shouldStop = true;
                    threads.remove(idToKill);
                } catch (Exception e){
                    System.out.println("Wrong thread id");
                }
            } else if (command.equals("c")) {
                ReportTime[] remains = threads.values().toArray(new ReportTime[0]);
                for (ReportTime toKill :
                        remains) {
                    toKill.shouldStop = true;
                }
                pool.shutdown();
                return;
            }else{
                System.out.println("Wrong input");
            }
        }
    }
}
