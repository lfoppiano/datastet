package org.grobid.core.data;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class BiblioComponentWrapper {
    private Map<String, Integer> stringToRefKeyMap;
    private Map<Integer, String> refKeyToStringMap;
    private AtomicInteger refKeyGenerator;

    public BiblioComponentWrapper() {
        stringToRefKeyMap = new HashMap<>();
        refKeyToStringMap = new HashMap<>();
        refKeyGenerator = new AtomicInteger(0);
    }

    public void addMapping(String refKeyString) {
        if (!stringToRefKeyMap.containsKey(refKeyString)) {
            int refKey = refKeyGenerator.incrementAndGet();
            stringToRefKeyMap.put(refKeyString, refKey);
            refKeyToStringMap.put(refKey, refKeyString);
        }
    }

    public Integer getRefKey(String refKeyString) {
        String refKeyStringClean = refKeyString.replaceFirst("^#", "");
        addMapping(refKeyStringClean);
        return stringToRefKeyMap.get(refKeyStringClean);
    }

    public String getRefKeyString(int refKey) {
        return refKeyToStringMap.get(refKey);
    }

    public void removeMapping(String refKeyString) {
        Integer refKey = stringToRefKeyMap.remove(refKeyString);
        if (refKey != null) {
            refKeyToStringMap.remove(refKey);
        }
    }

    public void removeMapping(int refKey) {
        String refKeyString = refKeyToStringMap.remove(refKey);
        if (refKeyString != null) {
            stringToRefKeyMap.remove(refKeyString);
        }
    }
}