package edu.escuelaing.arep.app;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest {

    @Test
    public void peticion() {
        Socket echoSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;
        Thread servidor = new Thread(() -> {
            try {
                HttpServer.main(null);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                fail();
                // e.printStackTrace();
            }
        });
        servidor.start();
        try {
            echoSocket = new Socket("127.0.0.1", 35000);
            out = new PrintWriter(echoSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don’t know about host!.");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn’t get I/O for " + "the connection to: localhost.");
            System.exit(1);
        }

        String res;

        try {
            out.println("GET /lobo.jpg \n");
            res = in.readLine();
            assertTrue(res.contains("200"));

        } catch (IOException e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
            fail();
        }

    }

}
