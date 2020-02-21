package escuelaing.arep.concurrentFramework;

import java.io.IOException;

import escuelaing.arep.concurrentFramework.framework.LisetenerSocket;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) throws IOException
    {
        System.out.println( "Server starts" );
        LisetenerSocket ls = new LisetenerSocket(20);
        ls.start();
    }
}
