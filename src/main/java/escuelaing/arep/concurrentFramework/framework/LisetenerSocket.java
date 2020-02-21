package escuelaing.arep.concurrentFramework.framework;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * LisetenerSocket
 */
public class LisetenerSocket {

    private final ServerSocket serverSocket;
    private final ExecutorService es;
    private Map<String, Method> mappingUrl = new HashMap<String, Method>();
    private boolean running;

    public LisetenerSocket(int poolSize) throws IOException {
        serverSocket = new ServerSocket(getPort());
        mappingUrl = LoadClasses.getPathClass("escuelaing.arep.concurrentFramework");
        es = Executors.newFixedThreadPool(poolSize);
        running = true;
    }

    public void start(){
        System.out.println("listening port: " + serverSocket.getLocalPort());
        while (running) {
            try {
                es.execute(new HandlerRequest(mappingUrl, serverSocket.accept()));
            } catch (IOException e) {
                es.shutdown();
            }
        }
    }

    /**
    * retorna un puerto disponible 
    * @return
    */
    private int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 5000; // returns default port if heroku-port isn't set (i.e. on localhost)
    }

    
}