import java.util.*;

public class URLPool {
    
    private LinkedList<URLDepthPair> pendingURLs;
    
    public LinkedList<URLDepthPair> processedURLs;
    
    private ArrayList<String> seenURLs = new ArrayList<String>();
    
    public int waitingThreads;

    public URLPool() {
        waitingThreads = 0;
        pendingURLs = new LinkedList<URLDepthPair>();
        processedURLs = new LinkedList<URLDepthPair>();
    }
    
    public synchronized int getWaitThreads() {
        return waitingThreads;
    }
    
    public synchronized int size() {
        return pendingURLs.size();
    }
    
    public synchronized boolean put(URLDepthPair depthPair) {
        
        boolean added = false;

        if (depthPair.getDepth() < depthPair.getDepth()) {
            pendingURLs.addLast(depthPair);
            added = true;
                
            waitingThreads--;
            this.notify();
        }
        else {
            seenURLs.add(depthPair.getURL());
        }
        return added;
        }

    public synchronized URLDepthPair get() {
        
        URLDepthPair myDepthPair = null;
        
        if (pendingURLs.size() == 0) {
            waitingThreads++;
            try {
                this.wait();
            }
            catch (InterruptedException e) {
                System.err.println("MalformedURLException: " + e.getMessage());
                return null;
            }
        } 
        myDepthPair = pendingURLs.removeFirst();
        seenURLs.add(myDepthPair.getURL());
        processedURLs.add(myDepthPair);
        return myDepthPair;
    }
    public synchronized ArrayList<String> getSeenList() {
        return seenURLs;
    }
}