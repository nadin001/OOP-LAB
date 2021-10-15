import java.net.*;
import java.util.*;
import java.io.*;


public class Crawler {
    
    

    public static void main(String[] args) {
        int depth = 0;
        int numThreads = 0;
        
        if (args.length != 3) {
            System.out.println("usage: java Crawler <URL> <depth> <number of crawler threads");
            System.exit(1);
        }
        else {
            try {
                depth = Integer.parseInt(args[1]);
                numThreads = Integer.parseInt(args[2]);
            }
            catch (NumberFormatException nfe) {
                System.out.println("usage: java Crawler <URL> <depth> <number of crawler threads");
                System.exit(1);
            }
        }
        
        URLDepthPair currentDepthPair = new URLDepthPair(args[0], 0);
        
        URLPool pool = new URLPool();
        pool.put(currentDepthPair);
        

        int totalThreads = 0;
        int initialActive = Thread.activeCount();
        
        while (pool.getWaitThreads() != numThreads) {
            if (Thread.activeCount() - initialActive < numThreads) {
                CrawlerTask crawler = new CrawlerTask(pool);
                new Thread(crawler).start();
            }
            else {
                try {
                    Thread.sleep(100);  // 0.1 second
                }
                catch (InterruptedException ie) {
                    System.out.println("Caught unexpected " +
                                       "InterruptedException, ignoring...");
                }

            }
        }
                
        Iterator<URLDepthPair> iter = pool.processedURLs.iterator();
        while (iter.hasNext()) {
            System.out.println(iter.next());
        }        // Exit.
        System.exit(0);
        


    }

    public static LinkedList<String> getAllLinks(URLDepthPair myDepthPair) {
        
        LinkedList<String> URLs = new LinkedList<String>();
        
        Socket sock;
        
        try {
            sock = new Socket(myDepthPair.getWebHost(), 80);
        }
        catch (UnknownHostException e) {
            System.err.println("UnknownHostException: " + e.getMessage());
            return URLs;
        }
        catch (IOException ex) {
            System.err.println("IOException: " + ex.getMessage());
            return URLs;
        }
        
        try {
            sock.setSoTimeout(3000);
        }
        catch (SocketException exc) {
            System.err.println("SocketException: " + exc.getMessage());
            return URLs;
        }
        
        String docPath = myDepthPair.getDocPath();
        String webHost = myDepthPair.getWebHost();
        
        OutputStream outStream;
        
        try {
            outStream = sock.getOutputStream();
        }
        catch (IOException exce) {
            System.err.println("IOException: " + exce.getMessage());
            return URLs;
        }
        
        PrintWriter myWriter = new PrintWriter(outStream, true);
        
        myWriter.println("GET " + docPath + " HTTP/1.1");
        myWriter.println("Host: " + webHost);
        myWriter.println("Connection: close");
        myWriter.println();
        
        InputStream inStream;
        
        try {
            inStream = sock.getInputStream();
        }
        catch (IOException excep){
            System.err.println("IOException: " + excep.getMessage());
            return URLs;
        }
        InputStreamReader inStreamReader = new InputStreamReader(inStream);
        BufferedReader BuffReader = new BufferedReader(inStreamReader);
        
        while (true) {
            String line;
            try {
                line = BuffReader.readLine();
            }
            catch (IOException except) {
                System.err.println("IOException: " + except.getMessage());
                return URLs;
            }
            if (line == null)
                break;
            
            
            int beginIndex = 0;
            int endIndex = 0;
            int index = 0;
            
            while (true) {
                
                String URL_INDICATOR = "a href=\"";
                
                String END_URL = "\"";
                
                
                index = line.indexOf(URL_INDICATOR, index);
                if (index == -1)
                    break;
                
                index += URL_INDICATOR.length();
                beginIndex = index;
                
                endIndex = line.indexOf(END_URL, index);
                index = endIndex;
                
                String newLink = line.substring(beginIndex, endIndex);
                URLs.add(newLink);
            }
            
        }
        return URLs;
    }
    
}

