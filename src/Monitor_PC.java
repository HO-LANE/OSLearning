class Buffer {
    int[] buf;
    int size, count, in, out;
    Buffer(int size) {
        buf = new int[size];
        this.size = size;
        count = in = out = 0;
    }

    synchronized void insert(int item) {
        while (count == size)
            try {
                wait();
            } catch (InterruptedException e) {}
        buf[in] = item;
        in = (in+1)%size;
        notify();
        count++;
    }

    synchronized int remove() {
        while (count == 0)
            try {
                wait();
            } catch (InterruptedException e) {}
        int item = buf[out];
        out = (out+1)%size;
        count--;
        notify();
        return item;
    }
}

class Producer extends Thread {
    Buffer b;
    int N;
    Producer(Buffer b, int N) {
        this.b = b; this.N = N;
    }
    public void run() {
        for (int i=0; i<N; i++)
            b.insert(i);
    }
}

class Consumer extends Thread {
    Buffer b;
    int N;
    Consumer(Buffer b, int N) {
        this.b = b; this.N = N;
    }
    public void run() {
        int item;
        for (int i=0; i<N; i++)
            item = b.remove();
    }
}

class Monitor_PC {
    public static void main(String[] arg) {
        Buffer b = new Buffer(100);
        Producer p = new Producer(b, 10000);
        Consumer c = new Consumer(b, 10000);
        p.start();
        c.start();
        try {
            p.join();
            c.join();
        } catch (InterruptedException e) {}
        System.out.println("Number of items in the buf is " + b.count);
    }
}