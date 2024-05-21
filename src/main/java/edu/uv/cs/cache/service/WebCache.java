package edu.uv.cs.cache.service;

import java.util.List;

import edu.uv.cs.cache.common.*;

public interface WebCache{
    public List<Pair> findAll();
    public Pair find(String key) throws KeyNotFoundException;
    public Pair add(Pair p);
    //public void delete(String key) throws KeyNotFoundException;
}