/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rest4hub.ifb.example;

import com.rest4hub.ibf.BannerPrinter;

/**
 *
 * @author Francisco Guimarães
 * @since 13/05/2016
 */
public class AppMavenResourceFiltering {

    public static void main(String[] args) {
        System.setProperty(BannerPrinter.BANNER_LOCATION_PROPERTY, "banner-maven.txt");
        BannerPrinter.print();
    }

}
