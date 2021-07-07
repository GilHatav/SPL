//
// Created by gil on 08/01/2020.
//

#include <map>
#include <vector>
#ifndef BOOST_ECHO_CLIENT_BOOKCLUB_H
#define BOOST_ECHO_CLIENT_BOOKCLUB_H
using namespace std;


 class BookClub {

 private:
    map<string, vector<string>> Inventory; //gener - books map.
    map<string, string> Borrow; //key is the book ,  value is the user.
    vector<string> myWishes;
    int id;
     map<string, int> joined;
    string myName;


 public:
     BookClub();
     void setName(string name);
     void updateId ();
     bool addBook(const string& gener, string book);
     bool removeBook(const string& gener ,string book);
     int getId();
     string getName();
     string getBorrow(string book);
     bool addBorrowedBook(const string& gener , string book , string BorrowedFrom);
     bool removeBorrowedBook(const string& gener , const string& book , const string& BorrowedFrom);
     vector<string> getBooks (string gener);
     bool ifWish (string book);
     void addWish(const string& book);
     int getId_Join (string gener);
     bool insert_id(const string& gener);
     bool Erase_id(const string& gener);

};
#endif //BOOST_ECHO_CLIENT_BOOKCLUB_H
