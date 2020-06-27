package com.chjzzy.imagehandle.model;

public class ImageModel {
    private String name;
    private byte[] bytecode;
    private int[]statistic;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getBytecode() {
        return bytecode;
    }

    public void setBytecode(byte[] bytecode) {
        this.bytecode = bytecode;
    }

    public int[] getStatistic() {
        return statistic;
    }

    public void setStatistic(int[] statistic) {
        this.statistic = statistic;
    }
}
