package bgu.spl.net.srv;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.api.StompMessagingProtocol;
import bgu.spl.net.api.StompMessagingProtocolImpl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class BlockingConnectionHandler<T> implements Runnable, ConnectionHandler<T> {

    private final StompMessagingProtocolImpl protocol;
    private final MessageEncoderDecoderImp encdec;
    private final Socket sock;
    private BufferedInputStream in;
    private BufferedOutputStream out;
    private int idcounter;
    private volatile boolean connected = true;
    private boolean recieveConnect;

    public BlockingConnectionHandler(Socket sock, MessageEncoderDecoderImp reader, StompMessagingProtocolImpl protocol,int idcounter) {
        this.sock = sock;
        this.encdec = reader;
        this.protocol = protocol;
        this.idcounter=idcounter;

    }

    @Override
    public void run() {
        try (Socket sock = this.sock) { //just for automatic closing
            int read;
            in = new BufferedInputStream(sock.getInputStream());
            out = new BufferedOutputStream(sock.getOutputStream());
            while (!protocol.shouldTerminate() && connected && (read = in.read()) >= 0) {
                String nextMessage = encdec.decodeNextByte((byte) read);
                if (nextMessage != null) {
                    if(nextMessage.contains("CONNECT")&&!nextMessage.contains("DISCONNECT")) //add user to connection map
                    {
                        ConnectionsImpl connections = ConnectionsImpl.getInstance();
                        connections.addToUserConnections(this.idcounter,this);
                        this.protocol.start(idcounter,connections);
                    }
                    protocol.process(nextMessage);
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void close() throws IOException {
        connected = false;
        sock.close();
    }

    @Override
    public void send(T msg) {
        try {
            byte[] b = encdec.encode(msg.toString());
            out.write(b);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
