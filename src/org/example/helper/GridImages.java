package org.example.helper;

public class GridImages {
	private String name;
    private int state;

    public GridImages(String name, int state) {
        super();
        this.name = name;
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }   
}