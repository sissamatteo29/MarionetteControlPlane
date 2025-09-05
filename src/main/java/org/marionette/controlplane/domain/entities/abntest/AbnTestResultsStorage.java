package org.marionette.controlplane.domain.entities.abntest;

import java.util.LinkedList;
import java.util.List;

public class AbnTestResultsStorage {

    private final List<SingleAbnTestResult> testResults = new LinkedList<>();

    public SingleAbnTestResult getResults(int index) {
        return testResults.get(index);
    }

    public void putResults(SingleAbnTestResult result) {
        testResults.add(result);
    }

}
