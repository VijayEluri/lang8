package com.tangledcode.lang8.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.tangledcode.lang8.client.model.User;

public class UserRegistrationEvent extends GwtEvent<UserRegistrationHandler> {

    private static Type<UserRegistrationHandler> TYPE;
    
    private User user;

    public static Type<UserRegistrationHandler> getType() {
        return TYPE != null
                ? TYPE
                : (TYPE = new Type<UserRegistrationHandler>());
    }
    
    public UserRegistrationEvent(User user) {
        this.user = user;
    }

    @Override
    protected void dispatch(UserRegistrationHandler handler) {
        handler.onUserRegistration(this);
    }

    @Override
    public Type<UserRegistrationHandler> getAssociatedType() {
        return getType();
    }

    public User getUser() {
        return this.user;
    }

}
