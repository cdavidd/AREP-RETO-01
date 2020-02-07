package edu.escuelaing.arep.app;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;

public class HttpServer {
    public static void main(String[] args) throws IOException {
        int port = getPort();
        ServerSocket serverSocket = null;
        while (true) {
            try {
                serverSocket = new ServerSocket(port);
            } catch (IOException e) {
                System.err.println("Could not listen on port: " + port);
                System.exit(1);
            }

            Socket clientSocket = null;
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String inputLine, archivo;
            archivo = "/";
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Recibí: " + inputLine);
                if (!in.ready()) {
                    break;
                }
                if (inputLine.contains("GET")) {
                    System.out
                            .println(inputLine.indexOf("/") + " " + inputLine.indexOf(" ", inputLine.indexOf(" ") + 1));
                    archivo = inputLine.substring(inputLine.indexOf("/") + 1,
                            inputLine.indexOf(" ", inputLine.indexOf(" ") + 1));
                    break;
                }
            }

            String[] pathType = getType(archivo);

            if (pathType[1] == "html") {
                getFile(pathType[0], out);
            } else if (pathType[1] == "img") {
                getImg(pathType[0], clientSocket.getOutputStream());
            }

            // outputLine = "HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n" + "\r\n" +
            // getFile(path);

            // out.println(outputLine);
            out.close();
            in.close();
            clientSocket.close();
            serverSocket.close();
        }
    }

    private static String[] getType(String file) {
        String path = "src/main/resources/";
        String[] res = new String[2];

        if (file.endsWith("html")) {
            path += "web/" + file;
            res[1] = "html";

        } else if (file.endsWith(".js")) {
            path += "js/" + file;
            res[1] = "js";
        } else {
            path += "img/" + file;
            res[1] = "img";
        }
        res[0] = path;
        return res;
    }

    private static void getFile(String path, PrintWriter out) {
        File archivo = new File(path);
        String temp, outputLine = "HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n" + "\r\n";
        System.out.println("outputLine " + archivo.exists() + " path " + path);
        if (archivo.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(archivo));
                while ((temp = br.readLine()) != null) {
                    System.out.println(temp);
                    outputLine += temp;
                }
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated
                e.printStackTrace();
            }
        }
        // System.out.println("outputLine " + outputLine);
        out.println(outputLine);
    }

    private static void getImg(String path, OutputStream clientOutput) {
        try {
            BufferedImage image = ImageIO.read(new File(path));
            ByteArrayOutputStream ArrBytes = new ByteArrayOutputStream();
            DataOutputStream writeimg = new DataOutputStream(clientOutput);
            ImageIO.write(image, "PNG", ArrBytes);
            writeimg.writeBytes("HTTP/1.1 200 OK \r\n" + "Content-Type: image/png \r\n" + "\r\n");
            writeimg.write(ArrBytes.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 35000; // returns default port if heroku-port isn't set(i.e. on localhost)
    }
}