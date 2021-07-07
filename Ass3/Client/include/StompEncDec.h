//
// Created by gil on 09/01/2020.
//
#include <vector>
#include <string>

#ifndef BOOST_ECHO_CLIENT_STOMPENCDEC_H
#define BOOST_ECHO_CLIENT_STOMPENCDEC_H
using namespace std;

class  StompEncDec {

private:




public:
    vector<string> splitString (string s);
    string login(string username , string password);
    string join(string gener , int id , int receipt);
    string add(string gener , string name , string bookName);
    string borrowRequest(string gener , string name , string bookName);
    string borrowAct (string gener , string book , string borrow);
    string returnBook (string gener , string book , string borrow);
    string statusRequest (string gener);
    string statusAct (string gener , string name , vector<string>& books);
    string logout(int id);
    int indexof(std::string& text, std::string pattern);
    string HasBook(string name , string gener ,  string book);
    string exitGener(string gener , int id , int reciept);


};
#endif //BOOST_ECHO_CLIENT_STOMPENCDEC_H
