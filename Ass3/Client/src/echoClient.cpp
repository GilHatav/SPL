
#include <stdlib.h>
#include <connectionHandler.h>
#include <SocketThread.h>
#include <KeyboardThread.h>
#include <thread>




/**
* This code assumes that the server replies the exact text the client sent it (as opposed to the practical session example)
*/
int main (int argc, char *argv[]) {

    StompEncDec stmp; // protocol

    bool alreadyConnected = false;

    const short bufsize = 1024;
    char buf[bufsize];
    std::cin.getline(buf, bufsize);
    std::string line(buf);
    vector<string> SplittedCommand = stmp.splitString(line);

    ConnectionHandler* connectionHandler;
    BookClub* myClub = new BookClub();
    while(!alreadyConnected) {
        if (SplittedCommand[0] == "login") {
            string hostPort = SplittedCommand[1];
            int index = stmp.indexof(hostPort, ":");
            string host = hostPort.substr(0, index);
            string port = hostPort.substr(index + 1);
            string name = SplittedCommand[2];
            string password = SplittedCommand[3];
            if (!alreadyConnected) {

                connectionHandler = new ConnectionHandler(host, stoi(port));
                if (!connectionHandler->connect()) {
                    std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
                }
                alreadyConnected = true;
            }

            myClub->setName(name);
            string toServer = stmp.login(name, password);
            if (!connectionHandler->sendLine(toServer)) {
                std::cout << "Disconnected. Exiting...\n" << std::endl;
            }

        }
    }

    bool* connected = new bool (false);
    bool* terminate = new bool (false);
    KeyboardThread *keyBoard=new KeyboardThread(*connectionHandler, *myClub ,stmp, *terminate, *connected);
    SocketThread *socket=new SocketThread(*connectionHandler, *myClub ,stmp, *terminate, *connected);
    std::thread T1(&KeyboardThread::run,keyBoard);
    std::thread T2(&SocketThread::run,socket);

    T1.join();
    T2.join();
    connectionHandler->close();
    delete connectionHandler;
    delete myClub;
    delete terminate;
    delete keyBoard;
    delete socket;
    delete connected;

    return 0;

}
