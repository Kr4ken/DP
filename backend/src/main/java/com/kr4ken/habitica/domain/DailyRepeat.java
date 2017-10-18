package com.kr4ken.habitica.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// Периодичность по дням недели
@JsonIgnoreProperties(ignoreUnknown = true)
public class DailyRepeat {
    private Boolean su;
    private Boolean s;
    private Boolean f;
    private Boolean th;
    private Boolean w;
    private Boolean t;
    private Boolean m;

    public DailyRepeat() {
        su = true;
        s = false;
        f = false;
        th = true;
        w = true;
        t = true;
        m = true;
    }

    public Boolean getSu() {
        return su;
    }

    public void setSu(Boolean su) {
        this.su = su;
    }

    public Boolean getS() {
        return s;
    }

    public void setS(Boolean s) {
        this.s = s;
    }

    public Boolean getF() {
        return f;
    }

    public void setF(Boolean f) {
        this.f = f;
    }

    public Boolean getTh() {
        return th;
    }

    public void setTh(Boolean th) {
        this.th = th;
    }

    public Boolean getW() {
        return w;
    }

    public void setW(Boolean w) {
        this.w = w;
    }

    public Boolean getT() {
        return t;
    }

    public void setT(Boolean t) {
        this.t = t;
    }

    public Boolean getM() {
        return m;
    }

    public void setM(Boolean m) {
        this.m = m;
    }
}
