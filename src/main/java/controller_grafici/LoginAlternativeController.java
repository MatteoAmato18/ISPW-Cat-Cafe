package controller_grafici;

import javafx.scene.layout.VBox;
import java.util.logging.Level;
import java.util.logging.Logger;

import bean.LoginBean;
import bean.ModelBeanFactory;
import dao.SessionManager;

import exception.WrongLoginCredentialsException;
import facade.ApplicationFacade;

import view.LoginAlternativeView;

public class LoginAlternativeController {

    private static final Logger logger = Logger.getLogger(LoginAlternativeController.class.getName());

    private final NavigationService navigationService;
    private final String userType;                        

    private final LoginAlternativeView loginView; 

    public LoginAlternativeController(NavigationService nav, String userType) {
        this.navigationService = nav;
        this.userType = userType;

        this.loginView = new LoginAlternativeView();     
        addEventHandlers();
    }

    /* ---------------------------------------------------------- */
    private void addEventHandlers() {
        loginView.getConfirmButton().setOnAction(_ -> handleLogin());
        loginView.getRegisterButton().setOnAction(_ -> goToRegistration());
    }

    /* ---------------------------------------------------------- */
    private void handleLogin() {

        
        if (userType == null && loginView.getLoginTypeGroup().getSelectedToggle() == null) {
            ApplicationFacade.showErrorMessage("Errore",
                    "Tipo di login non selezionato",
                    "Seleziona User o Staf prima di procedere.");
            return;
        }

        /* -------- POPOLA IL BEAN -------- */
        LoginBean bean = ModelBeanFactory.getLoginBean(loginView);

        if (userType != null)
            bean.setUserType(userType);              // prefissato dal costruttore

        
        boolean ok = true;
        loginView.hideErrorMessages();

        if (!bean.hasValidEmail()) {
            loginView.showEmailError();
            ok = false;
        }
        if (!bean.hasValidPassword()) {
            loginView.showPasswordError();
            ok = false;
        }
        if (!bean.hasUserType()) {                  
            loginView.getErrorMessage().setText("Seleziona un tipo di accesso.");
            loginView.getErrorMessage().setVisible(true);
            loginView.getErrorMessage().setManaged(true);
            ok = false;
        }
        if (!ok) return;

        
        try {
            logger.log(Level.INFO, "Tentativo di login per utente: {0}", bean.getUserType());

            if (ApplicationFacade.isLoginValid(bean)) {
                logger.log(Level.INFO, "Login riuscito per {0}", bean.getUserType());
                SessionManager.getInstance().setCredentials(bean);
                navigateToNextPage();
            } else {
                throw new WrongLoginCredentialsException("Email o password non validi");
            }

        } catch (WrongLoginCredentialsException e) {
            loginView.getErrorMessage().setText(e.getMessage());
            loginView.getErrorMessage().setVisible(true);
            loginView.getErrorMessage().setManaged(true);
            logger.warning("Errore di login: " + e.getMessage());
            ApplicationFacade.showErrorMessage("Accesso negato", "Credenziali errate", e.getMessage());

        } 
    }

    /* ---------------------------------------------------------- */
    private void navigateToNextPage() {
        navigationService.navigateToHomePage(navigationService,
                                             userType != null ? userType : "user");
    }

    private void goToRegistration() {
        navigationService.navigateToRegistration(navigationService,
                                                 userType != null ? userType : "user");
    }

    public VBox getRoot() {
        return loginView.getRoot();
    }
}