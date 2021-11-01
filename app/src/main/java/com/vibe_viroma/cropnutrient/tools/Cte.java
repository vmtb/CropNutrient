package com.vibe_viroma.cropnutrient.tools;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class Cte {
    public static String TAG_="CropTAG";
    public static String object2json(Object object){
        String data="";
        try {
            data = new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static Object string2class(String value, Class <?> the_class){
        Object object=null;
        try {
            object = (new ObjectMapper()).readValue(value, the_class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return object;
    }

}
