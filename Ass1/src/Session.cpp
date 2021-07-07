//
// Created by gil on 20/11/2019.
//
#include "../include/Session.h"
#include "../include/json.hpp"
#include "../include/User.h"
#include <string>
#include <fstream>
#include <utility>
using json = nlohmann::json;
using namespace std;
Session::~Session()
{
    for(const auto& user : userMap)//delete user map
        delete user.second;

    for(auto & j : actionsLog)
    {
        delete j;
    }

    for(auto & i : content) //delete content
        delete i;

    //delete defaultUser;

}
Session:: Session(const std::string &configFilePath) :content(),actionsLog(),userMap(),activeUser(),currInp()//constructor
{
    userMap.insert(make_pair("default", new LengthRecommenderUser("default")));
    activeUser = userMap.find("default")->second;
    const std::string file = configFilePath;
    std:: ifstream stream(configFilePath);
    nlohmann::json j;
    stream >> j;
    int id(1);
    //reading movies from json file
    for(int i=0; j["movies"][i]!= nullptr;i++){

        int length= j["movies"][i]["length"];
        string name = j["movies"][i]["name"];
        vector<std::string> tags;
        for(int k=0; j["movies"][i]["tags"][k]!=nullptr; k++){
            tags.push_back(j["movies"][i]["tags"][k]);
        }
        Watchable *movie = new Movie(id, name,length, tags);
        content.push_back(movie);
        id++;

    }
    //reading episodes from json file
    for(int i=0; j["tv_series"][i]!= nullptr;i++ ){
        string name = j["tv_series"][i]["name"];
        int length = j["tv_series"][i]["episode_length"];
        vector<std::string> tags;
        for(int k=0; j["tv_series"][i]["tags"][k]!=nullptr; k++) {
            tags.push_back(j["tv_series"][i]["tags"][k]);
        }
            int season(1);
            for(int c=0; j["tv_series"][i]["seasons"][c]!= nullptr; c++ ){
                int numOfEpisode = j["tv_series"][i]["seasons"][c];
                for(int d=1; d<= numOfEpisode;d++ ){
                    Watchable *epi = new Episode(id,name, length,season,d,tags );
                    if(j["tv_series"][i]["seasons"][c+1]== nullptr && d==numOfEpisode)
                        epi->setNextEpisodeId(0);
                    else
                        epi->setNextEpisodeId(id +1);

                    content.push_back(epi);

                    id++;
                }
                season = season+1;
            }
        }
    }


Session::Session(const Session &other): content(),actionsLog(),userMap(),activeUser(),currInp() {

    for(auto i : other.content){
       content.push_back(i->clone());
    }

    for(auto i : other.actionsLog){
        actionsLog.push_back(i->clone());
    }

        for (std::pair<std::string, User*> element : other.userMap)
        {
            userMap.insert(make_pair(element.first,element.second->duplicate(element.first)));
        }

        string activename = other.activeUser->getName();

        this->setActiveUser(*userMap.find(other.activeUser->getName())->second);



} //copy constructor

// Move Constructor
Session::Session(Session &&other) : content(move(other.getContent())),
                                    actionsLog(move(other.getActionsLog())),
                                    userMap(move(*other.getUserMap())),
                                    activeUser(other.activeUser),
                                    currInp(other.currInp)//move constructor
{
    other.userMap.clear();
    other.activeUser = nullptr;
    other.actionsLog.clear();
    other.content.clear();
    other.currInp= "";
}

Session& Session::operator=( Session &&other) //Move Assigment operator
{
    if(this!=&other) {
        for (auto &i : content) //delete older content
            delete i;
        content.clear();

        for (auto &j : actionsLog) //delete older actions
        {
            delete j;
        }
        actionsLog.clear();

        for (const auto &user : userMap)//delete older user map
        {
            delete user.second;
        }
        userMap.clear();
        content=move(other.getContent()),
                actionsLog = move(other.getActionsLog()),
                userMap=move(*other.getUserMap()),
                activeUser=(other.activeUser),
                currInp=(other.currInp);

            other.userMap.clear();
            other.activeUser = nullptr;
            other.actionsLog.clear();
            other.content.clear();
            other.currInp= "";

    }
    return *this;

}
Session& Session::operator=(const Session &other)
{

    if(this!=&other)
    {
        for(auto & i : content) //delete older content
            delete i;
        content.clear();

        for(auto i : other.content) //copy new content
            content.push_back(i->clone());

        for(auto & j : actionsLog) //delete older actions
        {
            delete j;
        }
        actionsLog.clear();

        for(auto i : other.actionsLog) //copy actions
            actionsLog.push_back(i->clone());

        string s = other.activeUser->getName();
        for(const auto& user : userMap)//delete older user map
        {
            delete user.second;
        }
        userMap.clear();

        for (std::pair<std::string, User*> element : other.userMap) //copy new user map
        {
            userMap.insert(make_pair(element.first,element.second->duplicate(element.first)));
        }
        this->activeUser = userMap.find(s)->second;

    }
    return *this;


}

vector<BaseAction*>  Session::getActionsLog() {
    return actionsLog;
}
void Session::setActionsLog(BaseAction *bs)
{
    this->actionsLog.push_back(bs);
}
void Session::setActiveUser(User &u)
{
    activeUser = &u;
}

User& Session::getActiveUser() {return *activeUser;}

std::unordered_map<std::string,User*>* Session::getUserMap()
{
    return &userMap;
}


void Session::start() {
    cout << "SPLFLIX is now on!" <<endl ;
    string input;
    getline(cin, input);
    int pos = input.find(' ');
    this->currInp = input.substr(pos+1);
    string action = input.substr(0,pos);
    BaseAction* action_;
    while(input!="exit") {
        if (action == "createuser") {
            action_ = new CreateUser();
            action_->act(*this);
        }

        if (action == "changeuser") {
            action_ = new ChangeActiveUser();
            action_->act(*this);
        }

        if (action == "deleteuser") {
            action_ = new DeleteUser();
            action_->act(*this);
        }
        if (action == "dupuser") {
            action_= new DuplicateUser();
            action_->act(*this);
        }
        if (action == "content") {
            action_ = new PrintContentList();
            action_->act(*this);
        }
        if (action == "watchhist") {
            action_ = new PrintWatchHistory();
            action_->act(*this);
        }
        if (action == "watch") {
            action_ = new Watch();
            action_->act(*this);
            int size = this->getActionsLog().size();
           if(this->getActionsLog()[size-1]->getStatus()!=ERROR) {
               string inp;
               do {
                   getline(cin, inp);
                   if (inp == "n")
                       break;
                   else {
                       if (inp == "y") {
                           action_ = new Watch();
                           action_->act(*this);
                       }
                   }

               } while (true);
           }
        }
        if (action == "log") {
            action_ = new PrintActionsLog();
            action_->act(*this);
        }

        getline (cin, input);
        pos = input.find(' ');
        this->currInp = input.substr(pos+1);
        action = input.substr(0,pos);
    }
    if (action == "exit") {
        action_ = new Exit();
        action_->act(*this);
    }
}



string Session::getCurrInp() {return this->currInp;}
void Session::setCurrInp(string s) {this->currInp = std::move(s);}
vector<Watchable*> Session::getContent() {return this->content;}




