//
// Created by gil on 08/01/2020.
//
#include "SocketThread.h"

SocketThread::SocketThread(ConnectionHandler& connectionHandler, BookClub& MyClub ,StompEncDec& stomp ,   bool &terminate, bool& connected):connectionHandler(connectionHandler), myClub(MyClub),stomp(stomp) , terminate(terminate) , connected(connected) {}
void SocketThread :: run() {

    while(!(terminate)){
        std::string answer;
        if (!connectionHandler.getLine(answer)) {
            std::cout << "Disconnected from socket ( gil). Exiting...\n" << std::endl;
            break;
        }
        std::cout<<answer<<std::endl;

        if (answer.find("status") != std::string::npos) {
            int indexDes = indexof(answer , "destination");
            string rest = answer.substr(indexDes);
            int indexGener = indexof(rest , "\n");
            string gener = rest.substr(12 , indexGener-12);
            vector<string> books = myClub.getBooks(gener);
            string myName = myClub.getName();
            string ans =  stomp.statusAct(gener , myName , books);
            connectionHandler.sendLine(ans);

        }

        else if (answer.find("wish") != std::string::npos) {
            int indexDes = indexof(answer , "destination");
            string rest = answer.substr(indexDes);
            int indexGener = indexof(rest , "\n");
            string gener = rest.substr(12 , indexGener - 12); //gener name
            int indexBorrow = indexof(answer , "borrow"); //here
            string restbook= answer.substr(indexBorrow);
            int indexBook = indexof(restbook , "\n");
            string book  = restbook.substr(7 , indexBook-7); //bookname
            string name = myClub.getName();
            string ans;
            vector<string> Books = myClub.getBooks(gener);
            for(int i=0; i<(int)Books.size();i++){
                if(Books[i]==book){
                    ans =  stomp.HasBook(name , gener , book);
                    break;
                }
            }
            connectionHandler.sendLine(ans);
        }


        else if (answer.find("MESSAGE") != std::string::npos && answer.find("has") != std::string::npos) {
            int indexDes = indexof(answer , "destination");
            string rest = answer.substr(indexDes);
            int indexGener = indexof(rest , "\n");
            string gener = rest.substr(12 , indexGener - 12); //gener name
            string middle  =rest.substr(indexGener);
            int mid =indexof(middle , "\n")+2;
            int indexx = indexof(middle , "has");
            string Borrowername = middle.substr(mid , indexx -3);
            int indexHas = indexof(answer , "has");
            string restHas= answer.substr(indexHas);
            int indexBook = indexof(restHas , "\n");
            string book  = restHas.substr(4 , indexBook-4);
            if(myClub.ifWish(book)){
                myClub.addBorrowedBook(gener , book , Borrowername);
                string ans =  stomp.borrowAct(gener , book , Borrowername);
                connectionHandler.sendLine(ans);
            }
        }

        else if (answer.find("Taking") != std::string::npos) {
            int indexDes = indexof(answer , "destination");
            string rest = answer.substr(indexDes);
            int indexGener = indexof(rest , "\n");
            string gener = rest.substr(12 , indexGener - 12); //gener name
            int indexTaking = indexof(answer , "Taking");
            string second = answer.substr(indexTaking);
            int indexFrom = indexof(second, "from");
            string book = second.substr(7 , indexFrom-8);
            string namex = second.substr(indexFrom+5);
            string name = namex.substr(0,namex.length()-1);
            if(myClub.getName()==name){
                myClub.removeBook(gener , book);
            }
        }

        else if (answer.find("MESSAGE") != std::string::npos && answer.find("Returning") != std::string::npos) {

            int indexDes = indexof(answer , "destination");
            string rest = answer.substr(indexDes);
            int indexGener = indexof(rest , "\n");
            string gener = rest.substr(12 , indexGener - 12); //gener name
            int indexTo = indexof(answer , "to ");
            int indexReturning = indexof(answer , "Returning");
            string book = answer.substr(indexReturning+10 ,indexTo-1 -(indexReturning+10) );
            string restSpace = answer.substr(indexTo);
            int indexSpace = indexof(restSpace, "\n");
            string name  = restSpace.substr(3,indexSpace - 3);
            if(myClub.getName()==name){
                myClub.addBook(gener ,book);
            }
        }


        else if (answer.find("CONNECTED") != std::string::npos) {

            std::cout<<"Login successful"<<endl;
            connected = true;

        }

        else if (answer.find("join successful") != std::string::npos) {


            int index_1 = indexof(answer, "successful ");
            string rest = answer.substr(index_1+11);
            int indexSpace = indexof(rest,"\n");
            string gener = rest.substr(0 ,indexSpace);

            std::cout<<"joined club " + gener <<endl;
        }

        else if (answer.find("exit successful") != std::string::npos) {

            int index_1 = indexof(answer, "successful ");
            string rest = answer.substr(index_1+11);
            int indexSpace = indexof(rest,"\n");
            string gener = rest.substr(0 ,indexSpace);

            std::cout<<"Exited club " + gener <<endl;

        }

        else if (answer.find("Disconnect") != std::string::npos) {
            connected = false;
            terminate = true;
            std::cout<<"Enter bye"<<endl;

        }

    }

}

int SocketThread::  indexof(std::string& text, std::string pattern) {
    std::string::size_type loc = text.find(pattern, 0);
    if (loc != std::string::npos) {
        return loc;
    } else {
        return -1;
    }
}
