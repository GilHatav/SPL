import sqlite3



def main():

    con = sqlite3.connect('moncafe.db')
    cursor = con.cursor()


    cursor.execute("SELECT * FROM Activities ORDER BY Activities.date ASC")
    Result = cursor.fetchone()
    print("Activities")
    if(Result!=None):
        printalsoreports = True
    while(Result!=None):
        print(Result)
        Result=cursor.fetchone()

    print("Coffee stands")
    cursor.execute("SELECT * FROM Coffee_stands ORDER BY Coffee_stands.id ASC")
    Result = cursor.fetchone()
    while(Result!=None):
        print(Result)
        Result=cursor.fetchone()


    print("Employees")
    cursor.execute("SELECT * FROM Employees ORDER BY Employees.id ASC")
    Result = cursor.fetchone()
    while(Result!=None):
        print(Result)
        Result=cursor.fetchone()

    print("Products")
    cursor.execute("SELECT * FROM Products ORDER BY Products.id ASC")
    Result = cursor.fetchone()
    while(Result!=None):
        print(Result)
        Result=cursor.fetchone()


    print("Suppliers")
    cursor.execute("SELECT * FROM Suppliers ORDER BY Suppliers.id ASC")
    Result = cursor.fetchone()
    while(Result!=None):
        print(Result)
        Result=cursor.fetchone()
    print()



    print("Employees report")
    cursor.execute("SELECT Employees.name, Employees.salary, Coffee_stands.location, ifnull(sum((Activities.quantity)*Products.price*(-1)),0) AS 'total sales income' FROM Employees"
                   " INNER JOIN Coffee_stands ON Employees.coffee_stand = Coffee_stands.id LEFT JOIN Activities ON Activities.activator_id = Employees.id AND Activities.quantity < 0 "
                   "LEFT JOIN Products ON Products.id = Activities.product_id GROUP BY Employees.id ORDER BY Employees.name")
    Result = cursor.fetchone()
    while(Result!=None):
        print(Result[0],Result[1],Result[2],Result[3])
        Result=cursor.fetchone()
    print()


    cursor.execute("SELECT Activities.date, Products.description , Activities.quantity, Employees.name AS 'Employee Name' , Suppliers.name AS 'Supplier Name' "
                   "FROM Activities LEFT JOIN Employees ON Employees.id = Activities.activator_id LEFT JOIN Suppliers ON Suppliers.id = Activities.activator_id "
                   "LEFT JOIN Products ON Products.id = Activities.product_id ORDER BY Activities.date ASC")
    Result = cursor.fetchone()
    if(Result!=None):
        print("Activities")
    while(Result!=None):
        print(Result)
        Result=cursor.fetchone()

    cursor.close()

if __name__ == '__main__':
    main()
