import java.util.Arrays;
import java.util.Random;

public class RandomDevice implements Device{

    Random[] randomDevice ;
// Array holds Random values
    public RandomDevice(){
          randomDevice =  new Random[10];
    }

    /**
     * Opens accepts a string
     * That string gets converted into an intger then passed into new Random
     * That Random is then placed into an empty spot in that array
     * The index is returned after it is placed, if the array isn't empty then -1 is returned
     * @param s
     * @return
     */
    @Override
    public int Open(String s) {

        int seed = 0;
        int i;
        if(s.isEmpty()){
            seed = Integer.parseInt(s);
        }
        Random random_Device = new Random(seed);
        for( i = 0; i < randomDevice.length; i++){
            if(randomDevice[i] == null){

                randomDevice[i] = random_Device;
                return i;
            }
        }
      return -1;
    }

    /**
     * Close takes in the id/index
     * The id/index is used to access the device and assign it to null
     * @param id
     */
    @Override
    public void Close(int id) {
        System.out.println("Random device is closed");
        for(int i = 0; i < randomDevice.length; i++){
           if(i == id){
               randomDevice[i] = null;
           }
        }
    }

    /**
     * Readn takes in id and size
     * It iterates through the array and add random numbers into it
     * The RandomIndex variable keeps the range of the array within the limit of a random device, so if the size of the new array exceeds the length of the random device array, an array won't be thrown.
     * It returns that array after it is filled up
     *
     *
     * @param id
     * @param size
     * @return
     */
    @Override
    public byte[] Read(int id, int size) {

        byte[] readArray = new byte[size];
        int randomIndex;
        for(int i = 0; i < size; i++){
            if(randomDevice[id] != null) {

                readArray[i] = (byte) randomDevice[id].nextInt();
                System.out.println(readArray[i]);
            }
        }
        return readArray;
    }


    /**
     * Seek reads it, but doesn't return anything
     * @param id
     * @param to
     */
    @Override
    public void Seek(int id, int to) {
        byte[] randomBytes = Read(id, 10);

    }

    /**
     * Write just returns zero as mentioned in the assignment
     * @param id
     * @param data
     * @return
     */
    @Override
    public int Write(int id, byte[] data) {

        return 0;
    }
}
