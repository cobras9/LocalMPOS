package com.mobilis.android.nfc.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

/**
 * Created by lewischao on 2/10/15.
 */
public class ObjectMapperUtil {
    public static ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setPropertyNamingStrategy(new UpperCaseStrategy());
        return objectMapper;
    }

    static class UpperCaseStrategy extends PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy {

        private static final long serialVersionUID = 1L;

        @Override
        public String translate(String arg0) {
            return arg0.toUpperCase();
        }
    }
}
