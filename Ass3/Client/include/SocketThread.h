//
// Created by gil on 08/01/2020.
//

#include "connectionHandler.h"
#include "BookClub.h"
#include "StompEncDec.h"

#ifndef BOOST_ECHO_CLIENT_SOCKETTHREAD_H
#define BOOST_ECHO_CLIENT_SOCKETTHREAD_H


class SocketThread {

public:
    SocketThread(ConnectionHandler& connectionHandler,BookClub& MyClub , StompEncDec& stomp , bool& terminate ,bool& connected);
    int indexof(std::string& text, std::string pattern);
    void run();
private:
    ConnectionHandler &connectionHandler;
    BookClub& myClub;
    StompEncDec& stomp;
    bool& terminate;
    bool& connected;




};
#endif //BOOST_ECHO_CLIENT_SOCKETTHREAD_H

