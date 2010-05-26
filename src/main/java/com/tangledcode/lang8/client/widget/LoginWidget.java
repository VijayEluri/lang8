package com.tangledcode.lang8.client.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.tangledcode.lang8.client.presenter.LoginPresenter.Display;

public class LoginWidget extends Composite implements Display {

    private static LoginWidgetUiBinder uiBinder = GWT.create(LoginWidgetUiBinder.class);

    @UiTemplate("ui/LoginWidget.ui.xml")
    interface LoginWidgetUiBinder extends UiBinder<Widget, LoginWidget> {
    }
    
    @UiField Button loginButton;
    @UiField TextBox usernameTextBox;
    @UiField PasswordTextBox passwordTextBox;

    public LoginWidget() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    public HasClickHandlers getLoginClickHandlers() {
        return this.loginButton;
    }

    public HasText getPasswordText() {
        return this.passwordTextBox;
    }

    public HasText getUsernameText() {
        return this.usernameTextBox;
    }

    public Widget asWidget() {
        return this;
    }

}
