import sqlite3
import printdb
import sys


def main(args):
    con = sqlite3.connect('moncafe.db')
    cursor = con.cursor()

    with open(args[1]) as inputfile:
        for line in inputfile:
            list = line.split(', ')
            str1 = list[3]
            list1 = str1.split('\n')
            cursor.execute("INSERT INTO Activities VALUES(?,?,?,?)", (list[0], list[1], list[2], list1[0]))
            con.commit()
            cursor.execute("SELECT quantity as q from Products WHERE Products.id =" + list[0])
            q = cursor.fetchone()[0]
            if(((q<int(list[1])*(-1)) & (int(list[1])<0))):
                x=0
            else:
                cursor.execute("UPDATE Products SET quantity = quantity +" + list[1] + " WHERE Products.id =" + list[0])
                con.commit()


    cursor.close()
    con.close()

    con = sqlite3.connect('moncafe.db')
    cursor = con.cursor()
    con.commit()

    printdb.main()

if __name__ == '__main__':
    main(sys.argv)
