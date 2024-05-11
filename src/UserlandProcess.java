

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.concurrent.Semaphore;

//Implements two interfaces

public abstract class UserlandProcess implements Runnable, Device {

    private final int PAGESIZE = 1024;
    public static final int MEMORYSIZE = 1024 * 1024;
    public static int [] memory = new int[MEMORYSIZE];
    public static int[][] TLB = new int[2][2];


  // instance variables
    private  Thread thread;
   private Semaphore semaphore = new Semaphore(0,true);
    public static int incrementer = 1;


    private static Boolean isQuantumExpired;

    private int pid;

    public UserlandProcess() throws InterruptedException {
        //System.out.println(Thread.currentThread().getName())
         this.thread = new Thread(this);


  }


// This method starts the thread



    // sets the IsQuantumExpired true to switch process
    public void requestStop() {

       isQuantumExpired = true;
    }

    abstract void main() throws Exception;


    // it checks to see if the process is stopped
    // it is stopped if there aren't any semaphores available
    public boolean isStopped(){

        if(semaphore.availablePermits() == 0){
            return true;
        }

        return false;
    }

    // Checks to see if the thread is alive of not
   public boolean isDone() {

       if( !thread.isAlive() ){
           return  true;

       }
       return false;

    }

    // This releases semaphore to start a thread

    public void start() throws InterruptedException {

     semaphore.release();
    }

    public Semaphore getSemaphore(){
        return semaphore;
    }

    // it acquires so thread can't run
    public void stop() throws InterruptedException {


        semaphore.acquire();
    }


    /**
     * The run method acquires the available semaphore and calls main
     */
    public void run(){
            try {
           semaphore.acquire();
           main();
            } catch (Exception e) {

            }

    }

// Cooperate is used determine if the thread is ready to switch process by checking if Quantum os expired
    void cooperate() throws NullPointerException, InterruptedException {


       if(isQuantumExpired != null) {

           if (isQuantumExpired) {

               isQuantumExpired = false;


               OS.SwitchProcess();

           }

       }




    }
  // getters and setters for pid
    public int getPid() {
        return pid;
    }

    public void setPid(int pid){
       this.pid = pid;
    }

    /**
     * The method takes virtual address, and use that to find virtual page number.
     * Virtual page number is used to find the physical page number
     * If doesn't exist in the TLB then get mapping is called and read is recursively called again
     * When the method returns the physical page then it is used to get the physical address, and it is used to access the array and get the value in memory
     * @param address
     * @return
     */

    public byte Read(int address) throws IOException {
        int PhysicalPageNumber = -1;
        int virtualPageNumber = address/PAGESIZE;
        int pageOffset =  address % PAGESIZE;

        for(int i = 0; i < 2; i++){
            if(TLB[i][0] == virtualPageNumber){
                PhysicalPageNumber = TLB[i][1];
            }
        }
        if (PhysicalPageNumber == -1){
            OS.getMapping(virtualPageNumber);
            return Read(address);
        }
        int physcalAddress = PhysicalPageNumber * PAGESIZE + pageOffset;
        return (byte) memory[physcalAddress];
    }

    /**
     * Method takes the virtual address, and a value to store in the memory
     * Takes the address and find the virtual page number as well as pageofset
     * It Iterates through the TLB to find the virtual and physical page map, If it doesn't exist then get mapping is called
     * and Write is recursively called again
     * The physical address is then calculated after the physical page number is retrieved from the TLB
     * That address is used to access the memory and store the value
     *
     * @param address
     * @param value
     */
    public void Write(int address, byte value) throws IOException {
        int PhysicalPageNumber = -1;
        int virtualPageNumber = address/PAGESIZE;
        int pageOffset =  address%PAGESIZE;

        for(int i = 0; i < 2; i++){

            if(TLB[i][0] == virtualPageNumber){
                PhysicalPageNumber = TLB[i][1];

            }
        }
        if (PhysicalPageNumber == -1){

            OS.getMapping(virtualPageNumber);
            Write(address,value);
        }
        if(PhysicalPageNumber != -1) {
            int physicalAddress = PhysicalPageNumber * PAGESIZE + pageOffset;
            memory[physicalAddress] = value;
        }


    }
}
