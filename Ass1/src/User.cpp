//
// Created by omerkempner on 20.11.2019.
//

#include "../include/User.h"
#include <string>
#include <utility>
#include <vector>
#include "../include/Watchable.h"
#include "../include/Session.h"
#include <map>

using namespace std;
User::~User()
{
    for(auto & i : history)
        delete i;
}

User::User(string _name) :  history() ,name(std::move(_name)) {history={};}//constructor
User:: User(const User& other, const string& name):history(), name(name) {} //copy constructor

bool User::existInHistory(int i) {
    for(auto & j : history)
        if(j->getID()==i)
            return true;
    return false;
}

std::string User:: getName()
{ return  this->name;}

vector<Watchable*> User::get_history() const {return this->history;}

void User::set_history(Watchable *w) {this->history.push_back(w);}
//LengthRecommenderUser
LengthRecommenderUser::LengthRecommenderUser(const string &name) : User(name) {}
LengthRecommenderUser::LengthRecommenderUser (const User& other, const string& name): User(other,name){
    for(int i=0; i< (int)other.get_history().size(); i++){
        history.push_back(other.get_history()[i]->clone());
    }
}
User* LengthRecommenderUser:: duplicate (string name) const {
    return new LengthRecommenderUser(*this,name);
}
Watchable* LengthRecommenderUser::getRecommendation(Session &s)
{
    double avg(0);
    for(auto & i : history){
        avg=avg+i->getLength();
    }
    avg = avg/history.size();
    int id(-1);
    for(int i=0; i< (int)s.getContent().size();i++){
        if(!existInHistory((int)s.getContent()[i]->getID())){
            id=i;
            break;
        }


    }
    if(id==-1)
        return nullptr;
    for (int i=0; i<  (int)s.getContent().size(); i++){
        if(!existInHistory(s.getContent()[i]->getID()) &&
           abs(s.getContent()[i]->getLength() - avg) < abs(s.getContent()[id]->getLength() - avg)) {
            id =i;
        }
    }
    return  s.getContent()[id];
}
User* LengthRecommenderUser:: clone(){return new LengthRecommenderUser(*this);}

//RerunRecommenderUser
RerunRecommenderUser::RerunRecommenderUser(const string &name) : User(name), nextRecommendation() {}
RerunRecommenderUser::RerunRecommenderUser (const User& other, string name): User(other,name), nextRecommendation(){
    for(int i=0; i< (int)other.get_history().size(); i++){
        history.push_back(other.get_history()[i]->clone());
    }
}
User* RerunRecommenderUser:: duplicate  (string name) const {
    return new RerunRecommenderUser(*this,name);
}
Watchable* RerunRecommenderUser::getRecommendation(Session &s)
{
    if(history.size()==1)
        nextRecommendation = 0;
    else{
        nextRecommendation = (nextRecommendation+1)%history.size();
    }
    return history[nextRecommendation];}
User* RerunRecommenderUser:: clone(){return new RerunRecommenderUser(*this);}

//GenreRecommenderUser
GenreRecommenderUser::GenreRecommenderUser(const string &name) : User(name) {}
GenreRecommenderUser::GenreRecommenderUser (const User& other, const string& name): User(other,name){
    for(int i=0; i< (int)other.get_history().size(); i++){
        history.push_back(other.get_history()[i]->clone());
    }
}
User* GenreRecommenderUser:: duplicate (string name) const {
    return new GenreRecommenderUser(*this,name);
}
Watchable* GenreRecommenderUser::getRecommendation(Session &s)
{
     map<string,int> popularTags;
    for(auto & i : history){
        for(int j=0; j< (int)i->getTags().size();j++ ){
            if(popularTags.find(i->getTags()[j]) == popularTags.end())
                popularTags.insert(pair<string,int>(i->getTags()[j],1));
            else
                popularTags.find(i->getTags()[j])->second++;
        }
    }

    while(!popularTags.empty()){
        pair<string,int> maxPair("a",-1);
        for (auto & popularTag : popularTags)
            if(popularTag.second>maxPair.second){
                maxPair.first=popularTag.first;
                maxPair.second=popularTag.second;
            }
        for(int i=0; i< (int)s.getContent().size();i++){
            for(int j=0; j<(int)s.getContent()[i]->getTags().size();j++){
                if(s.getContent()[i]->getTags()[j] == maxPair.first && !existInHistory(s.getContent()[i]->getID()))
                    return s.getContent()[i];
            }
        }
        popularTags.erase(maxPair.first);
    }
    return nullptr;
}
User* GenreRecommenderUser:: clone(){return new GenreRecommenderUser(*this);}

