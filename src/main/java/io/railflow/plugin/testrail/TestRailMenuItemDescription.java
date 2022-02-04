package io.railflow.plugin.testrail;

import com.katalon.platform.api.extension.ToolItemWithMenuDescription;
import com.katalon.platform.api.service.ApplicationManager;
import com.katalon.platform.api.ui.DialogActionService;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public class TestRailMenuItemDescription implements ToolItemWithMenuDescription {

    private Menu menu;

    @Override
    public String name() {
        return "TestRail";
    }

    @Override
    public String toolItemId() {
        return TestRailConstants.PLUGIN_ID + ".testRailToolItem";
    }

    @Override
    public String iconUrl() {
        return "platform:/plugin/" + TestRailConstants.PLUGIN_ID + "/icons/icon.png";
    }

    @Override
    public void defaultEventHandler() {
        if (menu != null && !menu.isDisposed()) {
            menu.setVisible(true);
        }
    }

    @Override
    public boolean isItemEnabled() {
        return true;
    }

    @Override
    public Menu getMenu(Control parent) {
        menu = new Menu(parent);
        MenuItem exportTestsMenuItem = new MenuItem(menu, SWT.PUSH);
        exportTestsMenuItem.setText("Export Tests");
        exportTestsMenuItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                MessageDialog.openInformation(parent.getShell(), "Export Tests", "Under construction...");
            }
        });

        MenuItem importTestsMenuItem = new MenuItem(menu, SWT.PUSH);
        importTestsMenuItem.setText("Import Tests");
        importTestsMenuItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                MessageDialog.openInformation(parent.getShell(), "Import Tests", "Under construction...");
            }
        });

        MenuItem settingMenuItem = new MenuItem(menu, SWT.PUSH);
        settingMenuItem.setText("Settings");
        settingMenuItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                ApplicationManager.getInstance().getUIServiceManager().getService(DialogActionService.class)
                        .openPluginPreferencePage(
                                TestRailConstants.PREF_PAGE_ID);
            }
        });

        // menu.addMenuListener(new MenuAdapter() {

        // @Override
        // public void menuShown(MenuEvent e) {

        // try {
        // exportTestsMenuItem.setEnabled(
        // PlatformUtil.getCurrentProject() != null &&
        // getSettingStore().isIntegrationEnabled());
        // importTestsMenuItem.setEnabled(PlatformUtil.getCurrentProject() != null);
        // } catch (Exception ex) {
        // ex.printStackTrace();
        // }
        // }
        // });
        return menu;
    }
}
