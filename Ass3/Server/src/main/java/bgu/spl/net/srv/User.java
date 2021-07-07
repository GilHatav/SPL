package bgu.spl.net.srv;

public class User {
    private String username;
    private String password;
    private int id;
    private boolean active;

    public User(String name, String password,boolean isActive, int id)
    {
        this.username = name;
        this.password = password;
        this.active = isActive;
        this.id = id;
    }

    public String getPassword() {
        return password;
    }


    public int getId() {
        return id;
    }

    public boolean getActive()
    {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
