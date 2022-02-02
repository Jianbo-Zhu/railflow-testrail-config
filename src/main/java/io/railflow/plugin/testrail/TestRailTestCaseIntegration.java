package io.railflow.plugin.testrail;

import java.util.HashMap;
import java.util.Map;

import com.katalon.platform.api.model.Integration;

public class TestRailTestCaseIntegration implements Integration {
    private String testCaseId;
    private String testCaseAuthor;
    private String testCaseDesc;
    private String testCaseJiraID;

    public String getTestCaseDesc() {
        return testCaseDesc;
    }

    public void setTestCaseDesc(String testCaseDesc) {
        this.testCaseDesc = testCaseDesc;
    }

    public String getTestCaseJiraID() {
        return testCaseJiraID;
    }

    public void setTestCaseJiraID(String testCaseJiraID) {
        this.testCaseJiraID = testCaseJiraID;
    }

    public void setTestCaseId(String testCaseId) {
        this.testCaseId = testCaseId;
    }

    public String getTestCaseId() {
        return testCaseId != null ? testCaseId.replaceAll("\\D", "") : null;
    }

    public void setTestCaseAuthor(String testCaseAuthor) {
        this.testCaseAuthor = testCaseAuthor;
    }

    public String getTestCaseAuthor(){
        return testCaseAuthor;
    }

    @Override
    public String getName() {
        return TestRailConstants.INTEGRATION_ID;
    }

    @Override
    public Map<String, String> getProperties() {
        HashMap<String, String> props = new HashMap<>();
        props.put(TestRailConstants.INTEGRATION_TESTCASE_ID, getTestCaseId());
        props.put(TestRailConstants.INTEGRATION_TESTCASE_AUTHOR, getTestCaseAuthor());
        props.put(TestRailConstants.INTEGRATION_TESTCASE_DESC, getTestCaseDesc());
        props.put(TestRailConstants.INTEGRATION_TESTCASE_JIRAID, getTestCaseJiraID());
        return props;
    }
}
