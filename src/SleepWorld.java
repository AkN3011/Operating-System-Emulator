import java.io.IOException;

public class SleepWorld extends UserlandProcess{
    public SleepWorld() throws InterruptedException {

    }
// Test process to check if Sleep method works

    @Override
    void main() throws InterruptedException, IOException {


        while (true) {

            System.out.println("Sleep");


            try {
                Thread.sleep(50);

            } catch (Exception e) {
                e.printStackTrace();
            }






             cooperate();
        }


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
