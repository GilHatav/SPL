CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude
LDFLAGS:=-lboost_system 

all: StompBookClubClient
	g++ -pthread -o bin/StompBookClubClient bin/echoClient.o bin/BookClub.o bin/StompEncDec.o bin/connectionHandler.o bin/KeyboardThread.o bin/SocketThread.o  $(LDFLAGS) $(DEPS)

StompBookClubClient: bin/echoClient.o bin/BookClub.o bin/StompEncDec.o bin/connectionHandler.o bin/KeyboardThread.o bin/SocketThread.o


bin/echoClient.o: src/echoClient.cpp
	g++ -pthread $(CFLAGS) -o bin/echoClient.o src/echoClient.cpp
	
bin/connectionHandler.o: src/connectionHandler.cpp
	g++ -pthread $(CFLAGS) -o bin/connectionHandler.o src/connectionHandler.cpp

bin/BookClub.o: src/BookClub.cpp
	g++ -pthread $(CFLAGS) -o bin/BookClub.o src/BookClub.cpp

bin/KeyboardThread.o: src/KeyboardThread.cpp
	g++ -pthread $(CFLAGS) -o bin/KeyboardThread.o src/KeyboardThread.cpp

bin/SocketThread.o: src/SocketThread.cpp
	g++ -pthread $(CFLAGS) -o bin/SocketThread.o src/SocketThread.cpp

bin/StompEncDec.o: src/StompEncDec.cpp
	g++ -pthread $(CFLAGS) -o bin/StompEncDec.o src/StompEncDec.cpp




	
.PHONY: clean
clean:
	rm -f bin/*
