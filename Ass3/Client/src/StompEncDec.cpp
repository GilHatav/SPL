//
// Created by gil on 09/01/2020.
//
#include <StompEncDec.h>


string StompEncDec ::  login(string username, string password ) {
    string STOMP;
    STOMP = STOMP + "CONNECT" + "\n";
    STOMP = STOMP + "accept-version:1.2" + "\n";
    STOMP = STOMP + "host:stomp.cs.bgu.ac.il"+ "\n";
    STOMP = STOMP + "login:" + username + "\n";
    STOMP = STOMP + "passcode:" + password + "\n";
    STOMP = STOMP +"\n"; // Begining of frame body
    STOMP = STOMP + "\0";
return STOMP;
}

string StompEncDec ::join(string gener, int id , int receipt) {

    string STOMP;
    STOMP = STOMP + "SUBSCRIBE" + "\n";
    STOMP = STOMP + "destination:" + gener+"\n";
    STOMP = STOMP + "id:" + to_string(id) + "\n";
    STOMP = STOMP + "receipt:" + to_string(receipt) + "\n"; // client id or -1 if not needed receipt
    STOMP = STOMP +"\n"; // Begining of frame body
    STOMP = STOMP + "\0";
    return STOMP;

}

string StompEncDec ::add(string gener, string name, string bookName) {
    string STOMP;
    STOMP = STOMP + "SEND" + "\n";
    STOMP = STOMP + "destination:" + gener+"\n" ;
    STOMP = STOMP +"\n"; // Begining of frame body
    STOMP = STOMP + name + " has added the book " + bookName+"\n";
    STOMP = STOMP + "\0";
return STOMP;
}

string StompEncDec ::borrowRequest(string gener, string name, string bookName) {
    string STOMP;
    STOMP = STOMP + "SEND" + "\n";
    STOMP = STOMP + "destination:" + gener+"\n" ;
    STOMP = STOMP +"\n"; // Begining of frame body
    STOMP = STOMP + name + " wish to borrow " + bookName+"\n";
    STOMP = STOMP + "\0";
    return STOMP;
}

string StompEncDec ::HasBook(string name, string gener ,  string bookName) {
    string STOMP;
    STOMP = STOMP + "SEND" + "\n";
    STOMP = STOMP + "destination:" + gener+"\n" ;
    STOMP = STOMP +"\n"; // Begining of frame body
    STOMP = STOMP + name + " has " + bookName+"\n";
    STOMP = STOMP + "\0";
    return STOMP;
}

string StompEncDec ::borrowAct(string gener, string book, string borrow) {
    string STOMP;
    STOMP = STOMP + "SEND" + "\n";
    STOMP = STOMP + "destination:" + gener+"\n" ;
    STOMP = STOMP +"\n"; // Begining of frame body
    STOMP = STOMP + "Taking " + book + " from " + borrow+"\n";
    STOMP = STOMP + "\0";

    return STOMP;
}

string StompEncDec ::returnBook(string gener, string book, string borrow) {
    string STOMP ;
    STOMP = STOMP + "SEND" + "\n";
    STOMP = STOMP + "destination:" + gener+"\n" ;
    STOMP = STOMP +"\n"; // Begining of frame body
    STOMP = STOMP + "Returning " + book + " to " + borrow+"\n";
    STOMP = STOMP + "\0";
    return STOMP;
}

string StompEncDec ::statusRequest(string gener) {
    string STOMP ;
    STOMP = STOMP + "SEND" + "\n";
    STOMP = STOMP + "destination:" + gener+"\n" ;
    STOMP = STOMP +"\n"; // Begining of frame body
    STOMP = STOMP + "book status"+"\n";
    STOMP = STOMP + "\0";
    return STOMP;
}

string StompEncDec ::statusAct(string gener, string name, vector<string>& books) {
    string STOMP ;
    STOMP = STOMP + "SEND" + "\n";
    STOMP = STOMP + "destination:" + gener;
    STOMP = STOMP +"\n"; // Begining of frame body
    STOMP = STOMP + name + ":";
    for(int i=0; i<(int)books.size(); i++){
        if(i<(int)books.size()-1){
            STOMP=STOMP + books[i] + "," ;
        }

        else
            STOMP=STOMP + books[i];
    }
    STOMP = STOMP + "\0";
    return STOMP;
}

string StompEncDec ::logout(int id) {
    string STOMP ;
    STOMP = STOMP + "DISCONNECT" + "\n";
    STOMP = STOMP + "receipt :" + to_string(id)+"\n";
    STOMP = STOMP + "\0";
    return STOMP;

}

string StompEncDec ::exitGener(string gener, int id , int receipt) {
    string STOMP ;
    STOMP = STOMP + "UNSUBSCRIBE" + "\n";
    STOMP = STOMP + "destination:" + gener+"\n";
    STOMP = STOMP + "id:" + to_string(id) + "\n";
    STOMP = STOMP + "receipt:" + to_string(receipt) + "\n";
    STOMP = STOMP +"\n"; // Begining of frame body
    STOMP = STOMP + "\0";

    return STOMP;

}


vector<string> StompEncDec::  splitString (string s){
    vector<string> split;
    string next ;

    for(int i=0; i<(int)s.length(); i++){
        if(s.at(i)!= ' '){
            next = next +s.at(i);
        }
        else{
            split.push_back(next);
            next = "";
        }
    }

    if(next!="")
        split.push_back(next);

    return split;

}

int StompEncDec::  indexof(std::string& text, std::string pattern) {
    std::string::size_type loc = text.find(pattern, 0);
    if (loc != std::string::npos) {
        return loc;
    } else {
        return -1;
    }
}