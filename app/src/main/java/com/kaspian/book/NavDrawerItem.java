package com.kaspian.book;

public class NavDrawerItem {

    private String title;
    private int icon;



    public NavDrawerItem(String title, int icon){
        this.title = title;
        this.icon = icon;
    }

    public NavDrawerItem(){

    }


    public String getTitle(){
        return this.title;
    }

    public int getIcon(){
        return this.icon;
    }


}