import java.util.*;


public class CrawlerTask implements Runnable {
    
    public URLDepthPair depthPair;
    
    public URLPool myPool;
    
    public CrawlerTask(URLPool pool) {
        myPool = pool;
    }
    
    public void run() {

        depthPair = myPool.get();
        
        int myDepth = depthPair.getDepth();
        
        LinkedList<String> linksList = new LinkedList<String>();
        linksList = Crawler.getAllLinks(depthPair);
        
        for (int i=0;i<linksList.size();i++) {
            String newURL = linksList.get(i);
            
            URLDepthPair newDepthPair = new URLDepthPair(newURL, myDepth + 1);
            myPool.put(newDepthPair);
        }
    }
}