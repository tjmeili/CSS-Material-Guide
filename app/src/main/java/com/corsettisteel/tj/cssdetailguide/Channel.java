package com.corsettisteel.tj.cssdetailguide;

/**
 * Created by TJ on 2/10/2018.
 */

public class Channel extends Component {

    private String depth, width, web, flange;


    public Channel() {
        setShape("");
        depth = "";
        width = "";
        web = "";
        flange = "";
    }

    public String getDepth() {
        return depth;
    }

    public void setDepth(String depth) {
        this.depth = depth;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public String getFlange() {
        return flange;
    }

    public void setFlange(String flange) {
        this.flange = flange;
    }

}
