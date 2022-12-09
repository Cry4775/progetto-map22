package utility;

/** Allows to wrap an object. */
public class Wrapper<T> {
    T obj;

    public Wrapper(T obj) {
        this.obj = obj;
    }

    public T getObj() {
        return obj;
    }

    public void setObj(T obj) {
        this.obj = obj;
    }
}
