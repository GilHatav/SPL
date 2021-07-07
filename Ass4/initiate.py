import sqlite3
import os.path
from os import path
import sys


def insert_to_c(con, id, location, emp):
    cursor = con.cursor()
    cursor.execute("INSERT INTO Coffee_stands VALUES(?,?,?)", (id, location, emp,))
    con.commit()
    cursor.close()


def insert_to_e(con, id, name, salary, coffee_stand):
    cursor = con.cursor()
    cursor.execute("INSERT INTO Employees VALUES(?,?,?,?)", (id, name, salary, coffee_stand))
    con.commit()
    cursor.close()


def insert_to_p(con, id, description, price):
    cursor = con.cursor()
    cursor.execute("INSERT INTO Products VALUES(?,?,?,?)", (id, description, price, 0))
    con.commit()
    cursor.close()


def insert_to_s(con, id, name, contant_info):
    cursor = con.cursor()
    cursor.execute("INSERT INTO Suppliers VALUES(?,?,?)", (id, name, contant_info))
    con.commit()
    cursor.close()


def main(args):
    if path.exists("moncafe.db"):
        os.remove("moncafe.db")

    con = sqlite3.connect('moncafe.db')
    cursor = con.cursor()

    cursor.execute("CREATE TABLE Employees(id INTEGER PRIMARY KEY, name TEXT NOT NULL, salary REAL NOT NULL , "
                   "coffee_stand INTEGER REFERENCES Coffee_stand(id))")  # create table Employees

    cursor.execute("CREATE TABLE Suppliers(id INTEGER PRIMARY KEY, name TEXT NOT NULL, "
                   "contact_information TEXT )")  # create table Suppliers

    cursor.execute("CREATE TABLE Products(id INTEGER PRIMARY KEY, description TEXT NOT NULL, "
                   "price REAL NOT NULL, quantity INTEGER NOT NULL)")  # create table Products

    cursor.execute("CREATE TABLE Coffee_stands(id INTEGER PRIMARY KEY, location TEXT NOT NULL, "
                   "number_of_employees INTEGER)")  # create table Coffee_stands

    cursor.execute("CREATE TABLE Activities(product_id INTEGER INTEGER REFERENCES Products(id),quantity INTEGER NOT NULL, "
                   "activator_id INTEGER NOT NULL ,date DATE NOT NULL )")  # create table Activities
    cursor.close()


    with open(args[1]) as inputfile:
        for line in inputfile:
            if line[0] == 'C':
                list = line.split(', ')
                str1 = list[3]
                list1 = str1.split('\n')
                insert_to_c(con, list[1], list[2], list1[0])
            if line[0] == 'E':
                list = line.split(', ')
                str1 = list[4]
                list1 = str1.split('\n')
                insert_to_e(con, list[1], list[2], list[3], list1[0])
            if line[0] == 'P':
                list = line.split(', ')
                str1 = list[3]
                list1 = str1.split('\n')
                insert_to_p(con, list[1], list[2], list1[0])
            if line[0] == 'S':
                list = line.split(', ')
                str1 = list[3]
                list1 = str1.split('\n')
                str2 = list1[0]
                list2 = str2.split('\r')
                insert_to_s(con, list[1], list[2], list2[0])


if __name__ == '__main__':
    main(sys.argv)