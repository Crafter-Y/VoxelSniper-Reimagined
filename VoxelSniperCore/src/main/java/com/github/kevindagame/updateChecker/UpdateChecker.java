package com.github.kevindagame.updateChecker;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateChecker {

    public UpdateChecker() {
        try {
            getLatestVersion();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getLatestVersion() throws Exception {
        //make request to the github api and get the latest release
        Pattern pattern = Pattern.compile("tag_name\":\"(.*?)\",");
        String url = "https://api.github.com/repos/KevinDaGame/VoxelSniper-Reimagined/releases/latest";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        int responseCode = con.getResponseCode();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        Matcher matcher = pattern.matcher(response.toString());
        if (matcher.find()) {
            return matcher.group(0);
            //TODO work with the version
        }
        return "";
    }

    /**
     * Checks if version 1 is newer than version 2
     *
     * @param version1 expects: a string that only contains numbers and dots
     * @param version2 expects: a string that only contains numbers and dots
     * @return true if the first version is higher than the second version
     */
    public boolean compareVersions(String version1, String version2) {
        //split v1 at dots
        //split v2 at dots
        //get the longest array
        //loop through the array
        //if v1[i] > v2[i] return true
        //if v1[i] < v2[i] return false
        //if v1[i] == v2[i] continue
        //if the loop ends return false

        version1 = version1.replaceAll("[^0-9.]", "");
        version2 = version2.replaceAll("[^0-9.]", "");
        String[] v1 = version1.split("\\.");
        String[] v2 = version2.split("\\.");
        var longest = v1.length > v2.length ? v1 : v2;
        for (int i = 0; i < longest.length; i++) {
            var v1i = getArrayIntValue(v1, i);
            var v2i = getArrayIntValue(v2, i);
            if (v1i > v2i) {
                return true;
            } else if (v1i < v2i) {
                return false;
            }
        }
        return false;
    }

    private int getArrayIntValue(String[] array, int index) {
        try {
            var val = array[index];
            try {
                return Integer.parseInt(val);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("The array contains a non-integer value");
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            return 0;
        }
    }
}
