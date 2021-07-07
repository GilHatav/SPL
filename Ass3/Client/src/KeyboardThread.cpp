//
// Created by gil on 08/01/2020.
//

#include "KeyboardThread.h"
#include <iostream>
#include <thread>
#include <StompEncDec.h>


using namespace std;

KeyboardThread::KeyboardThread(ConnectionHandler& connectionHandler, BookClub& MyClub ,StompEncDec& stomp ,   bool &terminate , bool& connected):connectionHandler(connectionHandler), myClub(MyClub),stomp(stomp) , terminate(terminate) , connected(connected) {}
void KeyboardThread :: run() {

    while (!(terminate)) {
        const short bufsize = 1024;
        char buf[bufsize];
        std::cin.getline(buf, bufsize);
        std::string line(buf);
        if(line=="bye"){
            terminate=true;}
        vector<string> SplittedCommand = splitString(line);

        if(SplittedCommand[0] == "login")
        {
            string hostPort = SplittedCommand[1];
            int index = indexof(hostPort,  ":");
            string host = hostPort.substr(0,index);
            string port = hostPort.substr(index+1);
            string name = SplittedCommand[2];
            string password = SplittedCommand[3];
            while(!connected){
                myClub.setName(name);}
                string toServer = stomp.login(name , password);
                if (!connectionHandler.sendLine(toServer)) {
                    std::cout << "Disconnected. Exiting...\n" << std::endl;
                    break;
                }
        }

        else if(SplittedCommand[0] == "join")
        {
            string gener = SplittedCommand[1];

            bool newJoin = myClub.insert_id(gener);
            if(newJoin){
                int id = myClub.getId_Join(gener);
                string ans = stomp.join(gener,id, id);
                connectionHandler.sendLine(ans);
            }
        }

        else if(SplittedCommand[0] == "exit")
        {
            string gener =SplittedCommand[1];
            int id = myClub.getId_Join(gener);
          //  int id = myClub.getId();
          if(id!=-1){
              string ans = stomp.exitGener(gener,id, id);
              connectionHandler.sendLine(ans);
          }
            myClub.Erase_id(gener);
        }

        else if(SplittedCommand[0] == "add")
        {
            string gener = SplittedCommand[1];;
            string name = myClub.getName();
            string book = "";
            for(int i =2; i<(int)SplittedCommand.size(); i++){
                if (i != (int)SplittedCommand.size() - 1) {
                    book = book + SplittedCommand[i] + " ";
                }
                else{book = book+ SplittedCommand[i];}
            }
            string ans = stomp.add(gener, name , book);
            myClub.addBook(gener, book);
            connectionHandler.sendLine(ans);
        }

        else if(SplittedCommand[0] == "borrow")
        {
          string gener =SplittedCommand[1];
            string book = "";
            for(int i =2; i<(int)SplittedCommand.size(); i++) {
                if (i != (int)SplittedCommand.size() - 1) {
                    book = book + SplittedCommand[i] + " ";
                }
                else{book = book+ SplittedCommand[i];}
            }
          string name = myClub.getName();
          myClub.addWish(book);
          string ans = stomp.borrowRequest(gener,name ,book);
          connectionHandler.sendLine(ans);
        }

        else if(SplittedCommand[0] == "return")
        {
            string gener =SplittedCommand[1];
            string book = "";
            for(int i =2; i<(int)SplittedCommand.size(); i++) {
                if (i !=(int) SplittedCommand.size() - 1) {
                    book = book + SplittedCommand[i] + " ";
                }
                else{book = book+ SplittedCommand[i];}
            }
            string borrow = myClub.getBorrow(book);
            myClub.removeBorrowedBook(gener , book , borrow);
            string ans = stomp.returnBook(gener, book , borrow);
            connectionHandler.sendLine(ans);
        }

        else if(SplittedCommand[0] == "status")
        {
            string gener =SplittedCommand[1];
            string ans = stomp.statusRequest(gener);
            connectionHandler.sendLine(ans);
        }

        else if(SplittedCommand[0] == "logout")
        {
            string ans = stomp.logout(myClub.getId());
            connectionHandler.sendLine(ans);
        }

    }

}


vector<string> KeyboardThread::  splitString (string s) {
    vector<string> split;
    string next = "";

    for (int i = 0; i < (int)s.length(); i++) {
        if (s.at(i) != ' ') {
            next = next + s.at(i);
        } else {
            split.push_back(next);
            next = "";
        }
    }

    if (next != "")
        split.push_back(next);

    return split;

}

int KeyboardThread::  indexof(std::string& text, std::string pattern) {
    std::string::size_type loc = text.find(pattern, 0);
    if (loc != std::string::npos) {
        return loc;
    } else {
        return -1;
    }
}
