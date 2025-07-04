package controller_grafici;

import javafx.scene.layout.VBox;

import java.util.logging.Logger;

import bean.BookingBean;
import bean.ModelBeanFactory;
import controller_applicativi.BookingService;
import entity.Client;
import facade.ApplicationFacade;

import view.BookingView;

public class BookingController {

    private static final Logger lOG =
            Logger.getLogger(BookingController.class.getName());

	

    /* ------------------------------------------------------------ */
    private final NavigationService nav;
    private final BookingView   view;
    private final String typeOfLogin;
    private final BookingService    service = new BookingService();

    /* ------------------------------------------------------------ */
    public BookingController(NavigationService nav,String typeOfLogin) {
        this.nav  = nav;
        this.view = new BookingView();
        this.typeOfLogin=typeOfLogin;
        addEventHandlers();
    }

    /* -------------------------- eventi GUI ---------------------- */
    private void addEventHandlers() {
        view.getConfirmButton().setOnAction(_ -> handleConfirm());
        view.getCancelButton() .setOnAction(_ -> handleCancel());
    }

    /* --------------------- conferma prenotazione ---------------- */
    private void handleConfirm() {

        BookingBean bean = ModelBeanFactory.getBookingBean(view);
       
        /* --- validazione GUI (solo con metodi del bean) --------- */
        boolean ok = true;
        view.hideAllErrors();

        if (!bean.hasValidTitle()) {
            view.setNomeError("Nome prenotazione obbligatorio");
            ok = false;
        }
        if (!bean.hasValidDates()) {
            view.setDataError("Controlla la date (prenotazione ≥ data odierna)");
            ok = false;
        }
        if (!bean.hasValidSeats()) {
            view.setPartecipantiError("Numero posti > 0 (50)");
            ok = false;
        }

        if (!ok) return;          // blocca se ci sono errori di GUI

        /* --- business ------------------------------------------- */
        Client currentUser =
                ApplicationFacade.getUserFromLogin();       // utente loggato

        String esito = service.book(currentUser, bean);

        switch (esito) {

            case "success" -> {
                lOG.info("Prenotazione salvata con successo");
                nav.navigateToHomePage(nav, "user");                 // torni alla home
            }

            case "error:duplicate" ->
                view.setDataError("Hai già una prenotazione per quel giorno");

            case "error:validation" ->
                view.setNomeError("Dati non validi – ricontrolla i campi");

            case "error:database_error" ->
                view.setNomeError("Errore di sistema. Riprova più tardi");

            default ->
                view.setNomeError("Errore sconosciuto");
        }
    }

    /* ----------------------- annulla ---------------------------- */
    private void handleCancel() {
        nav.navigateToHomePage(nav, typeOfLogin);
    }

    /* -------------------- root per il NavigationManager --------- */
    public VBox getRoot() { return view.getRoot(); }
}