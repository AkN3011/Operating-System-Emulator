import java.io.IOException;

public class HelloWorld extends UserlandProcess {
    private boolean isMemoryAllocated = false;

    private int address;
    public HelloWorld() throws Exception {





    }

    @Override
    void main() throws Exception {
       // main prints out hello world and calls cooperate in an infinite while loop

         // device opened
        if(isMemoryAllocated == false) {
           address = OS.AllocateMemory(2048);
           isMemoryAllocated = true;
        }
        Write(address,(byte) 3);
        byte readValue = Read(address);
        System.out.println("HelloWorld: Wrote 3 to memory " + "-- "+ "Read " + readValue + " from memory");
        //OS.FreeMemory(address,2048);


  while (true){



          System.out.println("Hello World"  );



      try {
          Thread.sleep(50);
      } catch (InterruptedException e) {
          e.printStackTrace();
      }

    
        cooperate();

      }



    }


    /**
     *
     * Open, Seek, Close, Read, Write calls their designated methods in the OS class.
     *
     *
     */
    @Override
    public int Open(String s) throws Exception {
        return OS.Open(s);
    }

    @Override
    public void Close(int id) throws IOException {
        OS.Close(id);
    }

    @Override
    public byte[] Read(int id, int size) throws IOException {
        return OS.Read(id,size);
    }

    @Override
    public void Seek(int id, int to) throws IOException {
       OS.Seek(id,to);
    }

    @Override
    public int Write(int id, byte[] data) throws IOException {
        return OS.Write(id,data);
    }
}


