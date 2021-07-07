#ifndef USER_H_
#define USER_H_

#include <vector>
#include <string>
#include <unordered_set>
#include <unordered_map>
class Watchable;
class Session;

class User{
public:
    virtual ~User();
    User(std::string  name);
    User(const User& other, const std::string& name);
    virtual Watchable* getRecommendation(Session& s) = 0;
    std::vector<Watchable*> get_history() const;
    void set_history(Watchable* w);
    virtual User* duplicate (std::string name)const = 0;
    bool existInHistory(int i);
    virtual User* clone()=0;
    std::string getName();


protected:
    std::vector<Watchable*> history;
private:
    const std::string name;

};

class LengthRecommenderUser : public User {
public:
    LengthRecommenderUser(const std::string& name);
    virtual Watchable* getRecommendation(Session& s);
    LengthRecommenderUser(const User& other, const std::string& name); //copy constructor
    User* duplicate (std::string name )const;
    User* clone();
private:
};

class RerunRecommenderUser : public User {
public:
    RerunRecommenderUser(const std::string& name);
    RerunRecommenderUser(const User& other, std::string name); //copy constructor
    virtual Watchable* getRecommendation(Session& s);
    User* duplicate (std::string name )const;
     User* clone();
private:
    int nextRecommendation;
};

class GenreRecommenderUser : public User {
public:
    GenreRecommenderUser(const std::string& name);
    GenreRecommenderUser(const User& other, const std::string& name); //copy constructor
    virtual Watchable* getRecommendation(Session& s);
    User* duplicate (std::string name)const;
    User* clone();
private:
};

#endif