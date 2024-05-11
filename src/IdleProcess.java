import java.io.IOException;
import java.util.IdentityHashMap;
import java.util.concurrent.Semaphore;

public class IdleProcess extends UserlandProcess{


    public IdleProcess() throws InterruptedException {

    }

    // Idle process calls Cooperate and goes to sleep

    @Override
    void main() throws InterruptedException {

        while (true){

            cooperate();





            try {

                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

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
