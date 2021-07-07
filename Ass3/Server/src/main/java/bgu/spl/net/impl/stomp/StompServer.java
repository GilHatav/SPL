package bgu.spl.net.impl.stomp;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.StompMessagingProtocolImpl;
import bgu.spl.net.impl.rci.ObjectEncoderDecoder;
import bgu.spl.net.impl.rci.RemoteCommandInvocationProtocol;
import bgu.spl.net.srv.MessageEncoderDecoderImp;
import bgu.spl.net.srv.Server;

public class StompServer {

    public static void main(String[] args) {

        Integer port = Integer.parseInt(args[0]);
       // Integer port = Integer.parseInt("7777");



      if(args[1].equals("tpc") ){
            //thread per client
            Server.threadPerClient(
                    port, //port
                    () -> new StompMessagingProtocolImpl(), //protocol factory
                    MessageEncoderDecoderImp::new //message encoder decoder factory
            ).serve();
        }



        else if(args[1].equals("reactor")) {
            //reactor
            Server.reactor(
                    Runtime.getRuntime().availableProcessors(),
                    port, //port
                    () -> new StompMessagingProtocolImpl(), //protocol factory
                    MessageEncoderDecoderImp::new //message encoder decoder factory
            ).serve();
          }
    }


}
