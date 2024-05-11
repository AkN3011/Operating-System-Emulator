import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FakeFileSystem implements Device{
    private RandomAccessFile[] randomAccessFiles;

    // Initializes an array that takes in 10 Random Access Files
    public FakeFileSystem(){
        randomAccessFiles = new RandomAccessFile[10];
    }

    public RandomAccessFile[] getRandomAccessFiles() {
        return randomAccessFiles;
    }

    /**
     * Opens accepts a string
     * if the string is null or empty then method throws an exception
     * It iterates through the array, and if there is an empty spot in the array, then it opens a new random access file and returns the index where it is stored.
     *If an occurs during opening of a random acess file then it throws an exception, and it will we caught by the try catch
     * if the array is full then -1 one is returned
     * @param s
     * @return
     * @throws Exception
     */
    @Override
    public int Open(String s) throws Exception {

        if(s == null || s.isEmpty()){
            throw new IllegalArgumentException("Filename cannot be null or empty");
        }

        for(int i = 0; i < randomAccessFiles.length; i++){
            if(randomAccessFiles[i] == null)
            {
                try {
                    randomAccessFiles[i] = new RandomAccessFile(s,"rw");
                    return i;
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                }

            }
        }
        return -1;
    }

    /**
     * File takes the id and acces the array
     * If there is device at that index then close() is called and the device will be set to null
     * @param id
     */
    @Override
    public void Close(int id)  {
        System.out.println("Fake file system device is closed");
             if(randomAccessFiles[id] != null){

                 try {
                     randomAccessFiles[id].close();
                     randomAccessFiles[id] = null;
                 }catch (Exception e){
                     e.printStackTrace();
                 }
             }

    }

    /**
     * Read takes in an int and size
     * The id is used to access the array's index, and if it isn't null, then read is called on that device, and the returned array is returned with the method call.
     * if it is null then an empty byte array is returned
     * @param id
     * @param size
     * @return
     * @throws IOException
     */

    @Override
    public byte[] Read(int id, int size) throws IOException {
        byte[] readArray = new byte[size];
            if(randomAccessFiles[id] != null) {
                readArray[id] = (byte) randomAccessFiles[id].read();
            }else{
                return new byte[0];
            }

        return readArray;
    }

    /**
     *
     * Seeks takes in id and to
     * if the there is device in the array at that specific index then seek is called and the to is passed in with it
     * else a exception is thrown
     * @param id
     * @param to
     * @throws IOException
     */
    @Override
    public void Seek(int id, int to) throws IOException {
             if(randomAccessFiles[id] != null){
                 randomAccessFiles[id].seek(to);
             }else {
                 throw new RuntimeException("RandomAccessFile: Random access file doesn't exist, and can't execute seek");
             }

    }


    /**
     * Write takes an int id  and byte array data
     * The id used to access the random access file and the data is passed into that write method
     * if the device doesn't exist then 0 is returned
     *
     * @param id
     * @param data
     * @return
     * @throws IOException
     */
    @Override
    public int Write(int id, byte[] data) throws IOException {


        if (randomAccessFiles[id] != null) {
            randomAccessFiles[id].write(data);
        } else {
            return 0;
        }

        return data.length;
    }
}
