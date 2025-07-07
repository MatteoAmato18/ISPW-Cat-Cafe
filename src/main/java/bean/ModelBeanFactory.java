package bean;



import java.time.LocalDate;
import java.util.List;


import dao.SessionManager;
import entity.Client;
import entity.Adoption;
import view.*;

public final class ModelBeanFactory {

    private ModelBeanFactory() { }            

    /* ------------------------------------------------------------------
     *  LOGIN BEAN –  versione “view principale”
     * ------------------------------------------------------------------ */
    public static LoginBean getLoginBean(LoginView view) {

        LoginBean bean = new LoginBean();
        bean.setEmail(view.getEmailField().getText());
        bean.setPassword(view.getPasswordField().getText());

        // RadioButton o toggle nella view principale (“user” / “staf”)
        if (view.getType().equals("user")) {
            bean.setUserType("user");
        } else  {
            bean.setUserType("staf");
        } // else null

        return bean;
    }

    /* ------------------------------------------------------------------
     *  LOGIN BEAN – versione “alternative view”
     * ------------------------------------------------------------------ */
    public static LoginBean getLoginBean(LoginAlternativeView view) {

        LoginBean bean = new LoginBean();
        bean.setEmail(view.getEmailField().getText());
        bean.setPassword(view.getPasswordField().getText());

        if (view.getClientLoginOption().isSelected()) {
            bean.setUserType("user");
        } else if (view.getReceptionistLoginOption().isSelected()) {
            bean.setUserType("staf");
        }

        return bean;
    }

    /* ------------------------------------------------------------------
     *  BEAN caricati dalla sessione (dopo login riuscito)
     * ------------------------------------------------------------------ */
    public static LoginBean loadLoginBean() {

        if (SessionManager.getInstance().getEmail() == null) return null;

        LoginBean bean = new LoginBean();
        bean.setEmail(SessionManager.getInstance().getEmail());
        bean.setPassword(SessionManager.getInstance().getPassword());
        bean.setUserType(SessionManager.getInstance().getType());
        return bean;
    }

    /* ------------------------------------------------------------------
     *  REGISTRAZIONE – bean di supporto
     * ------------------------------------------------------------------ */
    /* -------- view “normale” -------- */
    public static ClientRegistrationBean getClientRegistrationBean(RegistrationView v) {
        ClientRegistrationBean b = new ClientRegistrationBean();
        b.setFirstName(v.getFirstNameField().getText());
        b.setLastName(v.getLastNameField().getText());
        b.setEmail(v.getEmailField().getText());
        b.setPhoneNumber(v.getPhoneNumberField().getText());
        b.setPassword(v.getPasswordField().getText());
        b.setRepeatPassword(v.getRepeatPasswordField().getText());
        b.setUserType(v.getuserType());                     
        return b;
    }

    /* -------- view alternativa -------- */
    public static ClientRegistrationBean getClientRegistrationBean(RegistrationViewAlternative v) {
        ClientRegistrationBean b = new ClientRegistrationBean();
        b.setFirstName(v.getFirstNameField().getText());
        b.setLastName(v.getLastNameField().getText());
        b.setEmail(v.getEmailField().getText());
        b.setPhoneNumber(v.getPhoneNumberField().getText());
        b.setPassword(v.getPasswordField().getText());
        b.setRepeatPassword(v.getRepeatPasswordField().getText());
        b.setUserType(v.getSelectedUserType());
        return b;
    }
    public static BookingBean getBookingBean(BookingView v,Client user) {
        BookingBean b = new BookingBean();
        b.setTitle   (v.getNomePrenotazione());
        b.setDate (v.getDate());
        b.setTime(v.getTime());                
        b.setSeats   (v.getParticipants());
        b.setConfirmationEmail(v.getConfirmationEmail());
        String sel = v.getSelectedActivityName();   // oppure getSelectedActivity()
        b.setFreeActivities(List.of(sel)); 
        return b;
    }
    
    public static BookingBean getBookingBean(view.BookingViewAlternative v, Client user) {
        BookingBean b = new BookingBean();
        b.setTitle   (v.getNomePrenotazione());
        b.setDate    (v.getDate());
        b.setTime    (v.getTime());
        b.setSeats   (v.getParticipants());
        b.setConfirmationEmail(user == null ? null : user.getEmail());
        String sel = v.getSelectedActivity();   // oppure getSelectedActivity()
        b.setFreeActivities(List.of(sel)); 
        return b;
    }
    
    public static Adoption getRequestAdoptionBean(RequestAdoption a) {
        Adoption bean = new Adoption();
        bean.setName(a.getName());
        bean.setSurname(a.getSurname());
        String phoneNumber = a.getPhoneNumber();
        bean.setPhoneNumber(phoneNumber);

        bean.setEmail(a.getEmail());
        bean.setAddress(a.getAddress());
        bean.setNameCat(a.getSelectedCatName());
        bean.setStateAdoption(false); // default

        return bean;
    }
    public static ReviewBean getReviewBean(ReviewView v, Client user) {
        ReviewBean b = new ReviewBean();
        b.setDate(LocalDate.now());
        b.setTime(v.getTime());
        b.setEmail(user == null ? null : user.getEmail());
        b.setStars(v.getStars());
        b.setSpecialService(v.getSelectedService());
        b.setBody(v.getBody());
        return b;
    }

    /* ============================================================= */
    /*                         REVIEW – view alternativa             */
    /* ============================================================= */
    public static ReviewBean getReviewBean(ReviewAltView v, Client user) {
        ReviewBean b = new ReviewBean();
        b.setDate(LocalDate.now());
        b.setTime(v.getTime());
        b.setEmail(user == null ? null : user.getEmail());
        b.setStars(v.getStars());
        b.setSpecialService(v.getSelectedService());
        b.setBody(v.getBody());
        return b;
    }
    
}