#ifndef WATCHABLE_H_
#define WATCHABLE_H_

#include <string>
#include <vector>


class Session;

class Watchable{
public:
    Watchable(long id, int length, const std::vector<std::string>& tags);
    virtual ~Watchable();
    Watchable(const Watchable& other);
    virtual std::string toString() const = 0;
    virtual Watchable* getNextWatchable(Session&) const = 0;
    int getLength() const;
    long getID();
    virtual void setNextEpisodeId(int nextID)=0;
    virtual long getNextEpisodeId() const =0;
    std::string getTagsAsString() const;
    virtual Watchable* clone()=0;
    std::vector<std::string> getTags();
    virtual std::string Watching() const = 0;
private:
    const long id;
    int length;
    std::vector<std::string> tags;
};

class Movie : public Watchable{
public:
    Movie(long id, const std::string& name, int length, const std::vector<std::string>& tags);
    Movie(const Movie& other);
    virtual std::string toString() const;
    virtual Watchable* getNextWatchable(Session&) const;
    virtual long getNextEpisodeId() const;
    //not needed just for overriding:
    void setNextEpisodeId(int nextID);
    Watchable* clone();
    std::string getName() const;
    virtual std::string Watching() const ;
private:
    std::string name;
};


class Episode: public Watchable{
public:
    Episode(long id, const std::string& seriesName,int length, int season, int episode ,const std::vector<std::string>& tags);
    virtual std::string toString() const;
    //Episode(const Episode& other);
    virtual Watchable* getNextWatchable(Session&) const;
    virtual long getNextEpisodeId() const;
    void setNextEpisodeId(int nextID);
    std::string seasonAsString() const;
    std::string episodeAsString() const;
    Watchable* clone();
    virtual std::string Watching() const;
private:
    std::string seriesName;
    int season;
    int episode;
    long nextEpisodeId;
};

#endif