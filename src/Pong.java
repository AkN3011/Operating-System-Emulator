import java.io.IOException;

public class Pong  extends UserlandProcess{
      private static int num = 0;
      private int address;

      private boolean isMemoryAllocated = false;
    public Pong() throws InterruptedException {

    }

    @Override
    void main() throws Exception {
        if(!isMemoryAllocated){
            address = OS.AllocateMemory(5120);
            isMemoryAllocated = true;

        }
        Write(address,(byte) 25);
        byte readValue = Read(address);
        System.out.println("Pong: Wrote 25 to memory " + "-- "+ "Read " + readValue + " from memory");
       // OS.FreeMemory(address,5120);

        /**
        OS.SendMessage(new KernelMessage(OS.getPid(),OS.getPidByName("Ping"), num++ , new byte[6]));
        KernelMessage message = OS.WaitForMessage();

          if(message.getTargetPid() > 0) {
              System.out.println(message.toString());
          }
         */

       //OS.what +=1;
        while (true) {

            System.out.println("Pong");


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
