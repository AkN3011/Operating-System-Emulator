import java.io.IOException;

public class VFS implements Device{

    // Array to hold devices and an array to hold the indices returned from the Device
   private Device[] devices;
   private int[] ids;
//initialization
   public VFS(){
       devices = new Device[10];
       ids = new int[10];
   }


    /***
     * Open accepts a string which has information about the device and what is getting passed into the device
     * It takes the string and iterates until the first whitespace to determine what type of device needs to be opened
     * The rest of the strinng is considered as a pass in value
     * The if statements are used to open devices.
     * if the device is random then it goes to the random block and create a new instance of random, and adds it to the empty slot in the device array
     * The id is passed in after it calls open on the device and the returned value is added into the array
     * same with file system
     * If the inputted string doesn't parse to random or file it returns -1
     *
     *
     * @param s
     * @return
     * @throws Exception
     */

    @Override
    public int Open(String s) throws Exception {
       System.out.println(s);
        int j;
       Device device;
       int i = 0 ;
       int id;
       String passInValue;
        String devicetype = "";


          while (!Character.isWhitespace(s.charAt(i))){
              devicetype += s.charAt(i);
             i++;
          }
        passInValue   =  s.replace(devicetype,"").trim();


          if(devicetype.equals("random")){
              device = new RandomDevice();
              id = device.Open(passInValue);
              for( j = 0; j < devices.length; j++ ){

                  if(devices[j] == null){

                      devices[j] = device;
                      ids[j] = id;
                      return j;
                  }
              }
          }else if(devicetype.equals("file")){
              device = new FakeFileSystem();
              id = device.Open(passInValue);

              for( j = 0; j < devices.length; j++ ){
                  if(devices[j] == null){
                      devices[j] = device;
                      ids[j] = id;
                      return j;
                  }
              }

          }else {
              return -1;

          }

        return j;
    }

    /**
     * Close takes the ID and access the Device array to make sure it is valid
     * it calls  close on the Device by accessing the ID array with the passed in index id
     * Also sets the Device and ids array to null and 0;
     *
     * @param id
     * @throws IOException
     */
    @Override
    public void Close(int id) throws IOException {

         if(devices[id] != null){
             devices[id].Close(ids[id]);
             devices[id] = null;
             ids[id] = 0;
         }
    }

    /**
     *
     * Read takes an id and a size
     * The id is use to access the device and ids array to get the value.
     * The values are used to access the device and ids value is then passed into the read method
     * The read method from returns a byte array
     * @param id
     * @param size
     * @return
     * @throws IOException
     */

    @Override
    public byte[] Read(int id, int size) throws IOException {

       byte[] array = devices[id].Read(ids[id],size);
        return array;
    }


    /**
     * Seek takes an id and to
     * id is used access the device and the index from ids array
     * seek is then called with the id returned from the ids array from the vfs
     *
     * @param id
     * @param to
     * @throws IOException
     */
    @Override
    public void Seek(int id, int to) throws IOException {

         devices[id].Seek(ids[id], to);
    }


    /**
     *
     * Write takes in an id, and a byte array
     * Same as before the passed in id used to get the device and index
     * The method then returns an integer after it calls Write method
     * @param id
     * @param data
     * @return
     * @throws IOException
     */
    @Override
    public int Write(int id, byte[] data) throws IOException {

      int returnID = devices[id].Write(ids[id],data);
        return returnID;
    }
}
