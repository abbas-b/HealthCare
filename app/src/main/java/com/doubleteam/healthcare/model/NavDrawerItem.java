package com.doubleteam.healthcare.model;

/**
 * Created by abbas on 11/16/2015.
 */
public class NavDrawerItem {

        private String title;
        private int icon;


        public NavDrawerItem() {

        }

        public NavDrawerItem(String title, String Icon) {
            this.title = title;
            this.icon = icon;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getIcon() {
            return icon;
        }

        public void setIcon(int icon) {
            this.icon = icon;
        }

}
