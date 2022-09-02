package ru.sbrf.efx.zeromqtest.util;

public class Result<T, E extends Throwable> {

    private final T val;

    private final E err;

    public Result(T val, E err) {
        this.val = val;
        this.err = err;
    }

    public T getVal() {
        return val;
    }

    public E getErr() {
        return err;
    }

    public boolean ok(){
        return val != null && err == null;
    }

    public E err(){
        return err;
    }

}
