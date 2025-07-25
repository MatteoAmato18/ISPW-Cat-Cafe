package controller_grafici;

import entity.Adoption;
import bean.ModelBeanFactory;
import controller_applicativi.RequestAdoptionController;
import dao.CatDaoDB;
import dao.DatabaseConnectionManager;
import entity.Cat;
import entity.Client;
import facade.ApplicationFacade;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import view.RequestAdoption;
import java.util.List;
import java.util.logging.Logger;

public class RequestAdoptionGUIController {
    private static final Logger LOG = Logger.getLogger(RequestAdoptionGUIController.class.getName());

    /* ------------------------------------------------------------ */
    private final NavigationService nav;
    private final RequestAdoption view;
    private final String typeOfLogin;
    private final RequestAdoptionController request = new RequestAdoptionController();
    private final CatDaoDB catDAO;



    public RequestAdoptionGUIController(NavigationService nav, String typeOfLogin) {
        this.nav = nav;
        this.view = new RequestAdoption();
        this.typeOfLogin = typeOfLogin;
        this.catDAO = new CatDaoDB(DatabaseConnectionManager.getConnection());
        loadCatsIntoComboBox();
        addEventHandlers();
    }
    /* -------------------------- eventi GUI ---------------------- */
    private void addEventHandlers() {
        view.getConferma().setOnAction(_ -> handleConfirm());
        view.getAnnulla().setOnAction(_ -> handleCancel());
        view.getModifica().setOnAction(_ -> handleModify());
    }
    private void handleConfirm() {
        view.hideAllErrors();

        Adoption bean;
        try {
            bean = ModelBeanFactory.getRequestAdoptionBean(view);
        } catch (IllegalArgumentException e) {
            view.setTelefonoError(e.getMessage());
            return;
        }
        boolean ok = true;

        if (!bean.hasValidName()) {
            view.setNomeError("Nome obbligatorio");
            ok = false;
        }
        if (!bean.hasValidSurname()) {
            view.setCognomeError("Cognome obbligatorio");
            ok = false;

        }
        if (!bean.hasValidPhoneNumber()) {
            view.setTelefonoError("Numero di telefono non valido (min 10 cifre)");
            ok = false;

        }
        if (!bean.hasValidEmail()) {
            view.setEmailError("Email non valida");
            ok = false;

        }
        if (!bean.hasValidAddress()) {
            view.setIndirizzoError("Indirizzo obbligatorio");
            ok = false;

        }
        if (bean.getNameCat() == null || bean.getNameCat().isBlank()) {
            view.setNomeGattoError("Seleziona un gatto");
            ok = false;
        }

        if (!ok) return;          // blocca se ci sono errori


        Client currentUser = ApplicationFacade.getUserFromLogin(); // utente loggato

        if (currentUser == null) {
            view.setNomeError("Utente non loggato - effettua il login");
            return; // Interrompe l'esecuzione del metodo
        }

        String esito = request.requestAdoption(bean);

        switch (esito) {
            case "success" -> {
                LOG.info("Richiesta adozione inviata con successo");
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Successo");
                alert.setHeaderText(null);
                alert.setContentText("Richiesta adozione inviata con successo!");
                alert.showAndWait();
                nav.navigateToHomePage(nav, typeOfLogin);        // torna alla home
            }
            case "error:duplicate" ->
                    view.setNomeGattoError("Hai già una richiesta per questo gatto");
            case "error:validation" ->
                    view.setNomeError("Dati non validi - ricontrolla i campi");
            case "error:database_error" ->
                    view.setNomeError("Errore di sistema. Riprova più tardi");
            default ->
                    view.setNomeError("Errore sconosciuto");
        }
    }
    private void loadCatsIntoComboBox() {
        List<Cat> cats = catDAO.readAdoptableCats();
        populateCatNames(cats);
    }
    private void populateCatNames(List<Cat> cats) {
        List<String> catNames = cats.stream()
                .map(Cat::getNameCat)
                .toList();
        view.getComboBoxCatName().setItems(FXCollections.observableArrayList(catNames));
        view.getComboBoxCatName().setPromptText("Seleziona un gatto");
    }

    /* ----------------------- annulla ---------------------------- */
    private void handleCancel() {
        nav.navigateToHomePage(nav, typeOfLogin);
    }
    /* -------------------- root per il NavigationManager --------- */
    public VBox getRoot() { return view.getRoot(); }

    /* ----------------------- modifica ---------------------------- */

    private void handleModify() {
        nav.navigateToHomePage(nav, typeOfLogin);
    }

}