package it.unimib.finalproject.database.utils;

/**
 * @author Alessandro Condello
 * @since 29/06/2023
 * @last-modified 29/06/2023
 */
public class ReadWriteLock{

    private int readers = 0,
                writers = 0,
                consecutiveReaders = 0,
                writerRequests = 0;

    public synchronized void lockRead() throws InterruptedException{
        while(writers > 0 && consecutiveReaders > 2 && writerRequests > 0){
            wait();
        }
        consecutiveReaders++;
        readers++;
    }

    public synchronized void lockWrite() throws InterruptedException{
        writerRequests++;
        while(readers > 0 || writers > 0){
            wait();
        }
        consecutiveReaders = 0;
        writers++;
    }


    public synchronized void unlockWrite(){
        writers--;
        writerRequests--;
        notifyAll();
    }

    public synchronized void unlockRead(){
        readers--;
        notifyAll();
    }

}
