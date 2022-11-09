import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
enum State {
    THINKING, HUNGRY, EATING
}
public class Monitor_DP {
    public static void main(String[] args) throws Exception {
        int numOfPhils = 5;
        MPhiloshoper[] philosophers = new MPhiloshoper[numOfPhils];
        DiningPhilosopherMonitor monitor = new DiningPhilosopherMonitor(numOfPhils);
        for (int i = 0; i < philosophers.length; i++)
            new Thread(new MPhiloshoper(i, monitor)).start();
    }
}

class MPhiloshoper implements Runnable {
    private int id;
    private DiningPhilosopherMonitor monitor;
    public MPhiloshoper(int id, DiningPhilosopherMonitor monitor) {
        this.id = id;
        this.monitor = monitor;
    }
    @Override
    public void run() {
        while (true) {
            think();
            monitor.pickup(id);
            eat();
            monitor.putdown(id);
        }
    }
    private void think() {
        try {
            System.out.println(id + ": Now I'm thinking.");
            Thread.sleep((long)(Math.random()*500));
        } catch (InterruptedException e) { }
    }
    private void eat() {
        try {
            System.out.println(id + ": Now I'm eating.");
            Thread.sleep((long)(Math.random()*50));
        } catch (InterruptedException e) { }
    }
}

class DiningPhilosopherMonitor {
    private int numOfPhils;
    private State[] state;
    private Condition[] self;
    private Lock lock;
    public DiningPhilosopherMonitor(int num) {
        numOfPhils = num;
        state = new State[num];
        self = new Condition[num];
        lock = new ReentrantLock();
        for (int i = 0; i < num; i++) {
            state[i] = State.THINKING;
            self[i] = lock.newCondition();
        }
    }
    private int leftOf(int i) {
        return (i + numOfPhils - 1) % numOfPhils;
    }
    private int rightOf(int i) {
        return (i + 1) % numOfPhils;
    }
    private void test(int i) {
        if (state[i] == State.HUNGRY &&
                state[leftOf(i)] != State.EATING &&
                state[rightOf(i)] != State.EATING)
        {
            state[i] = State.EATING;
            self[i].signal();
        }
    }

    public void pickup(int id) {
        lock.lock();
        try {
            state[id] = State.HUNGRY;
            test(id);
            if (state[id] != State.EATING)
                self[id].await();
        }
        catch (InterruptedException e) {
        }
        finally {
            lock.unlock();
        }
    }
    public void putdown(int id) {
        lock.lock();
        try {
            state[id] = State.THINKING;
            test(leftOf(id)); // left neighbor
            test(rightOf(id)); // right neighbor
        }
        finally {
            lock.unlock();
        }
    }

}
