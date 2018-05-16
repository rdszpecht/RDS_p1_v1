import java.util.NoSuchElementException;
import java.util.concurrent.*;
import java.net.URL;
import java.io.*;
import java.nio.file.*;

public class ThreadCode implements Runnable{

    private final String initialPath = "./downloads";
    private LinkedBlockingDeque<String> linkList;
    private CyclicBarrier barrier;
    private CountDownLatch latch;

    public ThreadCode(CyclicBarrier barrier, CountDownLatch latch, LinkedBlockingDeque<String> linkList){
        this.linkList = linkList;
        this.barrier = barrier;
        this.latch = latch;
    }

    public void setLinks(LinkedBlockingDeque<String> links){
        this.linkList = links;
    }

    @Override
    public void run() {
        try {
            latch.await();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        String downloadsFileURL;
        boolean condition = true;
        while(condition) {
            condition = !linkList.isEmpty();
            if (condition) {
                while ((!linkList.isEmpty()) && (!linkList.getFirst().split(": ")[0].equals("Fichero"))) {
                    downloadsFileURL = linkList.poll();
                    Process.process(downloadsFileURL,this.initialPath);
                }
                try {
                    barrier.await();
                } catch (BrokenBarrierException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
