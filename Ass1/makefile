 # All Targets
all: splflix

# Tool invocations
splflix: bin/main.o bin/Session.o bin/Watchable.o bin/Action.o bin/User.o

	g++ -o bin/splflix bin/main.o bin/Session.o bin/Watchable.o bin/Action.o bin/User.o

# Depends on the source and header files
bin/main.o: src/main.cpp
	g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/main.o src/main.cpp

# Depends on the source and header files 
bin/Session.o: src/Session.cpp
	g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/Session.o src/Session.cpp
	
bin/Watchable.o: src/Watchable.cpp
	g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/Watchable.o src/Watchable.cpp
	
bin/User.o: src/User.cpp
	g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/User.o src/User.cpp

bin/Action.o: src/Action.cpp
	g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/Action.o src/Action.cpp

clean: 
	rm -f bin/*
