import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.Random;


public class ProducerConsumerSemaphore {
    Random random = new Random();
    int data;

    static Semaphore semConsumer = new Semaphore(0);
    static Semaphore semProducer = new Semaphore(1);
    static LinkedList<Integer> buffer = new LinkedList<Integer>();
    static final int border = 5;

    void produce() throws InterruptedException {
        while(true){
            Thread.sleep(1000);

            try {
                if (buffer.size() == border){
                    semProducer.acquire();
                }
            }
            catch (InterruptedException ex) {
                System.out.println("An error occured:"+ex.getMessage());
            }

            data = random.nextInt(1000+1);
            buffer.add(data);

            System.out.println("Producer produced: " + data);

            if (buffer.size() == 1){
                semConsumer.release();
            }
        }
    }

    void consume() throws InterruptedException {
        while(true){

            try {
                if (buffer.size() == 0){
                    semConsumer.acquire();
                }
            }
            catch (InterruptedException ex) {
                System.out.println("An error occured:"+ex.getMessage());
            }

            System.out.println("Consumer consumed: " + buffer.getLast());
            buffer.remove(buffer.size() - 1);

            if (buffer.size() == border - 1){
                semProducer.release();
            }
            Thread.sleep(1000);
        }
    }

    public static void main(String[] args) {
        ProducerConsumerSemaphore prodConSem = new ProducerConsumerSemaphore();
        ExecutorService executor = Executors.newFixedThreadPool(2);

        executor.execute(new Runnable() {
            @Override
            public void run()
            {

                try {
                    prodConSem.produce();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        executor.execute(new Runnable() {
            @Override
            public void run()
            {
                try {
                    prodConSem.consume();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        executor.shutdown();
    }
}

