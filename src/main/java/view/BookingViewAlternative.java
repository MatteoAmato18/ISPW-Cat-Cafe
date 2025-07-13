package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

public class BookingViewAlternative {

    private final VBox root = new VBox(20);

   
    private final TextField           dataField;
    private final Spinner<Integer>    oraHour;
    private final Spinner<Integer>    oraMin;

    private final TextField  numeroPartecipanti;
    private final TextField  nomePrenotazione;
    private final TextField  email;

    private final ComboBox<String> activityCombo = new ComboBox<>();

    private final Button confirmButton = new Button("Conferma");
    private final Button cancelButton  = new Button("Annulla");

    private Label err() { Label l=new Label(); l.getStyleClass().add("error-message"); l.setVisible(false); return l; }
    private final Label dataErr  = err();
    private final Label oraErr   = err();
    private final Label seatsErr = err();
    private final Label nameErr  = err();
    private final Label mailErr  = err();

    public BookingViewAlternative() {

        root.setPadding(new Insets(24));
        root.setAlignment(Pos.TOP_CENTER);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(8);
        grid.setAlignment(Pos.CENTER_LEFT);

        int r = -1;

        Label title = new Label("Prenotazione – modalità compatta");

        grid.add(new Label("Data (yyyy-MM-dd)"), 0, ++r);
        dataField = new TextField(); dataField.setPromptText("2025-06-01");
        grid.add(dataField, 1, r); grid.add(dataErr, 2, r);

        grid.add(new Label("Ora"), 0, ++r);
        oraHour = new Spinner<>(0,23,12); oraHour.setPrefWidth(70);
        oraMin  = new Spinner<>(0,45,0,15); oraMin.setPrefWidth(70);
        grid.add(new HBox(5, oraHour, new Label(":"), oraMin), 1, r);
        grid.add(oraErr, 2, r);

        grid.add(new Label("Partecipanti"), 0, ++r);
        numeroPartecipanti = new TextField(); numeroPartecipanti.setPrefWidth(80);
        grid.add(numeroPartecipanti, 1, r); grid.add(seatsErr, 2, r);

        grid.add(new Label("Titolo"), 0, ++r);
        nomePrenotazione = new TextField();
        grid.add(nomePrenotazione, 1, r); grid.add(nameErr, 2, r);

        grid.add(new Label("E-mail"), 0, ++r);
        email = new TextField();
        grid.add(email, 1, r); grid.add(mailErr, 2, r);

        grid.add(new Label("Attività"), 0, ++r);
        activityCombo.setPromptText("Seleziona un’attività");
        grid.add(activityCombo, 1, r);

        HBox buttons = new HBox(10, confirmButton, cancelButton);

        root.getChildren().addAll(title, grid, buttons);
    }

    
    public void loadActivities(List<String> names) {
        activityCombo.getItems().setAll(names);
    }
    public String getSelectedActivity() {
    	return activityCombo.getValue(); 
    	}

  
    public VBox   getRoot()          {
    	return root; 
    	}
    public Button getConfirmButton() {
    	return confirmButton;
    	}
    public Button getCancelButton()  {
    	return cancelButton; 
    	}


    public LocalDate getDate() {
        try { return LocalDate.parse(dataField.getText().trim()); }
        catch (DateTimeParseException _){ return null; }
    }
    public LocalTime getTime() { return LocalTime.of(oraHour.getValue(), oraMin.getValue()); }
    public int getParticipants(){
        try { return Integer.parseInt(numeroPartecipanti.getText().trim()); }
        catch(NumberFormatException _){ return 0; }
    }
    public String getNomePrenotazione(){ return nomePrenotazione.getText().trim(); }
    public String getConfirmationEmail(){ return email.getText().trim(); }

    
    public void hideAllErrors(){
    	dataErr.setVisible(false); oraErr.setVisible(false);
        seatsErr.setVisible(false); nameErr.setVisible(false); mailErr.setVisible(false);
        }
    public void setDataError(String m){
    	dataErr.setText(m); dataErr.setVisible(true);
    	}
    public void setOraError (String m){
    	oraErr .setText(m); oraErr .setVisible(true);
    	}
    public void setPartecipantiError(String m){
    	seatsErr.setText(m); seatsErr.setVisible(true);
    	}
    public void setNomeError(String m){ 
    	nameErr.setText(m); nameErr.setVisible(true);
    	}
    public void setEmailError(String m){ 
    	mailErr.setText(m); mailErr.setVisible(true);
    	}
}