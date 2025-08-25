package com.example.trx.support.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;

@Slf4j
public class JsonUtil {

    private static final ObjectMapper mapper=new ObjectMapper();
    private static final JSONParser jsonParser =new JSONParser();

    public static Object parseString(String s) {
        if(s==null)
            return "";
        try {
            return  jsonParser.parse(s);
        } catch (ParseException e) {
            log.info("ERROR(ParseString): {}",s);
            return s;
        }
    }

    public static <T> T readFor(String s,Class<T> type) {
        log.info("JSON PARSE: {}",s);
        if(s==null)
            return null;
        try {
            return (T) mapper.readerFor(type).readValue(s);
        }  catch (IOException e) {
            log.info("ERROR(ReadFor): {}",s);
            return null;
        }
    }

    public static <T> T convert(Object object,Class<T> type) {
        if(object==null)
            return null;
        return mapper.convertValue(object,type);
    }

    public static Object parseObject(Object o) {
        if(o==null)
            return "";
        try {
            return  jsonParser.parse(o.toString());
        } catch (ParseException e) {
            log.info("ERROR(ParseObject): {}",o);
            return null;
        }
    }

    public static String  toJsonString(Object o) {
        if(o==null)
            return "";
        try {
            return  mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
