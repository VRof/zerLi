package client;

import java.io.Serializable;

public class MyFile  implements Serializable {
    private int size=0;
    public  byte[] mybytearray;


    public void initArray(int size)
    {
        mybytearray = new byte [size];
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public byte[] getMybytearray() {
        return mybytearray;
    }

    public byte getMybytearray(int i) {
        return mybytearray[i];
    }

    public void setMybytearray(byte[] mybytearray) {

        for(int i=0;i<mybytearray.length;i++)
            this.mybytearray[i] = mybytearray[i];
    }
}
