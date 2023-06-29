package com.nirbhay.springbootlibrary.utils;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class ExtractJWT {
    public static String payloadJWTExtraction(String token,String extraction) {
        token.replace("Bearer", "");

        /*
        As JWT header,payload,signature is separated by period
        for eg: eyJraWQiOiJheHQtdGtZUGRJNlFSMmFzb2VwYzlaVG85NGp0Z3AxcVAxTVprUnNIVE1BIiwiYWxnIjoiUlMyNTYifQ.eyJzdWIiOiIwMHVhNWYwcm1qbWZiaDJEYTVkNyIsIm5hbWUiOiJUZXN0IFVzZXIiLCJlbWFpbCI6InRlc3R1c2VyQGVtYWlsLmNvbSIsInZlciI6MSwiaXNzIjoiaHR0cHM6Ly9kZXYtNDI2OTA1ODQub2t0YS5jb20vb2F1dGgyL2RlZmF1bHQiLCJhdWQiOiIwb2FhNWY0NHZ0N0ZOYWY1UDVkNyIsImlhdCI6MTY4ODAyODU1NywiZXhwIjoxNjg4MDMyMTU3LCJqdGkiOiJJRC55QUN2MVJqTnp6ay02MWRaT1lXbVk0MkNJLXpLVWZFU3RvNVNfUHdidFpRIiwiYW1yIjpbInB3ZCJdLCJpZHAiOiIwMG9hNWNrZ21vR1h6b1RmRzVkNyIsIm5vbmNlIjoiSWpobDhVNjdWQzhxTkIzTE0wS2JRZk5MZlFmaExPT3dCcmVwZHl0ZVRUTW5nWklGRFJpaTl6Vk9CQ081aXVSYSIsInByZWZlcnJlZF91c2VybmFtZSI6InRlc3R1c2VyQGVtYWlsLmNvbSIsImF1dGhfdGltZSI6MTY4ODAyODU1MywiYXRfaGFzaCI6Ijh5bmFEVzdVUnV5RWdOQjRjNEI2NmcifQ.h0o8zGZZGsPNhBZrm_xZLhZo_2joyJ2w9qd0GNz7es38yVeNz3GRz-VJ4mQuWrBJ5gM2GEC6JaNJfRNuWU4VRbRIxRePkizdM1QinZo5qiNfWZ2GXjdogPZusvPynyN-otP_kf4j9Rv9eq__QvjbihYfThg78Z06rS188WfI1U-75RM10R4OhYEB5j7uvulPpH9q8U8ZDD5fATbsL1y4vZfnxokcGNmoiDdZmTgks9cyLdHYTdd3FQoMunzvOonrvlOGWEuN5ltBI_Llg1EZyHCwzq4f-b2zknG6rHBCtCOGsKAe85ThNc3j3R1WtbpVDaJj1FtGYidy-OyyDpKZyQ
         */
        String[] chunks = token.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();

        String payload = new String(decoder.decode(chunks[1]));

        //on decoding payload, its is speared by commas
        String[] enteries = payload.split(",");

        Map<String, String> map = new HashMap<>();

        for (String entry : enteries) {
            String[] keyValue = entry.split(":");
            //as sub contains email

            if (keyValue[0].equals(extraction)) {
                int remove = 1;
                if (keyValue[1].endsWith("}")) {
                    remove = 2;
                }

                keyValue[1] = keyValue[1].substring(0, keyValue[1].length() - remove);
                keyValue[1] = keyValue[1].substring(1);

                map.put(keyValue[0], keyValue[1]);
            }

            if (map.containsKey(extraction)) {
                return map.get(extraction);
            }
        }
        return null;
    }

}
