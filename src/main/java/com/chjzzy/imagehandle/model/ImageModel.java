package com.chjzzy.imagehandle.model;

public class ImageModel {
    private String name;
    private String bytecode;
    private int[]statistic;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBytecode() {
        return bytecode;
    }

    public void setBytecode(String bytecode) {
        this.bytecode = bytecode;
    }

    public int[] getStatistic() {
        return statistic;
    }

    public void setStatistic(int[] statistic) {
        this.statistic = statistic;
    }
}
