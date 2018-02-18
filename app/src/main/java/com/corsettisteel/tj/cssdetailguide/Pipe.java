package com.corsettisteel.tj.cssdetailguide;

/**
 * Created by TJ on 2/10/2018.
 */

public class Pipe extends Component{

    private String od, thickness, wpf;

    public Pipe(){
        setShape("");
        od = "";
        thickness = "";
        wpf = "";
    }


    public String getOd() {
        return od;
    }

    public void setOd(String od) {
        this.od = od;
    }

    public String getThickness() {
        return thickness;
    }

    public void setThickness(String thickness) {
        this.thickness = thickness;
    }

    public String getWpf() {
        return wpf;
    }

    public void setWpf(String wpf) {
        this.wpf = wpf;
    }
}
