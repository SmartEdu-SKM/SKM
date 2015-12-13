package com.example.dell.smartedu;
/*
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
*/

import com.parse.ParseUser;

/**
 * Created by Dell on 9/2/2015.
 */
public class User {
    private static String objId;
    private static String email;
    private static double latitude;
    private static double longitude;
    private static String username;
    private static String xeroxCategory;
    private static int numOfPages;
    private static int numOfCopies;
    private static int packageSize;
    private static String pageRange;
   // private static ParseGeoPoint geoPoint;
   // private static ParseFile printFile;
    private static String orderType;
    private static String type;

    public static String getObjId(){
        return objId;
    }

    public static String getUsername() {

        username= ParseUser.getCurrentUser().getUsername();


        return username; }

    public static String getEmail(){

        if( ParseUser.getCurrentUser()!=null) {
            if( ParseUser.getCurrentUser().getString("email")!=null) {
                email = ParseUser.getCurrentUser().getString("email").toString();
            }

        }
        return email;
    }



    public static String getXeroxCategory(){ return xeroxCategory; }

    public static double getLatitude(){
        return latitude;
    }

    public static double getLongitude() { return longitude; }

    public static int getNumOfPages() { return numOfPages; }

    public static int getNumOfCopies() { return numOfCopies; }

    public static int getPackageSize() { return packageSize;}

    public static String getRangeString() { return pageRange;}
/*
    public static ParseFile getPrintFile() { return printFile;}

    public static ParseGeoPoint getGeoPoint() { return geoPoint;}
*/
    public static String getOrderType() { return orderType;}

    public static String getType(){ return type; }

    public static void setObjId(String string){
        objId = string;
    }

    public static void setUsername(String string){
        username = string;
    }



    public static void setXeroxCategory(String string){ xeroxCategory = string;}

    public static void setLatitude(double lat){ latitude = lat; }

    public static void setLongitude(double lng){
        longitude = lng;
    }

    public static void setNumOfPages(int num) { numOfPages = num;}

    public static void setNumOfCopies(int num){ numOfCopies = num;}

    public static void setPackageSize(int size) { packageSize = size;}

    public static void setRangeString(String string) { pageRange = string;}
/*
    public static void setGeoPoint(ParseGeoPoint point) { geoPoint = point;}

    public static void setPrintFile(ParseFile file) { printFile = file;}
*/
    public static void setOrderType(String string){ orderType = string;}

    public static void setType(String string) { type = string; }
}
