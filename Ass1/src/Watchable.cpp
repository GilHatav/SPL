
#include <string>
using namespace std;
#include <vector>
#include "../include/Watchable.h"
#include "../include/Session.h"
#include "../include/User.h"
Watchable::Watchable(long id, int length, const std::vector<std::string> &tags) : id(id) ,
                                                                                  length(length) , tags(tags)  {}
long Watchable::getID() { return id;}
Watchable::Watchable(const Watchable &other) : id(other.id), length(other.length),tags(other.tags) {}

int  Watchable::getLength() const {return length;}
std::string Watchable::getTagsAsString() const {
    string s = "";
    for(int i=0; i< (int)tags.size()-1; i++){
        s = s +tags[i] + ", ";
    }
    s = s +tags[tags.size()-1];
    return s;
}
Watchable::~Watchable() = default;
vector<std::string> Watchable::getTags() { return  tags;}

//Movie
Movie::Movie(long id, const std::string &name, int length, const std::vector<std::string> &tags) :
        Watchable(id,length,tags) , name(name){}
Movie::Movie(const Movie& other):Watchable(other), name(other.getName()){}
std::string Movie::getName() const { return  name;}
Watchable* Movie::getNextWatchable(Session &sess) const
{
    User &u = sess.getActiveUser();
    //int size = u.get_history().size();
    //Watchable* w = u.get_history()[size-1];
    Watchable *rec = u.getRecommendation(sess);
    return rec;
}

std::string Movie ::Watching() const
{
    return name;
}

std ::string Movie :: toString() const
{
    string s =  name + " " + std::to_string(getLength()) + " minutes" + "[" + getTagsAsString() + "]" ;
    return s;
}
//not needed just for overriding:
void Movie::setNextEpisodeId(int nextID){}

//Episode
Episode::Episode(long id1, const std::string &seriesName1, int length1, int season1, int episode1,
                 const std::vector<std::string> &tags1) : Watchable(id1,length1,tags1) ,
                                                          seriesName(seriesName1) , season(season1) , episode(episode1), nextEpisodeId() {this->nextEpisodeId=id1+1;};

Watchable* Episode::getNextWatchable(Session &sess) const
{

    User &u = sess.getActiveUser();
    vector<Watchable*> userHist =  u.get_history();
    int size = userHist.size();
    long nextEpId=0;
    if(!userHist.empty()) {
        nextEpId = userHist[size-1]->getNextEpisodeId();
        if( nextEpId != 0 )
            return sess.getContent()[nextEpId-1];
    }

    return sess.getActiveUser().getRecommendation(sess);
}

//Episode::Episode(const Episode& other):Watchable(other),nextEpisodeId(other.getNextEpisodeId()),season(other.season),episode(other.episode),seriesName(other.seriesName) {}

std::string Episode ::seasonAsString() const {

    string s;
    if(season<10)
        return   s= "0" + to_string(season);
    s = to_string(season);
    return s;
}

std::string Episode ::episodeAsString() const
{
    string s;
    if(episode<10)
        return   s= "0" + to_string(episode);
    s = to_string(episode);
    return s;
}


std::string Episode :: toString() const
{
    string s = seriesName + " " + "S" + seasonAsString() +  "E" +  episodeAsString() + " " + std::to_string(getLength())
            + " minutes" + "[" + getTagsAsString() + "]"  ;
    return s;
}

void Episode::setNextEpisodeId(int nextID)
{
    nextEpisodeId = nextID;
}

long Episode::getNextEpisodeId() const {
    return this->nextEpisodeId;
}

long Movie::getNextEpisodeId() const
{return 0;}

Watchable* Movie:: clone(){return new Movie(this->getID(),this->getName(),this->getLength(),this->getTags());}
Watchable* Episode:: clone()
{
    Watchable *w = new Episode(this->getID(),this->seriesName,this->getLength(),this->season,this->episode,this->getTags());
    w->setNextEpisodeId(this->getNextEpisodeId());
    return w;
}
std::string Episode ::Watching() const
{
    string s = seriesName + "" + "S" + seasonAsString() +  "E" +  episodeAsString();
    return s;
}