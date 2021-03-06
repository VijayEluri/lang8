package com.tangledcode.lang8.client.presenter;

import java.util.ArrayList;
import java.util.List;

import org.enunes.gwt.mvp.client.EventBus;
import org.enunes.gwt.mvp.client.presenter.BasePresenter;
import org.enunes.gwt.mvp.client.presenter.Presenter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.tangledcode.lang8.client.CurrentUser;
import com.tangledcode.lang8.client.dto.AuthenticationResponse;
import com.tangledcode.lang8.client.dto.GroupDTO;
import com.tangledcode.lang8.client.dto.LanguageDTO;
import com.tangledcode.lang8.client.dto.TextDTO;
import com.tangledcode.lang8.client.dto.UserDTO;
import com.tangledcode.lang8.client.event.GroupClickEvent;
import com.tangledcode.lang8.client.event.GroupClickHandler;
import com.tangledcode.lang8.client.event.LoginClickEvent;
import com.tangledcode.lang8.client.event.LoginClickHandler;
import com.tangledcode.lang8.client.event.LogoutClickEvent;
import com.tangledcode.lang8.client.event.LogoutClickHandler;
import com.tangledcode.lang8.client.event.NewGroupEvent;
import com.tangledcode.lang8.client.event.NewGroupHandler;
import com.tangledcode.lang8.client.event.RegistrationClickEvent;
import com.tangledcode.lang8.client.event.RegistrationClickHandler;
import com.tangledcode.lang8.client.event.ResetRegistrationEvent;
import com.tangledcode.lang8.client.event.ResetRegistrationHandler;
import com.tangledcode.lang8.client.event.TextClickEvent;
import com.tangledcode.lang8.client.event.TextClickHandler;
import com.tangledcode.lang8.client.event.TextSearchClickEvent;
import com.tangledcode.lang8.client.event.TextSearchClickHandler;
import com.tangledcode.lang8.client.event.TextSearchEvent;
import com.tangledcode.lang8.client.event.TextSearchHandler;
import com.tangledcode.lang8.client.event.TextSubmitEvent;
import com.tangledcode.lang8.client.event.TextSubmitHandler;
import com.tangledcode.lang8.client.event.UserLoggedInEvent;
import com.tangledcode.lang8.client.event.UserLoggedInHandler;
import com.tangledcode.lang8.client.event.UserLoggedOutEvent;
import com.tangledcode.lang8.client.event.UserLoginEvent;
import com.tangledcode.lang8.client.event.UserLoginHandler;
import com.tangledcode.lang8.client.event.UserRegistrationEvent;
import com.tangledcode.lang8.client.event.UserRegistrationHandler;
import com.tangledcode.lang8.client.model.Group;
import com.tangledcode.lang8.client.model.Language;
import com.tangledcode.lang8.client.model.Text;
import com.tangledcode.lang8.client.model.User;
import com.tangledcode.lang8.client.presenter.MainPresenter.Display;
import com.tangledcode.lang8.client.presenter.RegistrationPresenter.Display.UserRegistrationDetails;
import com.tangledcode.lang8.client.presenter.TextPresenter.Display.SubmitTextDetails;
import com.tangledcode.lang8.client.service.GroupService;
import com.tangledcode.lang8.client.service.GroupServiceAsync;
import com.tangledcode.lang8.client.service.LanguageService;
import com.tangledcode.lang8.client.service.LanguageServiceAsync;
import com.tangledcode.lang8.client.service.TextSearchItemsService;
import com.tangledcode.lang8.client.service.TextSearchItemsServiceAsync;
import com.tangledcode.lang8.client.service.TextService;
import com.tangledcode.lang8.client.service.TextServiceAsync;
import com.tangledcode.lang8.client.service.UserService;
import com.tangledcode.lang8.client.service.UserServiceAsync;

public class MainPresenterImp extends BasePresenter<Display> implements MainPresenter {

    private final Provider<RegistrationPresenter> registrationProvider;
    private final Provider<LoginPresenter> loginProvider;
    private final Provider<ProfilePresenter> profileProvider;
    private final Provider<GroupPresenter> groupProvider;
    private final Provider<TextPresenter> textProvider;
    private final Provider<TextSearchPresenter> textSearchProvider;
    
    private Presenter<? extends org.enunes.gwt.mvp.client.view.Display> presenter;
    
    private UserServiceAsync userSvc = GWT.create(UserService.class);
    private GroupServiceAsync groupSvc = GWT.create(GroupService.class);
    private LanguageServiceAsync langSvc = GWT.create(LanguageService.class);
    private TextServiceAsync txtSvc = GWT.create(TextService.class);
    private TextSearchItemsServiceAsync txtSearchSvc = GWT.create(TextSearchItemsService.class);
	private Language langTemo;

    private int baseId;

    @Inject
    public MainPresenterImp(EventBus eventBus, Display display, MenuPresenter menuPresenter, 
            Provider<RegistrationPresenter> registrationProvider,
            Provider<LoginPresenter> loginProvider,
            Provider<ProfilePresenter> profileProvider,
            Provider<GroupPresenter> groupProvider,
            Provider<TextPresenter> textProvider,
            Provider<TextSearchPresenter> textSearchProvider) {
        super(eventBus, display);

        this.registrationProvider = registrationProvider;
        this.loginProvider = loginProvider;
        this.profileProvider = profileProvider;
        this.groupProvider = groupProvider;
        this.textProvider = textProvider;
        this.textSearchProvider = textSearchProvider;

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
        
        this.registerHandler(this.eventBus.addHandler(UserLoggedInEvent.getType(), new UserLoggedInHandler() {
            
            public void onUserLoggedIn(UserLoggedInEvent event) {
                doUserLoggedIn();
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
        
        this.registerHandler(this.eventBus.addHandler(LogoutClickEvent.getType(), new LogoutClickHandler() {

            public void onLogoutClick(LogoutClickEvent event) {
                doLogoutClick();
            }
        }));

        this.registerHandler(this.eventBus.addHandler(ProfileClickEvent.getType(), new ProfileClickHandler() {

            public void onProfileClick(ProfileClickEvent event) {
                doProfileClick(event.getId());
            }
        }));
        
        this.registerHandler(this.eventBus.addHandler(GroupClickEvent.getType(), new GroupClickHandler() {
            
            public void onGroupClick(GroupClickEvent event) {
                doGroupClick();
            }
        }));
        
        this.registerHandler(this.eventBus.addHandler(NewGroupEvent.getType(), new NewGroupHandler() {
            
            public void onNewGroupClick(NewGroupEvent event) {
                doNewGroup(event.getGroup());
            }
        }));
        
        this.registerHandler(this.eventBus.addHandler(TextClickEvent.getType(), new TextClickHandler() {

            public void onTextClick(TextClickEvent event) {
                doTextClick();
            }
        }
        

        ));
        this.registerHandler(this.eventBus.addHandler(TextSearchClickEvent.getType(), new TextSearchClickHandler() {

			public void onTextSearchClick(TextSearchClickEvent handler) {
				doTextSeachClick();
				
			}


        }
        

        ));
        
        this.registerHandler(this.eventBus.addHandler(TextSubmitEvent.getType(), new TextSubmitHandler() {
			
			public void onSubmitClick(TextSubmitEvent event) {
				doSubmitText(event.getText());
				
			}
		}));
        
        this.registerHandler(this.eventBus.addHandler(TextSearchEvent.getType(), new TextSearchHandler() {
			
			public void onSubmitClick(TextSearchEvent event) {
				doTextSearch(event.getText(), event.getValue());
				
			}
		}));

        eventBus.fireEvent(new LoginClickEvent());
    }


	protected void doTextSearch(String value, String text) {
		final TextSearchPresenter presenter = this.textSearchProvider.get();
		final AsyncCallback<List<TextDTO>> callback = new AsyncCallback<List<TextDTO>>() {

			public void onFailure(Throwable arg0) {
				System.err.println(arg0);
				
			}

			public void onSuccess(List<TextDTO> arg0) {
				//presenter.setSearchItems(arg0);
				List<Text> returnTexts = new ArrayList<Text>();
				for(int i = 0; i < arg0.size();i++){
					returnTexts.add(new Text(arg0.get(i).getTitle(),arg0.get(i).getDescription(),arg0.get(i).getContent()));
				}
				presenter.setResultLabel(returnTexts);
				
			}
		};
		
		if(this.txtSearchSvc == null){
			this.txtSearchSvc = GWT.create(TextSearchItemsService.class);
		}
		this.txtSearchSvc.getSearchItems(value, text,callback);
		this.switchPresenter(presenter);
		
	}

	protected void doTextSeachClick() {
        final TextSearchPresenter presenter = this.textSearchProvider.get();
        this.switchPresenter(presenter);
		
	}
    protected void doUserLoggedIn() {
        eventBus.fireEvent(new ProfileClickEvent(CurrentUser.getUser().getId()));
    }

    protected void doTextClick() {
        final TextPresenter presenter = this.textProvider.get();
        
        final AsyncCallback<List<LanguageDTO>> callback = new AsyncCallback<List<LanguageDTO>>(){

			public void onFailure(Throwable arg0) {
				System.out.println("Failure by getting language codes!!!");
				System.err.println(arg0);
				
			}

			public void onSuccess(List<LanguageDTO> arg0) {
				System.out.println("Languages wurden empfangen" + arg0.get(0).getTitle());
				List<LanguageDTO> ruckgbae = arg0;
				presenter.setText(ruckgbae);
				
			}
        	
        };
        if(this.langSvc == null){
        	this.langSvc = GWT.create(LanguageService.class);
        }
        
        this.langSvc.getLanguages(callback);
        this.switchPresenter(presenter);
    }
    
    protected void doLoginClick() {
        final LoginPresenter presenter = this.loginProvider.get();
        this.switchPresenter(presenter);
    }
    
    protected void doLogoutClick() {
        CurrentUser.reset();
        doLoginClick();
        eventBus.fireEvent(new UserLoggedOutEvent());
    }

    protected void doRegistrationClick() {
        final RegistrationPresenter presenter = this.registrationProvider.get();
        this.switchPresenter(presenter);
    }
    
    protected void doGroupClick() {
        final GroupPresenter presenter = this.groupProvider.get();
        this.switchPresenter(presenter);
        
        if(this.groupSvc == null) {
            this.groupSvc = GWT.create(GroupService.class);
        }
        
        final AsyncCallback<Integer> callback4 = new AsyncCallback<Integer>() {

            public void onFailure(Throwable caught) {
            }

            public void onSuccess(Integer result) {
            	baseId = result;
            	showGroup1(presenter);
            }
        };
        
        this.groupSvc.getMaxId(callback4);
        
    }
    
    protected void showGroup1(final GroupPresenter presenter) {
    	
    	final AsyncCallback<GroupDTO> callback = new AsyncCallback<GroupDTO>() {

            public void onFailure(Throwable caught) {
            }

            public void onSuccess(GroupDTO group) {
                presenter.setGroup_1(new Group(group));
                showGroup2(presenter);
            }
        };
        
        groupSvc.getGroup(baseId-2, callback);
    }
    
    protected void showGroup2(final GroupPresenter presenter) {
    	
    	final AsyncCallback<GroupDTO> callback = new AsyncCallback<GroupDTO>() {

            public void onFailure(Throwable caught) {
            }

            public void onSuccess(GroupDTO group) {
                presenter.setGroup_2(new Group(group));
                showGroup3(presenter);
            }
        };
        
        groupSvc.getGroup(baseId-1, callback);
    }
    
    protected void showGroup3(final GroupPresenter presenter) {
    	
    	final AsyncCallback<GroupDTO> callback = new AsyncCallback<GroupDTO>() {

            public void onFailure(Throwable caught) {
            }

            public void onSuccess(GroupDTO group) {
                presenter.setGroup_3(new Group(group));
            }
        };
        
        groupSvc.getGroup(baseId, callback);
    }

    protected void doProfileClick(long id) {
        final ProfilePresenter presenter = this.profileProvider.get();
        this.switchPresenter(presenter);

        if(this.userSvc == null) {
            this.userSvc = GWT.create(UserService.class);
        }

        final AsyncCallback<UserDTO> callback = new AsyncCallback<UserDTO>() {

            public void onFailure(Throwable caught) {
            }

            public void onSuccess(UserDTO user) {
                presenter.setUser(new User(user));
            }
        };
        
        this.userSvc.getUser(id, CurrentUser.getSessionId(), callback);
    }
        
    protected void doNewGroup(Group group) {
        if(this.groupSvc == null) {
            this.groupSvc = GWT.create(GroupService.class);
        }
        
        AsyncCallback<Long> callback = new AsyncCallback<Long>() {
            
            public void onSuccess(Long id) {
                System.out.println("New group is created!");
            }
            
            public void onFailure(Throwable caught) {
                System.out.println("Could not create new Group!");
            }

        };
        
        this.groupSvc.saveGroup(new GroupDTO(group), callback);
        this.baseId ++;
        doGroupClick();
    }

    protected void doUserLogin(User user) {
        if(this.userSvc == null) {
            this.userSvc = GWT.create(UserService.class);
        }

        AsyncCallback<AuthenticationResponse> callback = new AsyncCallback<AuthenticationResponse>() {

            public void onFailure(Throwable caught) {
                System.out.println("could not login");
            }

            public void onSuccess(AuthenticationResponse response) {
                CurrentUser.setUser(response.getUser(), response.getSessionId());
                eventBus.fireEvent(new UserLoggedInEvent());
            }
        };

        this.userSvc.authenticate(user.getUsername(), user.getPassword(), callback);
    }

    protected void doUserRegistration(UserRegistrationDetails userRegistrationDetails) {
        if(this.userSvc == null) {
            this.userSvc = GWT.create(UserService.class);
        }

        AsyncCallback<Long> callback = new AsyncCallback<Long>() {

            public void onFailure(Throwable caught) {
                System.err.println("could not reg");
            }
            
            public void onSuccess(Long id) {
                eventBus.fireEvent(new LoginClickEvent());
            }

        };

        User user = new User(
                userRegistrationDetails.getUsername(),
                userRegistrationDetails.getEmail(),
                userRegistrationDetails.getPassword());
        
        this.userSvc.saveUser(new UserDTO(user), callback);
    }

    protected void doResetRegistration() {

    }
    
	protected void doSubmitText(SubmitTextDetails submitTextDetails) {
		if(this.userSvc == null) {
            this.userSvc = GWT.create(UserService.class);
        }

        AsyncCallback<Long> callback = new AsyncCallback<Long>() {

            public void onFailure(Throwable caught) {
                System.err.println("could not set text");
            }
            
            public void onSuccess(Long id) {
            	//TODO: change state
               // eventBus.fireEvent(new LoginClickEvent());
            }


        };
        
        final AsyncCallback<LanguageDTO> callback2 = new AsyncCallback<LanguageDTO>(){

			public void onFailure(Throwable arg0) {
				System.out.println("Failure by getting language codes by saving the text");
				System.err.println(arg0);
				
			}

			public void onSuccess(LanguageDTO arg0) {
				langTemo = new Language(arg0);
			}
        	
        };
        if(this.langSvc == null){
        	this.langSvc = GWT.create(LanguageService.class);
        }
       // System.out.println(submitTextDetails.getLanguage());
        this.langSvc.getLanguageById(submitTextDetails.getLanguage(), callback2);
        
        //TODO: GetLangage von
        
        Text sendText = new Text(submitTextDetails.getTitle(),submitTextDetails.getDescription(),submitTextDetails.getContent(),CurrentUser.getUser(),this.langTemo);
		
        this.txtSvc.sendText(new TextDTO(sendText), callback);
	}

}
