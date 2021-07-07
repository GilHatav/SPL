//
// Created by omerkempner on 20.11.2019.
//

#include "../include/Action.h"

#include <string>
#include "../include/Session.h"
#include "../include/User.h"
#include <unordered_map>
#include <iostream>
using namespace std;

BaseAction::~BaseAction() = default;
BaseAction::BaseAction(): errorMsg() ,status() {
    this->status = PENDING;
    this->errorMsg = "";
}
ActionStatus BaseAction::getStatus() const {return this->status;}
void BaseAction::complete()
{
    this->status=COMPLETED;
}
void BaseAction::error(const std::string &errorMsg) {this->status=ERROR, this->errorMsg=errorMsg;}
string BaseAction::getErrorMsg() const {return this->errorMsg;}

//Create user
void CreateUser::act(Session &sess)
{
    int pos = sess.getCurrInp().find(' ');
    string username = sess.getCurrInp().substr(0,pos);
    string usertype = sess.getCurrInp().substr(pos+1);
    User *u;

    if(sess.getUserMap()->find(username)  != sess.getUserMap()->end() )
    {
        cout<<"Error - ";
        this->error("the new user name is already taken");
        sess.setActionsLog(this);
        cout << getErrorMsg() << endl;
    }
    else if ((usertype != "len") && (usertype != "rer") && (usertype != "gen")) {
        cout<<"Error - ";
        this->error("recommendation algorithm does not exist");
        sess.setActionsLog(this);
        cout << getErrorMsg() << endl;
    } else {


        if (usertype == "len") {
            u = new LengthRecommenderUser(username);

        } else if (usertype == "rer") {
            u = new RerunRecommenderUser(username);
        } else if (usertype == "gen") {
            u = new GenreRecommenderUser(username);
        }

        (sess.getUserMap())->insert(make_pair(username, u));


        complete();
        sess.setActionsLog(this);
    }
}

BaseAction* CreateUser :: clone(){return new CreateUser(*this);}

string CreateUser::toString() const
{
    if(this->getStatus()==ERROR)
        return "CreateUser ERROR: " + getErrorMsg();
    if(this->getStatus()==COMPLETED)
        return "CreateUser COMPLETED";
    return "";
}

//Change active user
void ChangeActiveUser::act(Session &sess)
{

    string username = sess.getCurrInp();
     if(sess.getUserMap()->find(username) != sess.getUserMap()->end()) {
         User *u = sess.getUserMap()->find(username)->second;
         sess.setActiveUser(*u);
         complete();
         sess.setActionsLog(this);
     }
     else
     {
         cout<<"Error - ";
         this->error("User is not registered");
         sess.setActionsLog(this);
         cout << getErrorMsg() << endl;
     }


}

BaseAction* ChangeActiveUser :: clone(){return new ChangeActiveUser(*this);}


string ChangeActiveUser::toString() const
{
    if(this->getStatus()==ERROR)
        return "ChangeActiveUser ERROR: " + getErrorMsg();
    if(this->getStatus()==COMPLETED)
        return "ChangeActiveUser COMPLETED";
    return "";
}

//Delete user
void DeleteUser::act(Session &sess)
{
    int pos = sess.getCurrInp().find(' ');
    string username = sess.getCurrInp().substr(0,pos);

    if(sess.getUserMap()->find(username)  == sess.getUserMap()->end() )
    {
        cout<<"Error - ";
        this->error("User does not exists");
        sess.setActionsLog(this);
        cout << getErrorMsg() << endl;
    }
    else
    {
        User* tmp = sess.getUserMap()->find(username)->second;
        sess.getUserMap()->erase(sess.getUserMap()->find(username));
        delete tmp;
        complete();
        sess.setActionsLog(this);
    }


}

BaseAction* DeleteUser :: clone(){return new DeleteUser(*this);}


string DeleteUser::toString() const
{
    if(this->getStatus()==ERROR)
        return "DeleteUser ERROR: " + getErrorMsg();
    if(this->getStatus()==COMPLETED)
        return "DeleteUser COMPLETED";
    return "";
}

//duplicate user
void DuplicateUser::act(Session &sess) {

    int pos = sess.getCurrInp().find(' ');
    string original = sess.getCurrInp().substr(0,pos);
    string username = sess.getCurrInp().substr(pos+1);
    User *u;
    if(sess.getUserMap()->find(original)  == sess.getUserMap()->end() )
    {
        cout<<"Error - ";
        this->error("original user is not exist");
        sess.setActionsLog(this);
        cout << getErrorMsg() << endl;
    }
    else if(sess.getUserMap()->find(username)  != sess.getUserMap()->end() )
    {
        cout<<"Error - ";
        this->error("the new user name is already taken");
        sess.setActionsLog(this);
        cout << getErrorMsg() << endl;
    }
    else{
       u = sess.getUserMap()->at(original)->duplicate(username);
        (sess.getUserMap())->insert(make_pair(username, u));
        complete();
        sess.setActionsLog(this);
    }
}

BaseAction* DuplicateUser :: clone(){return new DuplicateUser(*this);}


string DuplicateUser::toString() const {
    if(this->getStatus()==ERROR)
        return "DuplicateUser ERROR: " + getErrorMsg();
    if(this->getStatus()==COMPLETED)
        return "DuplicateUser COMPLETED";
    return "";
}

//print content list
void PrintContentList::act(Session &sess)
{
    for(int i = 0 ; i < (int)sess.getContent().size() ; i++ )
    {
        Watchable* w = sess.getContent()[i];
        cout << i+1 ;
        cout<< ". ";
        cout << w->toString() << endl;
    }
    complete();
    sess.setActionsLog(this);
}

BaseAction* PrintContentList :: clone(){return new PrintContentList(*this);}


string PrintContentList::toString() const
{
    if(this->getStatus()==ERROR)
        return "PrintContentList ERROR: " + getErrorMsg();
    if(this->getStatus()==COMPLETED)
        return "PrintContentList COMPLETED";
    return "";

}

//print watch history

void PrintWatchHistory::act(Session &sess)
{
    cout<<"Watch history for " + sess.getActiveUser().getName()<<endl;
    for(int i = 0 ; i < (int)sess.getActiveUser().get_history().size() ; i++ )
    {
        Watchable* w = sess.getActiveUser().get_history()[i];
        std::string s= w->Watching();
        cout <<to_string(i+1) +". " + s << endl;
    }
    complete();
    sess.setActionsLog(this);
}

BaseAction* PrintWatchHistory :: clone(){return new PrintWatchHistory(*this);}


string PrintWatchHistory::toString() const
{
    if(this->getStatus()==ERROR)
        return "PrintWatchHistory ERROR: " + getErrorMsg();
    if(this->getStatus()==COMPLETED)
        return "PrintWatchHistory COMPLETED";
    return "";
}

//watch

void Watch::act(Session &sess)
{
    string toWatch = sess.getCurrInp();
    int index = std::stoi(toWatch);
    if(index>(int)sess.getContent().size() || index<=0)
    {
        cout<<"Error - ";
        this->error("Watchable does not exists");
        sess.setActionsLog(this);
        cout << getErrorMsg() << endl;

    }

    else {
        sess.getActiveUser().set_history(sess.getContent()[index-1]->clone());
        cout << "Watching ";
        cout << sess.getContent()[index-1]->Watching() << endl;
        if (sess.getContent()[index-1]->getNextWatchable(sess) != nullptr) {
            cout << "We recommend watching ";
            cout << sess.getContent()[index-1]->getNextWatchable(sess)->Watching();
            cout << " Continue Watching[y/n]" << endl;
            sess.setCurrInp(to_string(sess.getContent()[index-1]->getNextWatchable(sess)->getID()));

            complete();
            sess.setActionsLog(this);

        }
    }
}


BaseAction* Watch :: clone(){return new Watch(*this);}


string Watch::toString() const
{
    if(this->getStatus()==ERROR)
        return "Watch ERROR: " + getErrorMsg();
    if(this->getStatus()==COMPLETED)
        return "Watch COMPLETED";
    return "";
}

//print actions log
void PrintActionsLog::act(Session &sess)
{
    for(int i = (int)sess.getActionsLog().size()-1 ; i>=0 ; i-- )
    {
        BaseAction *bs = sess.getActionsLog()[i];

       cout<< bs->toString() << endl;
    }

    complete();
    sess.setActionsLog(this);

}

BaseAction* PrintActionsLog :: clone(){return new PrintActionsLog(*this);}


string PrintActionsLog::toString() const
{
    if(this->getStatus()==ERROR)
        return "PrintActionsLog ERROR: " + getErrorMsg();
    if(this->getStatus()==COMPLETED)
        return "PrintActionsLog COMPLETED";
    return "";
}

//exit
void Exit::act(Session &sess) {
    complete();
    sess.setActionsLog(this);
}

BaseAction* Exit :: clone(){return new Exit(*this);}


string Exit::toString() const
{
    if(this->getStatus()==ERROR)
        return "Exit ERROR: " + getErrorMsg();
    if(this->getStatus()==COMPLETED)
        return "Exit COMPLETED";
    return "";
}

