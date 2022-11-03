class Philosopher extends Thread {
    int id; // philosopher id
    Chopstick lstick, rstick;
    Philosopher(int id, Chopstick lstick, Chopstick rstick) {
        this.id = id;
        this.lstick = lstick;
        this.rstick = rstick;
    }

    public void run() {
        try {
            while (true) {
                lstick.acquire();
                rstick.acquire();
                eating();
                lstick.release();
                rstick.release();
                thinking();
            }
        }catch (InterruptedException e) { }
    }

    void eating() {
        System.out.println("[" + id + "] eating");
    }
    void thinking() {
        System.out.println("[" + id + "] thinking");
    }
}

class Chopstick {
    private boolean inUse = false;
    synchronized void acquire() throws InterruptedException {
        while (inUse)
            wait();
        inUse = true;
    }
    synchronized void release() {
        inUse = false;
        notify();
    }
}

class Monitor_DP {
    static final int num = 5; // number of philosphers & chopsticks
    public static void main(String[] args) {
        int i;
        /* chopsticks */
        Chopstick[] stick = new Chopstick[num];
        for (i=0; i<num; i++)
            stick[i] = new Chopstick();
        /* philosophers */
        Philosopher[] phil = new Philosopher[num];
        for (i=0; i<num; i++)
            phil[i] = new Philosopher(i, stick[i], stick[(i+1)%num]);
        /* let philosophers eat and think */
        for (i=0; i<num; i++)
            phil[i].start();
    }
}