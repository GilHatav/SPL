package bgu.spl.net.srv;

public class Pair<S, U> {
    private Object x1;
    private Object x2;

    public Pair(Object x1 , Object X2)
    {
        this.x1 = x1;
        this.x2 = X2;
    }

    public Object getKey() {
        return x1;
    }

    public Object getValue()
    {
        return this.x2;
    }
}
