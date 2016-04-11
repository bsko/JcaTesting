package com.airhacks.jca;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;

/**
 * Created by atemnov on 11.04.2016.
 */
public class SocketClient {

    public static void main(String[] args) throws Exception {
        final InetAddress address = InetAddress.getByName("localhost");
        final int port = 9001;

        final SocketAddress socketAddress = new InetSocketAddress(address, port);
        final Socket socket = new Socket();
        socket.setSoLinger(false, 0);

        final int timeoutMs = 1000;
        socket.connect(socketAddress, timeoutMs);
        final OutputStream outputStream = socket.getOutputStream();
        String sendString = args.length == 0 ? "sendString" : args[0];
        outputStream.write(sendString.getBytes());
        socket.shutdownOutput();

        final InputStream inputStream = socket.getInputStream();
        final BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));

        String str;
        while ((str = rd.readLine()) != null) {
            System.out.println(str);
        }
        rd.close();
        socket.close();
    }
}
