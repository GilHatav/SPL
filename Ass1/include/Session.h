#ifndef SESSION_H_
#define SESSION_H_

#include <vector>
#include <unordered_map>
#include <string>
#include "Action.h"
using namespace std;
class User;
class Watchable;

class Session{
public:
    Session(const std::string &configFilePath);
    Session(const Session& other);
    Session(Session&& other); //move constructor
    Session& operator= (const Session& other);
    Session& operator= (Session&& other);
    ~Session();
    void start();
    string getCurrInp();
    User& getActiveUser();
    vector<Watchable*> getContent();
    std::unordered_map<std::string,User*>* getUserMap();
    void setActiveUser(User &u);
    void setCurrInp(string s);
    vector<BaseAction*> getActionsLog();
    void setActionsLog(BaseAction *bs);

private:
    std::vector<Watchable*> content;
    std::vector<BaseAction*> actionsLog;
    std::unordered_map<std::string,User*> userMap;
    User* activeUser;
    string currInp;
    //User* defaultUser;


};
#endif