package se.edument.ringius.ge.com;

import se.edument.ringius.ge.StringPrinter;
import se.edument.ringius.ge.com.*;

import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.Scanner;

public class Main {
    public static void main(String args[]) {
        for (String s: args)
            System.out.println(s);

        if (args.length > 0) {
            switch (args[0]) {
                case "server":
                    setupServer(args);
                    /*
                    InetAddress addr = InetAddress.getLocalHost();

                    SocketConnectionProvider conProvider = new SocketConnectionProvider(new ObjectReaderWriter(), addr.getHostName(), 14712);
                    //SocketConnectionProvider conProvider = new SocketConnectionProvider(new JsonReaderWriter(), addr.getHostName(), 14712);

                    //2. Server-side, start a ComServer.
                    ComServer comServer = new ComServer();
                    comServer.setConnectionProvider(conProvider);
                    ComHandler handler = new ComHandler(comServer);
                    handler.start();
                    break;
                    */
                case "client":
                    setupClient(args);
                    break;
                default:
                    System.out.println("incorrect usage");
                    break;
            }
        }
        /*
        try {
            InetAddress addr = InetAddress.getLocalHost();

            //Create and start a server...
            //
            //1. Create a connection provider using the ComReaderWriter of choice...
            SocketConnectionProvider conProvider = new SocketConnectionProvider(new ObjectReaderWriter(), addr.getHostName(), 14712);
            //SocketConnectionProvider conProvider = new SocketConnectionProvider(new JsonReaderWriter(), addr.getHostName(), 14712);


            ComServer comServer = new ComServer();
            comServer.setConnectionProvider(conProvider);
            ComHandler handler = new ComHandler(comServer);
            handler.start();

            ComClient c2 = conProvider.createUnconnectedClient()
                    .registerConsumer(new ReceivePrinter()).setName("c2")
                    .start();

            ComClient c3 = conProvider.createUnconnectedClient()
                    .registerConsumer(new ReceivePrinter()).setName("c3")
                    .start();

            ComClient c4 = conProvider.createUnconnectedClient()
                    .registerConsumer(new ReceivePrinter()).setName("c4")
                    .start();

            ComClient c5 = conProvider.createUnconnectedClient()
                    .registerConsumer(new ReceivePrinter()).setName("c5")
                    .start();

            ComClient client = conProvider.createUnconnectedClient()
                    .registerConsumer(new ReceivePrinter()).setName("client")
                    .start();

            c2.subscribe(DataObject.class);
            c3.subscribe(DataObject.class);
            c4.subscribe(DataObject.class);
            c5.subscribe(DataObject.class);
            c2.subscribe(String.class);

            DataObject obj = new DataObject(0);
            for (int i = 0; i < 60; ++i) {
                obj.setIntValue(i);
                long nanos = System.nanoTime();

                if (i == 10) {
                    c2.unsubscribe(DataObject.class);
                }

                if (i == 20) {
                    c3.unsubscribe(DataObject.class);
                    c3.close();
                }

                if (i == 30) {
                    c4.unsubscribe(DataObject.class);
                }

                if (i == 40) {
                    c5.unsubscribe(DataObject.class);
                }

                if (i == 50) {
                    c2.subscribe(DataObject.class);
                }

                client.sendObject(obj.setTimeStampToNow());
                client.sendObject("olle - " + i);

                long newNanos = System.nanoTime();
                System.out.println("Elapsed:" + (newNanos - nanos) / 100_000);
                Thread.sleep(100);
            }

            c2.close();
            c3.close();
            c4.close();
            c5.close();
            client.close();
            handler.stop();
            System.out.println("Everything stopped");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        */
    }

    private static void setupClient(String args[]) {
        System.out.println("starting client");
        try {
            int portNumber = 14712;

            if (args.length > 2) {
                portNumber = Integer.valueOf(args[2]).intValue();
            }
            InetAddress addr = InetAddress.getLocalHost();

            if (args.length > 1) {
               addr = InetAddress.getByName(args[1]);
            }

            //SocketConnectionProvider conProvider = new SocketConnectionProvider(new ObjectReaderWriter(), addr.getHostName(), portNumber);
            SocketConnectionProvider conProvider = new SocketConnectionProvider(new JsonReaderWriter(), addr.getHostName(), portNumber);
            ComClient client = conProvider.createUnconnectedClient().registerConsumer(new ReceivePrinter());
            //client.subscribe(String.class);
            client.subscribe(DataObject.class);
            String input = null;
            client.start();
            input = client.getName() + " started";
            Scanner scanner = new Scanner(new InputStreamReader(
                    System.in, Charset.forName("IBM865")));

            int i=0;
            do {
                client.sendObject(new DataObject(input));
                input = scanner.nextLine();
                System.out.println("**" + input);
            } while(!input.equals("quit"));

            client.close();
        } catch(UnknownHostException e) {
            System.out.println("ups");
        }
    }

    private static void setupServer(String args[]) {
        System.out.println("starting server");
        try {
            InetAddress addr = InetAddress.getLocalHost();

            //SocketConnectionProvider conProvider = new SocketConnectionProvider(new ObjectReaderWriter(), addr.getHostName(), 14712);
            SocketConnectionProvider conProvider = new SocketConnectionProvider(new JsonReaderWriter(), addr.getHostName(), 14712);

            //2. Server-side, start a ComServer.
            ComServer comServer = new ComServer();
            comServer.setConnectionProvider(conProvider);
            ComHandler handler = new ComHandler(comServer);
            handler.start();
            String pelle = "pelle";
            Scanner scanner = new Scanner(System.in);
            while (!pelle.equals("quit"))
                pelle  = scanner.nextLine(); //really needed?
            comServer.close();
        } catch(UnknownHostException err) {

        }
    }
}