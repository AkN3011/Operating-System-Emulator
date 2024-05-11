import java.io.IOException;

public class GoodbyeWorld extends UserlandProcess{

    public GoodbyeWorld() throws InterruptedException {


    }

    @Override
    void main() throws InterruptedException {



        // Infinite while loop prints out of Hello world and goes to sleep. Then it calls cooperate

        /**
         * These are all tests created to test out the devices
         * Open,read,Write
         *
         */



       // System.out.println(id);
        while (true) {

            System.out.println("Goodbye World");

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
