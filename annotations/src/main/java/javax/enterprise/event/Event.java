package javax.enterprise.event;

public interface Event<T> {

    void fire(T var1);
}
