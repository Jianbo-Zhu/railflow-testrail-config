package com.railflow.plugin.testrail;

import org.eclipse.jface.preference.PreferencePage;

import com.katalon.platform.api.extension.PluginPreferencePage;

public class TestRailPluginPreferencePage implements PluginPreferencePage {

    @Override
    public String getName() {
        return "Railflow TestRail Config";
    }

    @Override
    public String getPageId() {
        return "com.railflow.plugin.testrail.TestRailPluginPreferencePage";
    }

    @Override
    public Class<? extends PreferencePage> getPreferencePageClass() {
        return TestRailPreferencePage.class;
    }

}
