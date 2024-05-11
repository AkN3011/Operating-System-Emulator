import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

public class Kernel implements Runnable, Device {
    private boolean[] pageStatus;
    HashMap<Integer,PCB> waitingProcess = new HashMap<>();
    private final Scheduler scheduler;

    private final VFS vfs;

    private final Thread thread;
    private final Semaphore semaphore;

    public boolean running = false;



    // The constructor instantiates the instance variables and calls start on thread
    public Kernel() throws InterruptedException {
        pageStatus = new boolean[100];
        this.scheduler = new Scheduler();
        this.thread = new Thread(this);
        this.semaphore = new Semaphore(0,true);
        Arrays.fill(pageStatus,false);
        vfs = new VFS();
        thread.start();

    }

    public Thread getThread() {
        return thread;
    }

    public boolean[] getPageStatus() {
        return pageStatus;
    }

    // Start() releases the semaphore, so that the thread could run
    public void Start() throws InterruptedException {

        semaphore.release();
        try {
           thread.start();
        }catch (Exception e){

        }
    }

    /**
     * Create process here calls the scheduler's create process taking the parameter passed in to OS
     *
     * @throws InterruptedException
     */

    public void CreateProcess() throws InterruptedException, IOException {
        if (!OS.getParameters().isEmpty()) {
            scheduler.CreateProcess((UserlandProcess) OS.getParameters().get(0));
        }
    }


    public void CreateProcess(UserlandProcess up, OS.Priority priorityOrder) throws InterruptedException, IOException {
        scheduler.CreateProcess(up, priorityOrder);

    }
    //Getter method to get the scheduler;

    public Scheduler getScheduler() {
        return scheduler;
    }

    // Kernel's create process calls scheduler's create process
    public void SwitchProcess() throws InterruptedException, IOException {
        scheduler.SwitchProcess();    }


  //Getter
    public Semaphore getSemaphore() {
        return semaphore;
    }

    public void Sleep(int Milliseconds) throws InterruptedException, IOException {
        scheduler.Sleep(Milliseconds);
    }


/**'
 *
 * When thread.start() is called it calls the run method.
 * First acquires a semaphore, so the thread can run
 * Based on what OS.Currentcall is it goes to one of the cases. It can either be Switchprocess() or Createprocess()
 * at the end it calls run on the UserlandProcess
 * Exception handlers to handle any exceptions
 *
 */

    public void run() {
        try {
            while (true) {

                try {

                    semaphore.acquire();

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                if (OS.currentCall != null) {
                    switch (OS.currentCall) {
                        case CREATEPROCESS:
                            try {
                                   CreateProcess();
                                   // CreateProcess(newProcess, priority);

                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            break;
                        case SWITCHPROCESS:
                            try {
                                SwitchProcess();
                                // System.out.println(OS.getKernel().scheduler.currentProcess);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }

                        case SLEEP:
                            try {
                                //System.out.println(OS.getParameters().get(0) + "" + OS.currentCall);
                               // Sleep((Integer) OS.getParameters().get(0));

                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            break;
                    }

                    if (scheduler.currentProcess != null) {
                        scheduler.getCurrentlyRunning().run();
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Open Accepts a string and returns the index of where the device is Stored in PCB
     * The method calls VFS open and vfs returns the index of where the device is stored in VFS
     * If the VFS returns -1 some issues has occurred
     * If the PCB array is full then it returns -1, since opening a new device is not possible
     *
     *
     * @param s
     * @return
     * @throws Exception
     */
    @Override
    public int Open(String s) throws Exception {
        int id;

       int[] ids =  scheduler.getCurrentlyRunning().ids;
       for(int i = 0; i < ids.length; i++){

           if(scheduler.getCurrentlyRunning().ids[i] == -1){
               id = vfs.Open(s);

               if(id == -1){
                   return -1;
               }
               scheduler.getCurrentlyRunning().ids[i] = id;
               return i;
           }
       }
        return -1;
    }

    /**
     *
     * Close accepts the ID and uses that to access the array index in the PCB
     * if the returned value is not -1, the method calls VFS with that id, and empty the slot in PCB by setting it to -1
     * @param id
     * @throws IOException
     */
    @Override
    public void Close(int id) throws IOException {
       int vfsID = getScheduler().getCurrentlyRunning().ids[id];
       if(vfsID != -1){
           vfs.Close(vfsID);
           getScheduler().getCurrentlyRunning().ids[id] = -1;
       }

    }

    /**
     *
     * Read accepts an id and size for the array to be returned
     * It uses the id and acees the PCB's array to get the index
     * That index is passed down when calling VFS read, and it returns a byte array
     *
     * @param id
     * @param size
     * @return
     * @throws IOException
     */

    @Override
    public byte[] Read(int id, int size) throws IOException {
         byte[] byteArray = new byte[size];
        if(id > 0) {
            int vfsID = getScheduler().getCurrentlyRunning().ids[id];
            //  System.out.println(getScheduler().getCurrentlyRunning().ids);
            if (vfsID != -1) {
                byteArray = vfs.Read(vfsID, size);
            }
        }
        return byteArray;
    }

    /**
     * Seek method accepts an ID and to
     * It gets vfs id by access the index with the passed in id
     * if the vfs id is not -1 then it calls vfs seek with vfs id, and to
     * @param id
     * @param to
     * @throws IOException
     */

    @Override
    public void Seek(int id, int to) throws IOException {
         int vfsID = getScheduler().getCurrentlyRunning().ids[id];
         if(vfsID != 1){
             vfs.Seek(vfsID,to);
         }
    }


    /**
     *
     * Write accepts an id and a byte array similar to Read
     * It gets VFS id from the PCB's arraau
     * if tje vfsid gottend from PCB is not -1 then it calls VFS's write
     *  returns the byte array
     * @param id
     * @param data
     * @return
     * @throws IOException
     */
    @Override
    public int Write(int id, byte[] data) throws IOException {
        int result = 0;

        int vfsID = getScheduler().getCurrentlyRunning().ids[id];

        if(vfsID != -1){
           result = vfs.Write(vfsID,data);
        }
        return result;
    }

    public int getPid(){

       return scheduler.currentProcess.getPid();
    }

    public int getPidByName(String name){
        return scheduler.getPidByName(name);
    }


    /**Send message makes an instance of KernelMessage using the copy constructor, and pass kernel message
     * the sender is pid is set using a setter
     * a hashmap is used to store pid, and current process.
     * The kernel message is then added to the message queue
     * If there are any waiting processes, then they are removed from a hashmap, and that process is then added back into the process queue.
     *
     *
     * @param km
     */
    public void SendMessage(KernelMessage km){
        KernelMessage copyKernelMessage = new KernelMessage(km);
        copyKernelMessage.setSenderPid(scheduler.currentProcess.getPid());
        PCB targetPcb = OS.pidMap.get(km.getTargetPid());
        targetPcb.messagesQueue.add(copyKernelMessage);
        if(waitingProcess.containsKey(scheduler.currentProcess.getPid())){
            waitingProcess.remove(scheduler.currentProcess.getPid(),scheduler.currentProcess);
            Scheduler.interactiveProcesses.addLast(scheduler.currentProcess);
        }
    }

    /**
     * It checks if there are any messages in the current processes queue, and if there is a process then it removed, and returned
     * If the first case is false, then the current process is added to a hashmap, and the process is removed from the process queue.
     * @return
     */
    public KernelMessage WaitForMessage(){
         KernelMessage currentMessage;
        if(!scheduler.currentProcess.messagesQueue.isEmpty()){
            currentMessage = scheduler.currentProcess.messagesQueue.pollFirst();
            return currentMessage;
        } else {

            waitingProcess.put(scheduler.currentProcess.getPid(), scheduler.currentProcess);
            Scheduler.interactiveProcesses.remove(scheduler.currentProcess);
        }
        return new KernelMessage(0,0,0,new byte[10]);
    }

    /**
     * Allocate memory takes size
     * It checks if the inputted size if multiple of two, since a page is 1024kb
     * It creates new Physical to virtual to memory object and adds it to the array
     *
     *
     * @param size
     * @return
     */
    public int AllocateMemory(int size){

        int arrayIndex = 0;
        int sSegment= 0;

        if(size % 1024 != 0){
            return -1;
        }
        int virtualPages = size / 1024;

        for( int i =0; i < getScheduler().currentProcess.getPhysicalPageNumbers().length; i++){
            if(getScheduler().currentProcess.getPhysicalPageNumbers()[i] == null){
                arrayIndex = i;
                for(int j = i; j < i + virtualPages; j++){
                    getScheduler().currentProcess.getPhysicalPageNumbers()[j]= new VirtualToPhysicalMapping();
                }
                break;
            }
        }

       // sSegment = ReturnFreePagesSegment(virtualPages);


        /**
        for(int i = sSegment; i < sSegment + virtualPages; i++){
            pageStatus[i] = true;
        }
*/
         /**
        for(arrayIndex = 0; arrayIndex < scheduler.currentProcess.getPhysicalPageNumbers().length; arrayIndex++) {
            int i = sSegment;
            int j = arrayIndex;
            if (scheduler.currentProcess.getPhysicalPageNumbers()[arrayIndex] == null) {
                while (i != sSegment + virtualPages) {

                    scheduler.currentProcess.getPhysicalPageNumbers()[j] = new VirtualToPhysicalMapping();
                    scheduler.currentProcess.getPhysicalPageNumbers()[j].setPhysicalPageNumber(i);
                    j++;
                    i++;
                }
                 break;
            }
        }*/
        return  arrayIndex * 1024;
    }

    // This method is used to check if a segment Physical memory is available and returns the index of where the segment starts.
    //It iterates through the array and during each iteration of the outer loop the inner loop tries to find an empty segment, so there will be no blanks

    public int ReturnFreePagesSegment(int virtualPages){
        for(int i = 0; i < pageStatus.length; i++) {
            boolean PagesFree = true;
            for(int j = i; j < i + virtualPages; j++){
                if(pageStatus[j]){
                    PagesFree = false;
                    break;
                }
            }
            if(PagesFree){
                return i;
            }
        }
        return -1;
    }

    /**
     * Free memory gets pointer and size
     * That address is then used to get the page number or the starting index of the segment
     * The index is used to get the value form PCB, and that is the index into kernel's boolean array
     * Using the start index and size, the segment length is determined. and it is then freed by setting it to false or -1 depending on the array
     * @param pointer
     * @param size
     * @return
     */
    public boolean FreeMemory(int pointer, int size){
        if(pointer % 1024 != 0 && size % 1024 != 0){
            return  false;
        }
        int StartIndex = pointer /1024;
        int pages  = size/1024;
        try {
            for (int i = StartIndex; i < StartIndex + pages; i++) {

                pageStatus[scheduler.currentProcess.getPhysicalPageNumbers()[i].getPhysicalPageNumber()] = false;
            }
            for (int i = StartIndex; i < StartIndex + pages; i++) {

                if(scheduler.currentProcess.getPhysicalPageNumbers()[i] != null && scheduler.currentProcess.getPhysicalPageNumbers()[i].getPhysicalPageNumber() != -1) {

                    scheduler.currentProcess.getPhysicalPageNumbers()[i] = null;
                }
            }
        }catch (Exception e){
            return  false;
        }
        return true;
    }
}

