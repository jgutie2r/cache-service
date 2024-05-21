package edu.uv.cs.cache.common;

public class KeyNotFoundException extends Exception{
    private static final long serialVersionUID = -3946586088628150615L;
    private String key;
    public KeyNotFoundException(String key){
        super("Key "+ key + " not found");
        this.key = key;
    }

    public String getKey(){
        return key;
    }
}