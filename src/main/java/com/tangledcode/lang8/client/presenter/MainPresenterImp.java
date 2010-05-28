package com.tangledcode.lang8.client.presenter;

import org.enunes.gwt.mvp.client.EventBus;
import org.enunes.gwt.mvp.client.presenter.BasePresenter;
import org.enunes.gwt.mvp.client.presenter.Presenter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.tangledcode.lang8.client.event.LoginClickEvent;
import com.tangledcode.lang8.client.event.LoginClickHandler;
import com.tangledcode.lang8.client.event.RegistrationClickEvent;
import com.tangledcode.lang8.client.event.RegistrationClickHandler;
import com.tangledcode.lang8.client.event.ResetRegistrationEvent;
import com.tangledcode.lang8.client.event.ResetRegistrationHandler;
import com.tangledcode.lang8.client.event.UserLoginEvent;
import com.tangledcode.lang8.client.event.UserLoginHandler;
import com.tangledcode.lang8.client.event.UserRegistrationEvent;
import com.tangledcode.lang8.client.event.UserRegistrationHandler;
import com.tangledcode.lang8.client.model.User;
import com.tangledcode.lang8.client.presenter.MainPresenter.Display;
import com.tangledcode.lang8.client.service.UserLoginService;
import com.tangledcode.lang8.client.service.UserLoginServiceAsync;

public class MainPresenterImp extends BasePresenter<Display> implements MainPresenter {

    private final Provider<RegistrationPresenter> registrationProvider;
    private final Provider<LoginPresenter> loginProvider;
    private final Provider<ProfilePresenter> profileProvider;
    
    private UserLoginServiceAsync userSvc = GWT.create(UserLoginService.class);
    
    private Presenter<? extends org.enunes.gwt.mvp.client.view.Display> presenter;

    @Inject
    public MainPresenterImp(EventBus eventBus, Display display, MenuPresenter menuPresenter, 
            Provider<RegistrationPresenter> registrationProvider,
            Provider<LoginPresenter> loginProvider,
            Provider<ProfilePresenter> profileProvider) {
        super(eventBus, display);

        this.registrationProvider = registrationProvider;
        this.loginProvider = loginProvider;
        this.profileProvider = profileProvider;

        menuPresenter.bind();
        this.display.addMenu(menuPresenter.getDisplay());
    }

    private void switchPresenter(Presenter<? extends org.enunes.gwt.mvp.client.view.Display> presenter) {
        if(this.presenter != null) {
            this.presenter.unbind();
            display.removeContent();
        }

        this.presenter = presenter;

        if(presenter != null) {
            this.display.addContent(presenter.getDisplay());
            this.presenter.bind();
        }
    }

    @Override
    public void unbind() {
        super.unbind();
        if(this.presenter != null) {
            this.presenter.unbind();
        }
    }

    @Override
    public void bind() {
        super.bind();

        this.registerHandler(this.eventBus.addHandler(ResetRegistrationEvent.getType(), new ResetRegistrationHandler() {

            public void onResetRegistration(ResetRegistrationEvent event) {
                doResetRegistration();
            }
        }));
        
        this.registerHandler(this.eventBus.addHandler(UserRegistrationEvent.getType(), new UserRegistrationHandler() {
            
            public void onUserRegistration(UserRegistrationEvent event) {
                doUserRegistration(event.getUser());
            }
        }));
        
        this.registerHandler(this.eventBus.addHandler(UserLoginEvent.getType(), new UserLoginHandler() {
            
            public void onUserLogin(UserLoginEvent event) {
                doUserLogin(event.getUser());
            }
        }));
        
        // menu click events
        
        this.registerHandler(this.eventBus.addHandler(RegistrationClickEvent.getType(), new RegistrationClickHandler() {
            
            public void onRegistrationClick(RegistrationClickEvent event) {
                doRegistrationClick();
            }
        }));
        
        this.registerHandler(this.eventBus.addHandler(LoginClickEvent.getType(), new LoginClickHandler() {
            
            public void onLoginClick(LoginClickEvent event) {
                doLoginClick();
            }
        }));
        
        this.registerHandler(this.eventBus.addHandler(ProfileClickEvent.getType(), new ProfileClickHandler() {
            
            public void onProfileClick(ProfileClickEvent event) {
                doProfileClick();
            }
        }));
        
        eventBus.fireEvent(new LoginClickEvent());
    }

    protected void doProfileClick() {
        final ProfilePresenter presenter = this.profileProvider.get();
        this.switchPresenter(presenter);
        
        if(this.userSvc == null) {
            this.userSvc = GWT.create(UserLoginService.class);
        }
        
        final AsyncCallback<User> callback = new AsyncCallback<User>() {
            
            public void onSuccess(User user) {
                presenter.setUser(user);
            }
            
            public void onFailure(Throwable caught) {
            }
        };
        
        this.userSvc.getUser(1L, callback);
    }

    protected void doLoginClick() {
        final LoginPresenter presenter = this.loginProvider.get();
        this.switchPresenter(presenter);
    }

    protected void doRegistrationClick() {
        final RegistrationPresenter presenter = this.registrationProvider.get();
        this.switchPresenter(presenter);
    }

    protected void doUserLogin(User user) {
        if(this.userSvc == null) {
            this.userSvc = GWT.create(UserLoginService.class);
        }
        
        AsyncCallback<String> callback = new AsyncCallback<String>() {

            public void onFailure(Throwable caught) {
                System.out.println("could not login");
            }

            public void onSuccess(String sessionId) {
                Cookies.setCookie("sessionId", sessionId);
                System.out.println(sessionId);
                eventBus.fireEvent(new ProfileClickEvent());
            }
        };
        
        this.userSvc.authenticate(user.getUsername(), user.getPassword(), callback);
    }

    protected void doUserRegistration(User user) {
        if(this.userSvc == null) {
            this.userSvc = GWT.create(UserLoginService.class);
        }
        
        AsyncCallback<Long> callback = new AsyncCallback<Long>() {
            
            public void onSuccess(Long id) {
                System.out.println("user is reg");
            }
            
            public void onFailure(Throwable caught) {
                System.out.println("could not reg user");
            }
        };
        
        this.userSvc.saveUser(user, callback);
    }

    protected void doResetRegistration() {
        
    }
    
}