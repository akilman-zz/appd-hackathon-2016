package com.googlesource.gerrit.plugins.cookbook.karma;

public class KarmaInfo {

    public int value = 42;
    public String message;

    public KarmaInfo() {
        this.value = 42;
    }

    public KarmaInfo(int value) {
        this.value = value;
    }

    public KarmaInfo(int value, String message) {
        this.value = value;
        this.message = message;
    }
}
