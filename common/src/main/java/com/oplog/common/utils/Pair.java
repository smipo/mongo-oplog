package com.oplog.common.utils;

public class Pair<F, S> {

    private F f;
    private S s;

    private Pair(){

    }
    private Pair(F f,S s){
        this.f = f;
        this.s = s;
    }

    public static <F,S> Pair<F,S> of(F f,S s){
        return new Pair<>(f,s);
    }

    public F f() {
        return f;
    }



    public S s() {
        return s;
    }


}
