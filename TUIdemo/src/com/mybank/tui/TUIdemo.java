package com.mybank.tui;

import com.mybank.domain.Account;
import com.mybank.domain.Bank;
import com.mybank.domain.CheckingAccount;
import com.mybank.domain.Customer;
import com.mybank.domain.SavingsAccount;
import java.util.Locale;

import jexer.TAction;
import jexer.TApplication;
import jexer.TField;
import jexer.TText;
import jexer.TWindow;
import jexer.event.TMenuEvent;
import jexer.menu.TMenu;

/**
 *
 * @author Alexander 'Taurus' Babich
 */
public class TUIdemo extends TApplication {

    private static final int ABOUT_APP = 2000;
    private static final int CUST_INFO = 2010;

    public static void main(String[] args) throws Exception {
        TUIdemo tdemo = new TUIdemo();
        (new Thread(tdemo)).start();
    }

    public TUIdemo() throws Exception {
        super(BackendType.SWING);

        Bank.getBank(); 

        Bank.addCustomer("Jane", "Simms");
        Bank.getCustomer(0).addAccount(new SavingsAccount(500.00, 0.05));
        Bank.getCustomer(0).addAccount(new CheckingAccount(200.00, 400.00));

        Bank.addCustomer("Owen", "Bryant");
        Bank.getCustomer(1).addAccount(new CheckingAccount(200.00, 0.00));

        Bank.addCustomer("Tim", "Soley");
        Bank.getCustomer(2).addAccount(new SavingsAccount(1500.00, 0.05));
        Bank.getCustomer(2).addAccount(new CheckingAccount(200.00, 0.00));

        Bank.addCustomer("Maria", "Soley");
        Bank.getCustomer(3).addAccount(new SavingsAccount(150.00, 0.05));

        addToolMenu();
        // Custom 'File' menu
        TMenu fileMenu = addMenu("&File");
        fileMenu.addItem(CUST_INFO, "&Customer Info");
        fileMenu.addDefaultItem(TMenu.MID_SHELL);
        fileMenu.addSeparator();
        fileMenu.addDefaultItem(TMenu.MID_EXIT);

        addWindowMenu();

        // Custom 'Help' menu
        TMenu helpMenu = addMenu("&Help");
        helpMenu.addItem(ABOUT_APP, "&About...");

        setFocusFollowsMouse(true);
        ShowCustomerDetails();
    }

    @Override
    protected boolean onMenu(TMenuEvent menu) {
        if (menu.getId() == ABOUT_APP) {
            messageBox("About", "\t\t\t\t\t   Just a simple Jexer demo.\n\nCopyright \u00A9 2019 Alexander \'Taurus\' Babich").show();
            return true;
        }
        if (menu.getId() == CUST_INFO) {
            ShowCustomerDetails();
            return true;
        }
        return super.onMenu(menu);
    }

    private void ShowCustomerDetails() {
        TWindow custWin = addWindow("Customer Info", 2, 1, 50, 14, TWindow.NOZOOMBOX);
        custWin.newStatusBar("Enter customer number (0-based) and press Show...");

        custWin.addLabel("Customer number (0 - " +
            (Bank.getNumberOfCustomers() - 1) + "): ", 2, 2);

        TField custNo = custWin.addField(26, 2, 4, false);

        TText details = custWin.addText(
            "Enter a customer number above and\npress [Show] to view details.",
            2, 4, 46, 8);

        custWin.addButton("&Show", 33, 2, new TAction() {
            @Override
            public void DO() {
                try {
                    int custNum = Integer.parseInt(custNo.getText().trim());
                    if (custNum < 0 || custNum >= Bank.getNumberOfCustomers()) {
                        messageBox("Error",
                            "Customer number must be between 0 and " +
                            (Bank.getNumberOfCustomers() - 1) + "!").show();
                        return;
                    }

                    Customer cust = Bank.getCustomer(custNum);
                    StringBuilder sb = new StringBuilder();
                    sb.append("Name:     ").append(cust.getFirstName())
                      .append(" ").append(cust.getLastName()).append("\n");
                    sb.append("Accounts: ").append(cust.getNumberOfAccounts()).append("\n\n");

                    Account acc = cust.getAccount(0);
                    sb.append("  Account #1: ");

                    if (acc instanceof SavingsAccount) {
                        sb.append("Savings\n");
                        sb.append("    Balance:  $")
                          .append(String.format(Locale.US, "%.2f", acc.getBalance()))
                          .append("\n");
                    } else if (acc instanceof CheckingAccount) {
                        sb.append("Checking\n");
                        sb.append("    Balance:  $")
                          .append(String.format(Locale.US, "%.2f", acc.getBalance()))
                          .append("\n");
                    } else {
                        sb.append("Unknown\n");
                        sb.append("    Balance:  $")
                          .append(String.format(Locale.US, "%.2f", acc.getBalance()))
                          .append("\n");
                    }

                    details.setText(sb.toString());

                } catch (NumberFormatException e) {
                    messageBox("Error", "Please enter a valid customer number!").show();
                }
            }
        });
    }
}
