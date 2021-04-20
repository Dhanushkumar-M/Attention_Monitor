package com.example.attentionmonitor;

public class DataStore {
    private String Attention_Level;
    private String Meditation_Level;
    private String id;
    private String name;

    public DataStore() {
    }

    public DataStore(String id, String name, String Attention_Level, String Meditation_Level) {
        this.id = id;
        this.name = name;
        this.Attention_Level = Attention_Level;
        this.Meditation_Level = Meditation_Level;
    }


    public String getName() {
        return name;
    }

    public String getAttention_Level() {
        return Attention_Level;
    }

    public String getMeditation_Level() {
        return Meditation_Level;
    }


}
