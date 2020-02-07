package com.emitter.utils;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {
    private final static Logger logger = LoggerFactory.getLogger(Utils.class);

    public static String getValueFromRegioncomResponse(String responseLine, String jsonBlock, String neededValue) {

        final JSONObject obj = new JSONObject(responseLine);
        logger.info("Trying to get {} from responseLine ", neededValue);
        String result;
        if (jsonBlock.isEmpty()) {
            logger.info("json block is empty..");
            result = obj.getString(neededValue);
        } else {
            logger.info("json block is NOT empty..");
            JSONObject geodata = obj.getJSONObject(jsonBlock);
            result = geodata.getString(neededValue);
        }
        logger.info("We've parsed RegionCom response. Here is a result: {}", neededValue);
        logger.info(result);
        return result;
    }
}