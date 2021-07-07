//
// Created by gil on 08/01/2020.
//

#include <BookClub.h>

#include <utility>

BookClub ::BookClub():Inventory() , Borrow() , myWishes() , id(0) , joined(), myName()  {}

bool BookClub::addBook(const string& gener, string book) {
    std::map<string,vector<string>>::iterator it;
    it = Inventory.find(gener);
    if(it!= Inventory.end()){
        Inventory[gener].push_back(book);
    }
   else {
        vector<string> toAdd;
        toAdd.push_back(book);
        Inventory.insert({gener,toAdd});
   }

    return true;
}


bool BookClub::removeBook(const string& gener ,string book) {
    std::map<string,vector<string>>::iterator it;
    it = Inventory.find(gener);
    int index = -1;
    if(it!= Inventory.end()){
        vector<string> update = Inventory[gener];
        for(int i =0; i<(int)update.size(); i++){
            if(update[i]== book){
                index = i;
                break;
            }

        }
        if(index!=-1){
            Inventory[gener].erase(Inventory[gener].begin() + index );
            return true;
        }
        return false;
    }
    return false;
}

string BookClub ::getBorrow(string book) {
    std::map<string,string>::iterator it;
    it = Borrow.find(book);
    if(it!= Borrow.end())
        return Borrow[book];
    else
        return "book is not borrowed or not exist";
}

bool BookClub ::addBorrowedBook(const string& gener, string book, string BorrowedFrom) {
    addBook(gener , book);
    Borrow.insert({book , BorrowedFrom});
    return true;
}

bool BookClub ::removeBorrowedBook(const string& gener, const string& book, const string& BorrowedFrom) {
  bool a =  removeBook(gener , book);
    std::map<string,string>::iterator it;
    it = Borrow.find(book);
    if(it!= Borrow.end())
        Borrow.erase(book);
    return a;
}

void BookClub ::updateId() {id = id+1;}
void BookClub ::setName(string name) {myName = std::move(name);}
int BookClub ::getId() { return id;}
string BookClub::getName() { return myName;}

vector<string> BookClub ::getBooks(string gener) {
    vector<string> v = Inventory[gener];
        return v;
    }


void BookClub ::addWish(const string& book) {
    myWishes.push_back(book);
}

bool BookClub ::ifWish(string book) {
    for(int i=0 ; i< (int)myWishes.size(); i++){
        if(myWishes[i]==book)
            return true;
    }
    return false;
}

int BookClub ::getId_Join(string gener) {
    std::map<string,int>::iterator it;
    it = joined.find(gener);
    if(it!=joined.end()){
        return joined[gener];
    }
    return -1;
}

bool BookClub ::insert_id(const string& gener) {
    std::map<string,int>::iterator it;
    it = joined.find(gener);
    if(it!=joined.end()){
        return false;
    }
    joined.insert({gener , id});
    updateId();
    return true;

}
bool BookClub ::Erase_id(const string& gener) {
    std::map<string,int>::iterator it;
    it = joined.find(gener);
    if(it!=joined.end()){
        joined.erase(gener);
        return true;
    }
    return false;
}