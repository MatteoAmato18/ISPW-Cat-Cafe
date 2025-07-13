package controller_grafici;

import bean.BookingBean;
import bean.ModelBeanFactory;
import controller_applicativi.BookingService;
import entity.Activity;

import javafx.scene.layout.VBox;
import view.BookingView;

import java.util.List;

/**
 * Controller GUI per la view “standard” di prenotazione.
 */
public class BookingController {

    private final NavigationService nav;
    private final String            typeOfLogin;

    private final BookingView    view   = new BookingView();
    private final BookingService service = new BookingService();
    

    public BookingController(NavigationService nav, String typeOfLogin) {
        this.nav         = nav;
        this.typeOfLogin = typeOfLogin;
        addEventHandlers();
        loadAvailableActivities();
    }


    private void addEventHandlers() {
        view.getConfirmButton().setOnAction(_ -> handleConfirm());
        view.getCancelButton() .setOnAction(_ -> nav.navigateToHomePage(nav, typeOfLogin));
    }

    
    private void handleConfirm() {

        BookingBean bean = ModelBeanFactory.getBookingBean(view);

        /* --- mini-check di PRESENZA per UX --- */
        boolean ok = true; view.hideAllErrors();
        if (bean.getTitle() == null || bean.getTitle().isBlank())
            {
        	view.setNomeError("Titolo obbligatorio"); ok = false; 
        	}
        if (bean.getDate() == null || bean.getTime() == null)
            {
        	view.setDataError("Data/ora mancanti"); ok = false;
        	}
        if (bean.getSeats() <= 0)
            {
        	view.setPartecipantiError("Inserisci i posti"); ok = false; 
        	}
        if (!ok) return;

        
        String sel = view.getSelectedActivityName();
        bean.setFreeActivities(sel == null ? List.of() : List.of(sel));

        
        switch (service.book(bean)) {
            case "success"          -> nav.navigateToHomePage(nav, typeOfLogin);
            case "error:duplicate"  -> view.setDataError("Prenotazione già presente quel giorno");
            case "error:validation" -> view.setNomeError("Dati non validi");
            default                 -> view.setNomeError("Errore di sistema, riprova");
        }
    }

    
    private void loadAvailableActivities() {
        List<Activity> acts = service.getAvailableActivities();
        view.addActivityRadios(acts.stream().map(Activity::getName).toList());
    }

    
    public VBox getRoot() { return view.getRoot(); }
}