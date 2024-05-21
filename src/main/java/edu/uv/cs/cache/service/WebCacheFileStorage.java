package edu.uv.cs.cache.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import edu.uv.cs.cache.common.KeyNotFoundException;
import edu.uv.cs.cache.common.Pair;

public class WebCacheFileStorage implements WebCache {
    private ConcurrentHashMap<String, String> data;

    // File to store the data (in order to persist)
    private String dataStore = "store.txt";
    private ExecutorService ex;

    public WebCacheFileStorage() {
        // Read data from file if present
        data = new ConcurrentHashMap<String, String>();
        File f = new File(dataStore);
        if (f.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(dataStore));
                String linea;
                while ((linea = br.readLine()) != null) {
                    String[] d = linea.split(";");
                    data.put(d[0], d[1]);
                }
                br.close();
            } catch (Exception ex) {
            }
        }
        ex = Executors.newSingleThreadExecutor();
    }

    /**
     * Method to save data asynchronously in a file
     */
    private void asyncUpdateStore() {
        // Execute update of the file asynchronously in a different thread
        ex.execute(new Runnable() {
            public void run() {
                try {
                    PrintWriter pw = new PrintWriter(new FileWriter(dataStore));
                    for (String k : data.keySet())
                        pw.println(k + ";" + data.get(k));
                    pw.flush();
                    pw.close();
                } catch (Exception ex) {
                    System.out.println("No file found with pairs to initialize");
                }
            }
        });

    }

    @Override
    public List<Pair> findAll() {
        List<Pair> pares = new ArrayList<Pair>();
        data.forEach((k, v) -> pares.add(new Pair(k, v)));
        System.out.println("get()=" + data);
        return pares;
    }

    @Override
    public Pair find(String key) throws KeyNotFoundException {
        if (!data.containsKey(key))
            throw new KeyNotFoundException(key);
        Pair p = new Pair(key, data.get(key));
        System.out.println("get(" + key + ")=" + p);
        return p;
    }

    @Override
    public Pair add(Pair p) {
        // Add pair to memory
        data.put(p.getKey(), p.getValue());
        // Update file asynchronously
        asyncUpdateStore();
        System.out.println("put(" + p + ")");
        return p;
    }

    /* 
     *     @Override
    public void delete(String key) throws KeyNotFoundException {
        if (!data.containsKey(key))
            throw new KeyNotFoundException(key);
        data.remove(key);
        // Update file asynchronously
        asyncUpdateStore();
        System.out.println("delete(" + key + ")");
    }
    */
}