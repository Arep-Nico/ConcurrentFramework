package escuelaing.arep.concurrentFramework.pojos;

import escuelaing.arep.concurrentFramework.framework.annotatations.server;
import escuelaing.arep.concurrentFramework.framework.annotatations.web;

/**
 * webHello
 */
@server(path = "/hello")
public class webHello {

    @web(path = "/greeting")
    public static String Hello() {
        return "<h1>Hello world!</h1>";
    }
}