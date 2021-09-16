package com.sbytestream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CmdLineParser {
    public CmdLineParser(String[] args) {
        parse(args);
    }

    public String getParamValue(String name) {
        return pairs.get(name);
    }

    public boolean hasFlag(String flagName) {
        if (!pairs.containsKey(flagName))
            return false;
        else
            return (pairs.get(flagName) == null);
    }

    public String getAt(int index) {
        return rawValues.get(index);
    }

    private void parse(String[] args) {
        String currentParamName = null;

        for(int n=0; n < args.length; n++) {
            String current = args[n];
            if (current.charAt(0) == '-') {
                currentParamName = current.substring(1);
                pairs.put(currentParamName, null);
            }
            else {
                if (currentParamName != null) {
                    pairs.put(currentParamName, current);
                }
                else {
                    rawValues.add(current);
                }
            }
        }
    }

    HashMap<String, String> pairs = new HashMap<String, String>();
    List<String> rawValues = new ArrayList<String>();
}

