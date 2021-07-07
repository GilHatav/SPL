//
// Created by gil on 08/01/2020.
//

#include "connectionHandler.h"
#include "BookClub.h"
#include "StompEncDec.h"



#ifndef BOOST_ECHO_CLIENT_KEYBOARDTHREAD_H
#define BOOST_ECHO_CLIENT_KEYBOARDTHREAD_H


class KeyboardThread {

public:
    KeyboardThread(ConnectionHandler& connectionHandler, BookClub& MyClub, StompEncDec& stomp  , bool& terminate ,bool& connected);
    vector<string> splitString (string s);
    int indexof(std::string& text, std::string pattern);
    void run();
private:
    ConnectionHandler &connectionHandler;
    BookClub& myClub;
    StompEncDec& stomp;
    bool& terminate;
    bool& connected;


};
#endif //BOOST_ECHO_CLIENT_KEYBOARDTHREAD_H
