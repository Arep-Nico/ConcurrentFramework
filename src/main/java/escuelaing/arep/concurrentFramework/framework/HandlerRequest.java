package escuelaing.arep.concurrentFramework.framework;

import java.net.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;

import escuelaing.arep.concurrentFramework.framework.models.User;
import escuelaing.arep.concurrentFramework.framework.services.ParserJson;

public class HandlerRequest implements Runnable {

   public static final String USERPATH = System.getProperty("user.dir");
   public static final String SEPARATOR = System.getProperty("file.separator");
   private Map<String, Method> mappingUrl = new HashMap<String, Method>();
   private Socket clientSocket;

   public HandlerRequest(Map<String, Method> url, Socket clientSocket) {
      this.mappingUrl = url;
      this.clientSocket = clientSocket;
   }

   @Override
   public void run() {
      try {
         start();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public void start() throws IOException {
      OutputStream ops = clientSocket.getOutputStream();
      PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
      BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      BufferedOutputStream outbs = new BufferedOutputStream(clientSocket.getOutputStream());

      String inputLine, fileName = "/";
      while ((inputLine = in.readLine()) != null) {
         if (inputLine.startsWith("GET"))
            fileName = inputLine.substring(inputLine.indexOf("/") + 1, inputLine.indexOf("HTTP"));
         if (!in.ready()) {
            break;
         }
      }
      if (fileName.equals(" "))
         fileName = "index.html ";
      if (fileName.equals("getDB "))
         HandlerDB(out);
      else if (fileName.startsWith("api"))
         HanderApi(out, fileName.substring(fileName.indexOf("/"), fileName.length() - 1));
      else if (!fileName.equals("/"))
         HandlerFiles(fileName, out, outbs, ops);
      out.flush();
      out.close();
      ops.close();
      outbs.close();
      in.close();
   }

   /**
    * Clasifica el contenido de la peticion del cliente
    * 
    * @param fileName
    * @param out
    * @param outbs
    * @param os
    */
   private void HandlerFiles(String fileName, PrintWriter out, BufferedOutputStream outbs, OutputStream os) {

      String path = HandlerRequest.USERPATH + HandlerRequest.SEPARATOR + "src" + HandlerRequest.SEPARATOR + "main"
            + HandlerRequest.SEPARATOR + "java" + HandlerRequest.SEPARATOR + "resources" + HandlerRequest.SEPARATOR
            + fileName.substring(0, fileName.length() - 1);

      System.out.println("Request: " + fileName);
      String contentType = "";

      if (fileName.endsWith(".html ") || fileName.endsWith(".htm "))
         contentType = "text/html";
      else if (fileName.endsWith(".css "))
         contentType = "text/css";
      else if (fileName.endsWith(".ico "))
         contentType = "image/x-icon";
      else if (fileName.endsWith(".png "))
         contentType = "image/png";
      else if (fileName.endsWith(".jpeg ") || fileName.endsWith(".jpg "))
         contentType = "image/jpeg";
      else if (fileName.endsWith(".js "))
         contentType = "application/javascript";
      else if (fileName.endsWith(".json "))
         contentType = "application/json";
      else
         contentType = "text/plain";

      try {
         File file = new File(path);
         BufferedReader br = new BufferedReader(new FileReader(file));

         if (contentType.contains("image/")) {
            HandlerImage(file, os, contentType.substring(contentType.indexOf("/") + 1));
         } else {
            String outString = "HTTP/1.1 200 Ok\r\n" + "Content-type: " + contentType + "\r\n"
                  + "Server: Java HTTP Server\r\n" + "Date: " + new Date() + "\r\n" + "\r\n";
            String st;
            while ((st = br.readLine()) != null)
               outString += st;
            // System.out.println(outString);
            out.println(outString);
            br.close();
         }
      } catch (IOException e) {
         String outputLine = "HTTP/1.1 404 Not Found\r\n" + "Content-type: " + contentType + "\r\n"
               + "Server: Java HTTP Server\r\n" + "Date: " + new Date() + "\r\n" + "\r\n" + "<!DOCTYPE html>" + "<html>"
               + "<head>" + "<meta charset=\"UTF-8\">" + "<title>File Not Found</title>\n" + "</head>" + "<body>"
               + "<center><h1>File Not Found</h1></center>" + "</body>" + "</html>";
         out.println(outputLine);
      }
   }

   /**
    * Transforma la imagen solicitada para mandarla por un socket
    * 
    * @param file
    * @param outputStream
    * @param ext
    * @throws IOException
    */
   private void HandlerImage(File file, OutputStream outputStream, String ext) throws IOException {
      FileInputStream fis = new FileInputStream(file);
      byte[] data = new byte[(int) file.length()];
      fis.read(data);
      fis.close();

      // Cabeceras con la info de la imágen
      DataOutputStream binaryOut = new DataOutputStream(outputStream);
      String outString = "HTTP/1.1 200 Ok\r\n" + "Content-type: image/" + ext + "\r\n" + "Server: Java HTTP Server\r\n"
            + "Date: " + new Date() + "\r\n" + "Content-Length: " + data.length + "\r\n" + "\r\n";
      binaryOut.writeBytes(outString);
      binaryOut.write(data);

      binaryOut.close();
   }

   /**
    * 
    * @param out
    */
   private void HandlerDB(PrintWriter out) {
      DataBase.connection();
      List<User> res = DataBase.getData("select * from user");
      String outString = "HTTP/1.1 200 Ok\r\n" + "Content-type: " + "application/json" + "\r\n";
      try {
         outString += ParserJson.toJson(res);
      } catch (JsonProcessingException e) {
         e.printStackTrace();
      }
      out.println(outString);
   }

   /**
    * optiene el valor del metodo especificado y lo estructura en un html
    * @param out
    * @param path
    */
   private void HanderApi(PrintWriter out, String path) {
      Method m = mappingUrl.get(path);
      String res = "";
      if (!m.equals(null)) {
         try {
            res = (String) m.invoke(null);
         } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
            res = "<center><h1>Can't find a handler</h1></center>";
         }
      }

      String outString = 
         "HTTP/1.1 200 Ok\r\n" + 
         "Content-type: " + "text/html" + "\r\n" + 
         "Server: Java HTTP Server\r\n" + 
         "Date: " + new Date() + "\r\n" + 
         "\r\n" +
         "<!DOCTYPE html>" + 
            "<html>" + 
            "<head>" + 
            "<meta charset=\"UTF-8\">" + 
            "<title>DataBase</title>\n" + 
            "</head>" + 
            "<body>" + 
            res +
            "</body>" + 
            "</html>";
      out.println(outString);
   }
}