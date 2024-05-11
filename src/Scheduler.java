
import java.io.IOException;
import java.time.Clock;
import java.util.*;

public class Scheduler {
    private int pageSize = 1024;
     static PCB currentArg;

     private HashMap<String,Integer> processNamesMappedPid = new HashMap<>(0);

     private Kernel kernel;

    long wakeUpTime;

    //private LinkedList<UserlandProcess> schedulerLinkedList ;

    public static LinkedList<PCB> realTimeProcesses;

    public static LinkedList<PCB> interactiveProcesses;

    public static LinkedList<PCB> backgroundProcesses;

    public HashMap<PCB, OS.Priority> processPriorityHashMap =  new HashMap<>();


    private final Clock clock = Clock.systemDefaultZone();


    private Timer timer;



    public PCB currentProcess;

    private ArrayList<PCB> sleepingProcess = new ArrayList<>();
   private HashMap< PCB, Long> sleepingProcessesMap = new HashMap<>();

    private int pidIncrementer = 1;
//  scheduler's constructor contains the interrupt message that is used to time out and process and switch after 250ms.
    public Scheduler(){

      //  this.schedulerLinkedList = new LinkedList<>();

        realTimeProcesses = new LinkedList<>();

        interactiveProcesses = new LinkedList<>();

        backgroundProcesses = new LinkedList<>();

        timer = new Timer();
        TimerTask timerTask = new TimerTask() {

            @Override
            public void run() {

             if(currentProcess != null) {

                 currentProcess.requestStop();
             }

            }
        };
       timer.scheduleAtFixedRate(timerTask,0,250);

    }



    public Long getTIme(){
        return clock.millis();
    }


    public void CreateProcess(UserlandProcess up, OS.Priority priorityOrder) throws InterruptedException, IOException {

         currentArg = new PCB(up);

         OS.pidMap.put(currentArg.getPid(),currentProcess);

         processNamesMappedPid.put(currentProcess.getName(),currentArg.getPid());

         //OS.pidMap()
         processPriorityHashMap.put(currentArg,priorityOrder);




//         System.out.println(interactiveProcesses + "" + realTimeProcesses + "" + backgroundProcesses );
        if(currentProcess == null || currentProcess.isDone()){


            SwitchProcess();

        }


    }

/**
 * Sleeps sets the wakeup time and put the current process on to  the Hashmap with its approximate wakeup time
 * It also add the current process into a new list that holds sleeping processes
 * removes that process from the list that has all the currently running processes
 * Calls Switch process after
 *
 * */
    public void Sleep(int Milliseconds) throws InterruptedException, IOException {

        long currentTime = getTIme();

        wakeUpTime = currentTime + Milliseconds;

        if(!sleepingProcessesMap.containsKey(currentProcess)) {
            sleepingProcessesMap.put(currentProcess, wakeUpTime);

            sleepingProcess.add(currentProcess);


            OS.Priority processPriority = processPriorityHashMap.get(currentProcess);

            if (processPriority == OS.Priority.INTERACTIVE) {
                interactiveProcesses.remove(currentProcess);
            } else if (processPriority == OS.Priority.REALTIME) {
                realTimeProcesses.remove(currentProcess);
            } else {
                backgroundProcesses.remove(currentProcess);
            }
        }

           currentProcess = null;

          SwitchProcess();

        // OS.SwitchProcess();

        // System.out.println(currentProcess);

    }

    //getter

    public PCB getCurrentlyRunning() {
        return currentProcess;
    }
    /**
     *
     * @param up
     * Method takes a UserlandProcess as an up.
     * It adds the argument int to a linked-list.
     * if the current process is null or is done it calls switch process
     * The method return a pid
     *
     * @return
     * @throws InterruptedException
     */
    public int CreateProcess(UserlandProcess up) throws InterruptedException, IOException {
       // System.out.println(12312);
        // System.out.println(up);
      //  schedulerLinkedList.add(up ) ;


       currentArg = new PCB(up);

       OS.pidMap.put(currentArg.getPid(),currentArg);
       processNamesMappedPid.put(currentArg.getName(),currentArg.getPid());
       processPriorityHashMap.put(currentArg, OS.Priority.INTERACTIVE);


       // System.out.println(up);

        if(currentProcess == null ){

            SwitchProcess();
        }
            up.setPid(pidIncrementer);
            pidIncrementer++;

        return pidIncrementer;
    }


    // SwitchProcess checks for two edge cases where the current process is null or process is done
    // If the case is false, then it gets the first process in the linked list and call start

    public void SwitchProcess() throws InterruptedException, IOException {


      int randomNum = 0;

 /**
        if(currentProcess != null && !sleepingProcessesMap.containsKey(currentProcess)) {
            schedulerLinkedList.addLast(currentProcess);
        }
  */
        CloseAllOpenDevices();


 // Adds the new process into its correct list based on its priority

        if(!sleepingProcessesMap.containsKey(currentArg) && currentArg != null){

            OS.Priority processPriority =  processPriorityHashMap.get(currentArg);


            switch (processPriority){
                case REALTIME:
                    realTimeProcesses.add(currentArg);
                    currentArg = null;
                    break;

                case BACKGROUND:
                    backgroundProcesses.add(currentArg);
                    currentArg = null;
                    break;

                case INTERACTIVE:
                    interactiveProcesses.add(currentArg);
                    currentArg = null;
                    break;

            }
        }

        // This checks if the process is ready to be awakened
        // This is done by checking each process and seeing its awake time, and adding back on to the list of existing process
        WakeUpProcess();
       // System.out.println(currentProcess);
        //Demotion
        // Checks if the current process has more than 5 timeouts
        DemotionHandler();

     //   System.out.println(1);
       // This takes the current PCB and adds it back to its correct list based on its priority.
        if(!sleepingProcessesMap.containsKey(currentProcess) && currentProcess != null ){

            OS.Priority processPriority =  processPriorityHashMap.get(currentProcess);

            switch (processPriority){
                case REALTIME:
                    realTimeProcesses.addLast(currentProcess);
                    break;

                case BACKGROUND:
                    backgroundProcesses.addLast(currentProcess);
                    break;

                case INTERACTIVE:

                    interactiveProcesses.addLast(currentProcess);

                    break;
            }

        }

        /** This makes sure that there is at least a single process in each category
            Using a random number generate the switch 
         */
        // Clears TLB


        //


       if(currentProcess != null) {
          ClearTLBandMemory();
       }
        StartProcess();





    }

    // This method clears the TLB and memory when a process ends.
    // This done by getting physical page number from TLB and using that to get the physical address
    // TLB is cleared by just iterating through the array, and setting everything to -1
    public void ClearTLBandMemory(){

        int address;
        int pageOffset;
        for(int i = 0; i < 2; i++){

            if(UserlandProcess.TLB[i][1] != 0) {
                address = UserlandProcess.TLB[i][1] * 1024;


                if(address> 0) {
                    //System.out.println(address);
                    pageOffset = address % 1024;
                    UserlandProcess.memory[UserlandProcess.TLB[i][1] * 1024 + pageOffset] = 0;
                }
            }
        }





        for(int i = 0; i < 2; i++){
            for(int j = 0; j < 2; j++){
                UserlandProcess.TLB[i][j] = -1;
            }
        }
        //


    }

    /**
     *This method is used to close all the open devices opened by a specific process
     *This is done using a for loop, iterating through each index, and using a reference to the kernel to get the close method.
     * @throws IOException
     */

    public void CloseAllOpenDevices() throws IOException {

        if(currentProcess != null) {


            if (currentProcess.isDone() ) {

                int[] ids = currentProcess.ids;
                if(ids.length > 0) {
                    for (int i = 0; i < ids.length; i++) {

                        if (ids[i] != -1) {
                            System.out.println("Device Closed");
                            OS.getKernel().Close(i);
                        }
                    }
                }

            }
        }

    }
// generated a number between one and three
    public int getRandomNum(int max){

        Random random = new Random();
        int randNum = random.nextInt(max) +1;


        return randNum;
    }

/** it iterates through all the sleeping process, and checks if a process is ready to be awake.
    The check is done by looking at the current time and comparing it to the time stored in hashmap.
   If a process is ready to be awake then it is taken out of the sleeping process and added back into right queue based on its priority
 */


public void StartProcess() throws InterruptedException {


    if(interactiveProcesses.size() >= 4) {
        currentProcess = interactiveProcesses.pollFirst();

        if (currentProcess != null) {

            currentProcess.start();

        }
    }

}


public void StartProcess(int randomNum) throws InterruptedException {
       //System.out.println(interactiveProcesses + "" + realTimeProcesses + ""+ backgroundProcesses);

/**
    if(interactiveProcesses.isEmpty()&& realTimeProcesses.isEmpty() && !backgroundProcesses.isEmpty()){
        currentProcess = backgroundProcesses.pollFirst();
        if(currentProcess != null){
            currentProcess.start();
        }

    }else if(!interactiveProcesses.isEmpty()&& realTimeProcesses.isEmpty() && !backgroundProcesses.isEmpty() ){
        randomNum = getRandomNum(2);
        switch (randomNum){
            case 1:
                currentProcess = interactiveProcesses.pollFirst();
                currentProcess.start();
                break;

            case 2:
                currentProcess = backgroundProcesses.pollFirst();
                currentProcess.start();
                break;
        }

    }
 */

        if(!interactiveProcesses.isEmpty()&& !realTimeProcesses.isEmpty() && !backgroundProcesses.isEmpty() ) {

        randomNum = getRandomNum(3);

        try {
            switch (randomNum) {

                case 1:

                    currentProcess = interactiveProcesses.pollFirst();
                    if(currentProcess != null) {
                        currentProcess.start();
                    }

                    break;

                case 2:
                    currentProcess = realTimeProcesses.pollFirst();
                    if(currentProcess != null) {
                        currentProcess.start();
                    }
                    break;
                case 3:

                    currentProcess = backgroundProcesses.pollFirst();
                    if(currentProcess != null) {
                        currentProcess.start();

                    }
                    break;

                default:
                    // currentProcess = schedulerLinkedList.pollFirst();
                    //currentProcess.start();
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
        
}
 public void DemotionHandler(){
     if(currentProcess != null &&  OS.processTimeOuts.get(currentProcess) != null ) {

         if (OS.processTimeOuts.get(currentProcess) > 5) {
             UserlandProcess.incrementer = 0;
             OS.Priority processPriority = processPriorityHashMap.get(currentProcess);

             if (processPriority == OS.Priority.REALTIME) {

                 processPriority = OS.Priority.INTERACTIVE;
                 processPriorityHashMap.replace(currentProcess, processPriority);

                 System.out.println(currentProcess + "" + processPriority);

             } else if (processPriority == OS.Priority.INTERACTIVE) {
                 processPriority = OS.Priority.BACKGROUND;
                 processPriorityHashMap.replace(currentProcess, processPriority);
                 System.out.println(currentProcess + "" + processPriority);

             }

             OS.processTimeOuts.replace(currentProcess, UserlandProcess.incrementer);


         }
     }

 }
    public void WakeUpProcess(){

        for (int i = 0; i < sleepingProcess.size(); i++){

            if(sleepingProcessesMap.containsKey(sleepingProcess.get(i))){

                long wakeTime = sleepingProcessesMap.get(sleepingProcess.get(i));

                if(getTIme() >= wakeTime){


                    OS.Priority priority =  processPriorityHashMap.get(currentProcess);

                    if(priority == OS.Priority.REALTIME){
                        realTimeProcesses.add(sleepingProcess.get(i));
                    } else if(priority == OS.Priority.INTERACTIVE){
                        interactiveProcesses.add(sleepingProcess.get(i));
                    } else if(priority == OS.Priority.BACKGROUND) {
                        backgroundProcesses.add(sleepingProcess.get(i));
                    }
                    // schedulerLinkedList.add(sleepingProcess.get(i));
                    sleepingProcessesMap.remove(sleepingProcess.get(i));
                    sleepingProcess.remove(i);
                }

            }
        }

    }

    /**
     * get pid returns the pid of the current process
     */  
    public int getPid(){
    return currentProcess.getPid();
    }

    /**
     * Used a hashmap to store name of the process and its pid
     * Believed it was better than iterating through all the queues to find an Userland process with the given name
     * @param name
     * @return
     */
    public int getPidByName(String name){
      int pid = processNamesMappedPid.get(name);

      if(pid > 0) {
          return pid;
      }
       return -1;
    }

    /**
     *
     * Get random process finds a process that has physical pages available, and write that to disk.
     * Before the victim process writes to disk the current process steals those pages, and use it.
     * OS.Pagenumber is used to assign the blocks of memory on disk
     *
     *
     * @param virtualPageNumber
     * @return
     * @throws IOException
     */
    public PCB GetRandomProcess(int virtualPageNumber) throws IOException {
        Random random = new Random();
        PCB victimProcess;
        VirtualToPhysicalMapping victimsMapping;
        int randNum;
        for(int i = 0; i < interactiveProcesses.size(); i++) {
            randNum = random.nextInt(interactiveProcesses.size());
          //  System.out.println(randNum + "--" + interactiveProcesses.get(randNum).getUserlandprocess());
            victimProcess = interactiveProcesses.get(randNum);

            for(int  j = 0; j < victimProcess.getPhysicalPageNumbers().length; j++){

                if(victimProcess.getPhysicalPageNumbers()[j] != null){
                  victimsMapping = victimProcess.getPhysicalPageNumbers()[j];
                  if(victimsMapping.getPhysicalPageNumber() != -1) {
                      OS.swapFile.seek((long) OS.pageNumber * pageSize);
                      OS.swapFile.writeInt(victimsMapping.getPhysicalPageNumber());
                      OS.pageNumber++;
                      currentProcess.getPhysicalPageNumbers()[virtualPageNumber].setPhysicalPageNumber(victimsMapping.getPhysicalPageNumber());
                      victimsMapping.setPhysicalPageNumber(-1);
                      victimsMapping.setDiskPageNumber(OS.pageNumber * pageSize);
                      return currentProcess;
                  }
                }
            }
        }
        return null;
    }
}
