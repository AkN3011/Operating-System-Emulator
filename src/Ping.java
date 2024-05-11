import java.io.IOException;

public class Ping extends UserlandProcess {
    private boolean isMemoryAllocated = false;

    private static int num = 0;

    private int address;
    public Ping() throws InterruptedException {
        System.out.println("I'm ping:");

    }



       public void AllocateMemory(){

           address = OS.AllocateMemory(2048);
       }


    @Override
    void main() throws Exception {

        if(!isMemoryAllocated){
            AllocateMemory();
            isMemoryAllocated = true;
        }
        Write(address,(byte) 10);
        byte readValue = Read(address);
        System.out.println("Ping: Wrote 10 to memory " + "-- "+ "Read " + readValue + " from memory");
        //OS.FreeMemory(address,2048);




        /**
        OS.SendMessage(new KernelMessage(OS.getPid(),OS.getPidByName("Pong"), num++, new byte[6]));

        KernelMessage message = OS.WaitForMessage();

       if(  message.getTargetPid() > 0) {
           System.out.println(message.toString());
       }
         */






        while (true) {
         System.out.println("Ping");

            try {
                Thread.sleep(50);

            } catch (Exception e) {
                e.printStackTrace();
            }

            cooperate();
        }
    }

    public String getName(){
        return toString();
    }



    @Override
    public int Open(String s) throws Exception {
        return 0;
    }

    @Override
    public void Close(int id) throws IOException {

    }

    @Override
    public byte[] Read(int id, int size) throws IOException {
        return new byte[0];
    }

    @Override
    public void Seek(int id, int to) throws IOException {

    }

    @Override
    public int Write(int id, byte[] data) throws IOException {
        return 0;
    }
}
