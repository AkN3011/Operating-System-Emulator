public class OperatingSystem  {


    public static void main(String[] args) throws Exception {
        // Calls startup with a new instance of Hello world, and then creates a new process with an instance of Goodbye world
        OS.Startup(new HelloWorld());

        try {
            Thread.sleep( 50); // sleep for 50 ms
        } catch (Exception e) { };

       OS.CreateProcess(new Pong());


        try {
            Thread.sleep( 50); // sleep for 50 ms
        } catch (Exception e) { };


        OS.CreateProcess(new Ping());
    }


}