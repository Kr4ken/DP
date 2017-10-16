package com.kr4ken.habitica.utils;

import com.kr4ken.habitica.domain.Argument;

public class ArgUtils {

    private ArgUtils(){
    }

    public static Argument arg(String argName, String argValue){
        return new Argument(argName, argValue);
    }
}
