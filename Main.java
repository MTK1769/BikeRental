class MySemaphore {
    private int permits;
    public MySemaphore(int permits){
        this.permits = permits;
    }
    public synchronized void acquire(){
        while(permits ==0){
            try{
                wait();
            }catch(InterruptedException e){}
        }
        permits--;
    }
    public synchronized void release(){
        permits++;
        notifyAll();
    }
}

class Tourist extends Thread {
    int id;
    int rounds;

    public Tourist(int id,int rounds){
        this.id = id;
        this.rounds = rounds;

    }
    public void run(){
        for (int i = 0; i < rounds; i++) {
            Main.pool.acquire();
            Main.mutex.acquire();
            Main.busy++;
            if (Main.busy > Main.maxBusy) {
                Main.maxBusy = Main.busy;
            }
            Main.uses[id-1]++;
            Main.total++;
            Main.mutex.release();
            try{
                Thread.sleep(20);
            }catch(InterruptedException e){}
            Main.mutex.acquire();
            Main.busy--;
            Main.mutex.release();
            Main.pool.release();
        }
    }
}
public class Main{
    static MySemaphore pool = new MySemaphore(4);
    static MySemaphore mutex = new MySemaphore(1);
    static int busy =0;
    static int maxBusy=0;
    static int total=0;
    static int[] uses= new int[7];
    public static void main(String[] args){
        
        try{
            Tourist[] tourists = new Tourist[7];
            int[] rounds = {2,3,3,4,2,5,3};
            for(int i=0;i<7;i++){
                tourists[i] = new Tourist(i+1, rounds[i]);
            }
            for (int i = 0; i < 7; i++) {
                tourists[i].start();
            }
            for(int i =0;i<7;i++){
                tourists[i].join();
            }
            System.out.println("Total uses: " + total);
            for(int i =0;i<7;i++){
                System.out.println("Tourist " + (i+1) +": " + uses[i] + " uses");
            }
            System.out.println("Most in use at the same time: "+maxBusy);
        }catch(InterruptedException e){}
            
    }

}