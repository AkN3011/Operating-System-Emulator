import java.util.Arrays;
import java.util.LinkedList;

public class PCB implements Runnable{
    private VirtualToPhysicalMapping[] physicalPageNumbers;
    private static int nextpid = 1;

    private int pid = 0;

    private UserlandProcess userlandProcess;

    private Thread thread;
 // The array holds the indices returned from VFS

    public int[] ids;

    private String ProcessName;

    public LinkedList<KernelMessage> messagesQueue;


    public PCB(UserlandProcess userlandProcess){
        physicalPageNumbers = new VirtualToPhysicalMapping[100];
        ProcessName = userlandProcess.getClass().getSimpleName();
        this.userlandProcess = userlandProcess;
        this.thread = new Thread();
        this.pid = nextpid;
        ids = new int[10];
        // It is filled with -1 as recommend in the assignment
        Arrays.fill(ids,-1);
        this.messagesQueue = new LinkedList<>();
        nextpid++;

    }

    public String getName(){

      return ProcessName;
    }

    public VirtualToPhysicalMapping[] getPhysicalPageNumbers() {
        return physicalPageNumbers;
    }


    //getter
    public int getPid(){
        return this.pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public UserlandProcess getUserlandprocess(){
        return userlandProcess;
    }

    //calls request stop from the userland process
    void requestStop(){
        userlandProcess.requestStop();
    }

   public void start() throws InterruptedException {
        userlandProcess.start();

    }
    // Stop calls stop from the Userland process
    void stop() throws InterruptedException  {

        userlandProcess.stop();

        while (!userlandProcess.isStopped()) {

            try {
                Thread.sleep(100);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }


    //boolean

    public boolean isDone(){


   return userlandProcess.isDone();
    }

    public void run() {
      // System.out.println(userlandProcess.getSemaphore().availablePermits());
          //    System.out.println(OS.getKernel().getScheduler().currentProcess + "111");
            userlandProcess.run();


    }
}
