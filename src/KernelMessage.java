public class KernelMessage {
    // instance variables
    private int senderPid;
    private int targetPid;

    private int messageIndicator;

    private byte[] data;

    private Kernel copyMessage;

    // regular constructor
    public KernelMessage(int senderPid, int targetPid, int messageIndicator, byte[] data) {
        this.senderPid =senderPid;
        this.targetPid = targetPid;
        this.messageIndicator = messageIndicator;
        this.data = data;

    }
    // Copy constructor accepts the kernelMessage and duplicates it.
    public KernelMessage(KernelMessage message){
        this.senderPid = message.senderPid;
        this.targetPid = message.targetPid;
        this.messageIndicator = message.messageIndicator;
        this.data = message.data.clone();
    }


    // Getters and setters
    public void setSenderPid(int senderPid) {
        this.senderPid = senderPid;
    }

    public void setTargetPid(int targetPid) {
        this.targetPid = targetPid;
    }

    public int getSenderPid() {
        return senderPid;
    }

    public int getTargetPid() {
        return targetPid;
    }

    /**
     *
     * ToString method see the actual messages
     * @return
     */

    @Override
    public String toString() {

        if(senderPid  == 3) {
            return "Pong: From " + senderPid + " to " + targetPid + " What " + messageIndicator;
        }else if(senderPid == 4){
            return "Ping From " + senderPid + " to " + targetPid + " What " + messageIndicator;
        }else {
            return "";
        }

    }
}
