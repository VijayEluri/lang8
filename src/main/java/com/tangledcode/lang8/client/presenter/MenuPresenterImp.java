package com.tangledcode.lang8.client.presenter;

import org.enunes.gwt.mvp.client.EventBus;
import org.enunes.gwt.mvp.client.presenter.BasePresenter;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.inject.Inject;
import com.tangledcode.lang8.client.event.LoginClickEvent;
import com.tangledcode.lang8.client.event.RegistrationClickEvent;
import com.tangledcode.lang8.client.presenter.MenuPresenter.Display;

public class MenuPresenterImp extends BasePresenter<Display> implements MenuPresenter {

    @Inject
    public MenuPresenterImp(EventBus eventBus, Display display) {
        super(eventBus, display);
    }

    @Override
    public void bind() {
        super.bind();
        
        this.display.getRegistrationClickHandlers().addClickHandler(new ClickHandler() {
            
            public void onClick(ClickEvent event) {
                eventBus.fireEvent(new RegistrationClickEvent());
            }
        });
        
        this.display.getLoginClickHandlers().addClickHandler(new ClickHandler() {
            
            public void onClick(ClickEvent event) {
                eventBus.fireEvent(new LoginClickEvent());
            }
        });
        
        this.display.getProfileClickHandlers().addClickHandler(new ClickHandler() {
            
            public void onClick(ClickEvent event) {
                eventBus.fireEvent(new ProfileClickEvent());
            }
        });
    }
}