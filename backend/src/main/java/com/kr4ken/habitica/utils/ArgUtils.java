package com.kr4ken.habitica.utils;

public class ArgUtils {

    private ArgUtils(){
    }

    public static Argument arg(String argName, String argValue){
        return new Argument(argName, argValue);
    }
}
