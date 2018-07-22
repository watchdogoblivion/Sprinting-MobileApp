package com.example.sj_sc.sprint;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

class Settings {

    private int settingsID;
    private float startingR;
    private float stoppingR;
    private int delayM;
    private int delayS;
    private int delayMS;

    public final static List<Settings> SETTINGS_ARRAY_LIST = new CopyOnWriteArrayList<>();

    Settings(float startingR, float stoppingR, int delayM, int delayS, int delayMS){
        this.startingR = startingR;
        this.stoppingR = stoppingR;
        this.delayM = delayM;
        this.delayS = delayS;
        this.delayMS = delayMS;
    }


    public void setStartingR(float startingR) {
        this.startingR = startingR;
    }


    public void setStoppingR(float stoppingR) {
        this.stoppingR = stoppingR;
    }


    public void setDelayM(int delayM) {
        this.delayM = delayM;
    }


    public void setDelayS(int delayS) {
        this.delayS = delayS;
    }


    public void setDelayMS(int delayMS) {
        this.delayMS = delayMS;
    }

    public int getSettingsID() {
        return settingsID;
    }

    public void setSettingsID(int settingsID) {
        this.settingsID = settingsID;
    }
}
