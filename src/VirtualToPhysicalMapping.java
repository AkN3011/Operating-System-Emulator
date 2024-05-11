public class VirtualToPhysicalMapping {
    private int physicalPageNumber;
    private int diskPageNumber;

    public VirtualToPhysicalMapping(){
        this.physicalPageNumber = -1;
        this.diskPageNumber = -1;

    }

    public int getPhysicalPageNumber() {
        return physicalPageNumber;
    }

    public int getDiskPageNumber() {
        return diskPageNumber;
    }

    public void setPhysicalPageNumber(int physicalPageNumber) {
        this.physicalPageNumber = physicalPageNumber;
    }

    public void setDiskPageNumber(int diskPageNumber) {
        this.diskPageNumber = diskPageNumber;
    }
}
