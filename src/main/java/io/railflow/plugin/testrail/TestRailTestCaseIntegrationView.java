package io.railflow.plugin.testrail;

import com.katalon.platform.api.extension.TestCaseIntegrationViewDescription;
import com.katalon.platform.api.model.Integration;
import com.katalon.platform.api.model.TestCaseEntity;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.util.Map;

public class TestRailTestCaseIntegrationView implements TestCaseIntegrationViewDescription.TestCaseIntegrationView {

    private Composite container;

    private Text txtId;
    private Text txtAuthor;
    private Text txtJiraID;
    private Text txtDesc;

    private Boolean isEdited = false;

    @Override
    public Control onCreateView(Composite parent,
            TestCaseIntegrationViewDescription.PartActionService partActionService, TestCaseEntity testCase) {

        container = new Composite(parent, SWT.NONE);

        createLabel("ID New");
        txtId = createTextbox();

        createLabel("Author New");
        txtAuthor = createTextbox();

        createLabel("JIRA ID New");
        txtJiraID = createTextbox();

        createLabel("Description New");
        txtDesc = createTextbox();

        GridLayout gridLayout = new GridLayout(2, false);
        gridLayout.verticalSpacing = 10;
        gridLayout.horizontalSpacing = 15;
        container.setLayout(gridLayout);

        Integration integration = testCase.getIntegration(TestRailConstants.INTEGRATION_ID);
        if (integration != null) {
            Map<String, String> integrationProps = integration.getProperties();
            if (integrationProps.containsKey(TestRailConstants.INTEGRATION_TESTCASE_ID)) {
                txtId.setText(integrationProps.get(TestRailConstants.INTEGRATION_TESTCASE_ID));
            }
            if (integrationProps.containsKey(TestRailConstants.INTEGRATION_TESTCASE_AUTHOR)) {
                txtAuthor.setText(integrationProps.get(TestRailConstants.INTEGRATION_TESTCASE_AUTHOR));
            }
            if (integrationProps.containsKey(TestRailConstants.INTEGRATION_TESTCASE_JIRAID)) {
                txtJiraID.setText(integrationProps.get(TestRailConstants.INTEGRATION_TESTCASE_JIRAID));
            }
            if (integrationProps.containsKey(TestRailConstants.INTEGRATION_TESTCASE_DESC)) {
                txtDesc.setText(integrationProps.get(TestRailConstants.INTEGRATION_TESTCASE_DESC));
            }
        }

        ModifyListener listener = modifyEvent -> {
            isEdited = true;
            partActionService.markDirty();
        };

        txtAuthor.addModifyListener(listener);
        txtId.addModifyListener(listener);
        txtDesc.addModifyListener(listener);
        txtJiraID.addModifyListener(listener);

        return container;
    }

    private Text createTextbox() {
        Text text = new Text(container, SWT.BORDER);
        GridData gridData = new GridData(SWT.FILL, SWT.CENTER, false, false);
        gridData.widthHint = 200;
        text.setLayoutData(gridData);
        return text;
    }

    private void createLabel(String text) {
        Label label = new Label(container, SWT.NONE);
        label.setText(text);
        GridData gridData = new GridData(SWT.LEFT, SWT.TOP, false, false);
        label.setLayoutData(gridData);
    }

    @Override
    public Integration getIntegrationBeforeSaving() {
        TestRailTestCaseIntegration integration = new TestRailTestCaseIntegration();
        integration.setTestCaseId(txtId.getText());
        integration.setTestCaseAuthor(txtAuthor.getText());
        integration.setTestCaseDesc(txtDesc.getText());
        integration.setTestCaseJiraID(txtJiraID.getText());
        isEdited = false;
        return integration;
    }

    @Override
    public boolean needsSaving() {
        return isEdited;
    }
}
