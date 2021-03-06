package io.railflow.plugin.testrail;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import io.railflow.license.LicenseHandler;
import io.railflow.license.LicenseHandlerFactoryImpl;
import io.railflow.testrail.client.api.TestRailClient;
import io.railflow.testrail.client.api.TestRailConnectionParameters;
import io.railflow.testrail.client.api.impl.ApiGetMethod;
import io.railflow.testrail.client.api.impl.TestRailClientImpl;
import io.railflow.testrail.client.api.impl.TestRailConnectionParametersImpl;
import io.railflow.testrail.client.model.Project;

import com.katalon.platform.api.exception.ResourceException;
import com.katalon.platform.api.preference.PluginPreference;
import com.katalon.platform.api.service.ApplicationManager;
import com.katalon.platform.api.ui.UISynchronizeService;

public class TestRailPreferencePage extends PreferencePage implements TestRailComponent {

    private Button chckEnableIntegration;

    private Group grpAuthentication;

    private Text txtUsername;

    private Text txtPassword;

    private Text txtUrl;

    private Text txtProject;

    private Text txtLicenseKey;

    private Composite container;

    private Button btnTestConnection;

    private Label lblConnectionStatus;

    private Thread thread;

    @Override
    protected Control createContents(Composite composite) {
        container = new Composite(composite, SWT.NONE);
        container.setLayout(new GridLayout(1, false));

        chckEnableIntegration = new Button(container, SWT.CHECK);
        chckEnableIntegration.setText("Enable Railflow Plugin");

        grpAuthentication = new Group(container, SWT.NONE);
        grpAuthentication.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        GridLayout glAuthentication = new GridLayout(2, false);
        glAuthentication.horizontalSpacing = 15;
        glAuthentication.verticalSpacing = 10;
        grpAuthentication.setLayout(glAuthentication);
        grpAuthentication.setText("TestRail Configuration");

        createLabel("URL");
        txtUrl = createTextbox();

        createLabel("Username");
        txtUsername = createTextbox();

        createLabel("Password / API Key");
        txtPassword = createPasswordTextbox();

        createLabel("Railflow License Key");
        txtLicenseKey = createTextbox();

        createLabel("Project ID / Key");
        txtProject = createTextbox();

        btnTestConnection = new Button(grpAuthentication, SWT.PUSH);
        btnTestConnection.setText("Test Connection");
        btnTestConnection.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                testTestRailConnection(
                        txtUsername.getText(),
                        txtPassword.getText(),
                        txtUrl.getText(),
                        txtProject.getText()
                );
            }
        });

        lblConnectionStatus = new Label(grpAuthentication, SWT.WRAP);
        lblConnectionStatus.setText("");
        lblConnectionStatus.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true, 1, 1));

        handleControlModifyEventListeners();
        initializeInput();
        
        return container;
    }

    private Text createTextbox() {
        Text text = new Text(grpAuthentication, SWT.BORDER);
        GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gridData.widthHint = 200;
        text.setLayoutData(gridData);
        return text;
    }

    private Text createPasswordTextbox(){
        Text text = new Text(grpAuthentication, SWT.PASSWORD | SWT.BORDER);
        GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gridData.widthHint = 200;
        text.setLayoutData(gridData);
        return text;
    }

    private void createLabel(String text) {
        Label label = new Label(grpAuthentication, SWT.NONE);
        label.setText(text);
        GridData gridData = new GridData(SWT.LEFT, SWT.TOP, false, false);
        label.setLayoutData(gridData);
    }

    private void testTestRailConnection(String username, String password, String url, String projectId) {
        btnTestConnection.setEnabled(false);
        lblConnectionStatus.setForeground(lblConnectionStatus.getDisplay().getSystemColor(SWT.COLOR_DARK_YELLOW));
        lblConnectionStatus.setText("Connecting...");
        lblConnectionStatus.requestLayout();
        thread = new Thread(() -> {
            try {
                // test connection here
                // TestRailConnector connector = new TestRailConnector(url, username, password);
                // connector.getProject(projectId);
                TestRailConnectionParameters testRailConnectionParameters = new TestRailConnectionParametersImpl(url, username, password);
                try (TestRailClient testRailClient = new TestRailClientImpl(testRailConnectionParameters, null)) {
                    Project project = testRailClient.executeGetRequest(ApiGetMethod.GET_PROJECT, Integer.parseInt(projectId));
                    System.out.println(project.getName());
                }
                syncExec(() -> {
                    lblConnectionStatus
                            .setForeground(lblConnectionStatus.getDisplay().getSystemColor(SWT.COLOR_DARK_GREEN));
                    lblConnectionStatus.setText("Succeeded!");
                    lblConnectionStatus.requestLayout();
                });
            } catch (Exception e) {
                System.err.println("Cannot connect to TestRail.");
                e.printStackTrace(System.err);
                syncExec(() -> {
                    lblConnectionStatus
                            .setForeground(lblConnectionStatus.getDisplay().getSystemColor(SWT.COLOR_DARK_RED));
                    lblConnectionStatus.setText("Failed: " + e.getMessage());
                    lblConnectionStatus.requestLayout();
                });
            } finally {
                syncExec(() -> btnTestConnection.setEnabled(true));
            }
        });
        thread.start();
    }

    void syncExec(Runnable runnable) {
        if (lblConnectionStatus != null && !lblConnectionStatus.isDisposed()) {
            ApplicationManager.getInstance()
                    .getUIServiceManager()
                    .getService(UISynchronizeService.class)
                    .syncExec(runnable);
        }
    }

    private void handleControlModifyEventListeners() {
        chckEnableIntegration.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                recursiveSetEnabled(grpAuthentication, chckEnableIntegration.getSelection());
            }
        });
    }

    public static void recursiveSetEnabled(Control ctrl, boolean enabled) {
        if (ctrl instanceof Composite) {
            Composite comp = (Composite) ctrl;
            for (Control c : comp.getChildren()) {
                recursiveSetEnabled(c, enabled);
                c.setEnabled(enabled);
            }
        } else {
            ctrl.setEnabled(enabled);
        }
    }

    @Override
    public boolean performOk() {
        try {
            PluginPreference pluginStore = getPluginStore();
            
            if (!super.isControlCreated()) {
                return super.performOk();
            }
            
            pluginStore.setBoolean(TestRailConstants.PREF_TESTRAIL_ENABLED, chckEnableIntegration.getSelection());
            pluginStore.setString(TestRailConstants.PREF_TESTRAIL_USERNAME, txtUsername.getText());
            pluginStore.setString(TestRailConstants.PREF_TESTRAIL_PASSWORD, txtPassword.getText());
            pluginStore.setString(TestRailConstants.PREF_TESTRAIL_URL, txtUrl.getText());
            pluginStore.setString(TestRailConstants.PREF_TESTRAIL_PROJECT, txtProject.getText());
            pluginStore.setString(TestRailConstants.PREF_TESTRAIL_LICENSEKEY, txtLicenseKey.getText());

            pluginStore.save();
            // check license
            try{
                LicenseHandler licenseHandler = LicenseHandlerFactoryImpl.THE_INSTANCE.create(null);
                licenseHandler.checkLicense(txtLicenseKey.getText());
            } catch (Exception e) {
                MessageDialog.openWarning(getShell(), "Warning", "License Key is invalid or expired!");
            }

            return super.performOk();
        } catch (ResourceException e) {
            MessageDialog.openWarning(getShell(), "Warning", "Unable to update TestRail Integration Settings.");
            return false;
        }
    }

    private void initializeInput() {
        try {
            PluginPreference pluginStore = getPluginStore();

            chckEnableIntegration.setSelection(pluginStore.getBoolean(TestRailConstants.PREF_TESTRAIL_ENABLED, false));
            chckEnableIntegration.notifyListeners(SWT.Selection, new Event());

            txtUsername.setText(pluginStore.getString(TestRailConstants.PREF_TESTRAIL_USERNAME, ""));
            txtPassword.setText(pluginStore.getString(TestRailConstants.PREF_TESTRAIL_PASSWORD, ""));
            txtUrl.setText(pluginStore.getString(TestRailConstants.PREF_TESTRAIL_URL, ""));
            txtProject.setText(pluginStore.getString(TestRailConstants.PREF_TESTRAIL_PROJECT, ""));
            txtLicenseKey.setText(pluginStore.getString(TestRailConstants.PREF_TESTRAIL_LICENSEKEY, ""));

            container.layout(true, true);
        } catch (ResourceException e) {
            System.out.println(e.getDetailMessage());
            MessageDialog.openWarning(getShell(), "Warning", "Unable to update TestRail Integration Settings.");
        }
    }
}
