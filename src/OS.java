
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class OS  {
    public static  RandomAccessFile swapFile;
    public static int pageNumber = 1;

    public static int pageSize = 1024;
   public static Random random= new Random();
    public static int what = 0;
    public static HashMap<Integer,PCB> pidMap = new HashMap<>();
    // enums
    public enum CallType{
        CREATEPROCESS,
        SWITCHPROCESS,
        SLEEP
    }

    public enum Priority{
        REALTIME,
        INTERACTIVE,
        BACKGROUND
    }
    public static UserlandProcess newProcess;
    public static OS.Priority priority;
    public static HashMap<PCB, Integer> processTimeOuts = new HashMap<>();
    final static Semaphore semaphore = new Semaphore(1,true);
    private static Kernel kernel;
    public static CallType currentCall;
    private static ArrayList<Object> parameters = new ArrayList<>();
    private static Object returnValue;
    public static Semaphore getSemaphore() {
        return semaphore;
    }

    /**
     *
     * @param up
     * The method takes a useland process as an argument
     * First the method clears the parameters then adds current argument  into the list
     * Then sets the currentCall to the correct enum
     * Calls the switch kernel method
     * it returns the process ID of the current process
     *
     * @return
     * @throws InterruptedException
     */
    public static int CreateProcess(UserlandProcess up) throws InterruptedException, IOException {
        parameters.clear();
        parameters.add(up);
        currentCall = CallType.CREATEPROCESS;
        switchToKernel();
        returnValue = 0;
        return (int)returnValue;
    }

    public static int CreateProcess(UserlandProcess up, Priority priorityOrder) throws InterruptedException {
        parameters.clear();
        parameters.add(up);
        priority = priorityOrder;
        newProcess = up;
        currentCall = CallType.CREATEPROCESS;
        switchToKernel(up, priorityOrder);
        return 0;
    }

    /**
     *  Switch to kernel method calls start on kernel, and that releases a semaphore
     *  Then calls create process and that creates a new process in the kernel
     *  After that if the current process is not null it calls stop on the current process, else if  current process is null the thread goes to sleep
     * @throws InterruptedException
     */
    public static void switchToKernel() throws InterruptedException, IOException {
        kernel.Start();
        // kernel.run();
        if(kernel.getScheduler().currentProcess != null){
            kernel.getScheduler().currentProcess.stop();
        } else {
            while (true){
                Thread.sleep(10);
                return;
            }
        }
    }


    public static void switchToKernel(UserlandProcess up, Priority priority) throws InterruptedException {
           kernel.Start();
          //kernel.CreateProcess(up,priority);
        //   kernel.CreateProcess(up,priority);
         // kernel.CreateProcess(up,priority);
        if(kernel.getScheduler().currentProcess != null){
            kernel.getScheduler().currentProcess.stop();
        } else {
            while (true){
                Thread.sleep(10);
                return;
            }
        }
    }
    /**
     * The method sets current call to the Switchprocess
     * calls kernel to switch process, and then run it
     * It releases the semaphore for the kernel to switch process
     * @throws InterruptedException
     */
    public static void SwitchProcess() throws InterruptedException {
        //System.out.println(1);
          //This adds the process and timeouts incrementer to a hashmap to keep track of how many times a process timed out.
        if(kernel.getScheduler().processPriorityHashMap.get(OS.getKernel().getScheduler().currentProcess) != Priority.INTERACTIVE) {
            if (!processTimeOuts.containsKey(OS.getKernel().getScheduler().currentProcess)) {
                processTimeOuts.put(OS.getKernel().getScheduler().currentProcess, UserlandProcess.incrementer);
            } else {
                UserlandProcess.incrementer = processTimeOuts.get(OS.getKernel().getScheduler().currentProcess);
                UserlandProcess.incrementer += 1;
                processTimeOuts.replace(OS.getKernel().getScheduler().currentProcess, UserlandProcess.incrementer);

            }
        }
        currentCall = CallType.SWITCHPROCESS;
         if(kernel.getSemaphore().availablePermits() == 0){
             kernel.Start();
         }
        kernel.run();
        if(kernel.getScheduler().currentProcess != null){
            kernel.getScheduler().currentProcess.stop();
        }

    }

    /**
     *
     * Sleep method is setup similar to create process in the OS class
     * Takes the parameter and switch to kernel
     * @param Milliseconds
     * @throws InterruptedException
     */
    public static void Sleep(int Milliseconds) throws InterruptedException, IOException {
        parameters.clear();
        parameters.add(Milliseconds);
        currentCall = CallType.SLEEP;
        switchToKernel();
    }

    /**
     *
     * @param init
     *
     * It takes a userland process as parameter
     * Create an instance of kernel and idle process.
     * Calls create process for the passed in Userland Process, and Idle process
     * @throws InterruptedException
     */

    public static void Startup(UserlandProcess init) throws Exception {
        for(int i =0; i < 2; i++){
            for(int j = 0; j < 2; j++){
                UserlandProcess.TLB[i][j] = -1;
            }
        }


      kernel = new Kernel();
      CreateProcess(init);
      CreateProcess(new IdleProcess());
      FakeFileSystem fakeFileSystem = new FakeFileSystem();
      int fileIndex = fakeFileSystem.Open("SwapFile");
      swapFile = fakeFileSystem.getRandomAccessFiles()[fileIndex];

        //OS.CreateProcess(new SleepWorld(),OS.Priority.REALTIME);

    }
  //getter
    public static Kernel getKernel() {
        return kernel;
    }
   // getter
    public static ArrayList<Object> getParameters() {
        return parameters;
    }

    /**
     * Open, Read, Write, Seek, and Close get called from Userland processes, then OS calls kernel to deal with it based on the designated calls from the Userland processes.
     *
     * it basically makes a switch to kernel
     * They are all static methods since as mentioned in assignment 1 everything in OS must be static
     */
    public static int Open(String s) throws Exception {
        return kernel.Open(s);
    }

    public static byte[] Read(int id,int size ) throws IOException {

        return kernel.Read(id,size);
    }

    public static void Close(int id) throws IOException {
        kernel.Close(id);
    }

    public static void Seek(int id, int to) throws IOException {
        kernel.Seek(id,to);
    }

    public static int Write(int id, byte[] data) throws IOException {

        return kernel.Write(id,data);

    }

    // calls kernels get pid
    public static int getPid(){
       return kernel.getPid();
    }

    //calls kernels getPidBuNa,e
    public static int getPidByName(String name){


        return kernel.getPidByName(name);
    }
// calls kernels sendMessage
    public static void SendMessage(KernelMessage km){

        kernel.SendMessage(km);
    }
    // calls kernels WaitForMessage
    public static KernelMessage WaitForMessage(){

        return kernel.WaitForMessage();
    }

    /**
     * getMapping takes Virtual page number
     * The virtual page number is index into PCB's array
     * The value associated with index is the physical page
     * With a random number generator. it randomly inputs the virtual page number and physical page number into the TLB
     * Now the method deals with two different ways of mapping:
     * one is where it checks to see if there is free memory block of memory ond use it
     * second if there is no memory available it steals from an already existing process, and writes its data to disk
     *
     * @param virtualPageNumber
     */
   public static void getMapping(int virtualPageNumber) throws IOException {
       int getindex;
       int physicalPageNumber;
       int index = 0;
       PCB currentProcess;
       index = random.nextInt(2);
       getindex = getFreeBlock();
       if (kernel.getScheduler().currentProcess.getPhysicalPageNumbers()[virtualPageNumber].getPhysicalPageNumber() == -1 && getindex != -1) {

           for(int i = virtualPageNumber; i < kernel.getScheduler().currentProcess.getPhysicalPageNumbers().length; i++) {
               if(kernel.getScheduler().currentProcess.getPhysicalPageNumbers()[i] != null && kernel.getScheduler().currentProcess.getPhysicalPageNumbers()[i].getPhysicalPageNumber() == -1) {
                   System.out.println(getindex);
                   kernel.getScheduler().currentProcess.getPhysicalPageNumbers()[i].setPhysicalPageNumber(getindex);
                   kernel.getScheduler().currentProcess.getPhysicalPageNumbers()[i].setDiskPageNumber(0);
                   getindex = getFreeBlock();
               }
           }

           physicalPageNumber = kernel.getScheduler().currentProcess.getPhysicalPageNumbers()[virtualPageNumber].getPhysicalPageNumber();
           UserlandProcess.TLB[index][0] = virtualPageNumber;
           UserlandProcess.TLB[index][1] = physicalPageNumber;
       } else if (kernel.getScheduler().currentProcess.getPhysicalPageNumbers()[virtualPageNumber].getDiskPageNumber() != -1 && kernel.getScheduler().currentProcess.getPhysicalPageNumbers()[virtualPageNumber].getDiskPageNumber() != 0) {
           int num = kernel.getScheduler().currentProcess.getPhysicalPageNumbers()[virtualPageNumber].getDiskPageNumber();
           swapFile.seek(num);
           int pageNum = swapFile.readInt();
           kernel.getScheduler().currentProcess.getPhysicalPageNumbers()[virtualPageNumber].setPhysicalPageNumber(pageNum);
           kernel.getScheduler().currentProcess.getPhysicalPageNumbers()[virtualPageNumber].setDiskPageNumber(0);
       } else {
           currentProcess = kernel.getScheduler().GetRandomProcess(virtualPageNumber);
           if (currentProcess != null) {
               UserlandProcess.TLB[index][0] = virtualPageNumber;
               UserlandProcess.TLB[index][1] = currentProcess.getPhysicalPageNumbers()[virtualPageNumber].getPhysicalPageNumber();
           }
       }
   }


    /**
     * Get a free memory block and returns the index if there is a block available else returns -1
     * @return
     */
   public static int getFreeBlock(){
       for(int i = 0; i < kernel.getPageStatus().length; i++){
           if(!kernel.getPageStatus()[i]){
               kernel.getPageStatus()[i] = true;
               return i;
           }
       }
       return -1;
   }

   public static int AllocateMemory(int size){
        return  kernel.AllocateMemory(size);
   }

   public static boolean FreeMemory(int pointer, int size){
    return kernel.FreeMemory(pointer,size);
   }


}
