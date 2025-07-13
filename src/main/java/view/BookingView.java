package view;

import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.IntStream;


public class BookingView {

   
    private final VBox             root = new VBox(15);

    private final DatePicker       data = new DatePicker();
    private final ComboBox<LocalTime> ora = new ComboBox<>();
    private final TextField        numeroPartecipanti = new TextField();
    private final TextField        nomePrenotazione   = new TextField();
    private final TextField        email              = new TextField();

    
    private final VBox         activitySection = new VBox(8);
    private final ToggleGroup  activityGroup   = new ToggleGroup();   

    
    private final Button confirmButton = new Button("Conferma");
    private final Button cancelButton  = new Button("Annulla");

   
    private Label makeErr() {
        Label l = new Label();
        l.getStyleClass().add("error-message");
        l.setVisible(false);
        return l;
    }
    private final Label dataErr  = makeErr();
    private final Label oraErr   = makeErr();
    private final Label seatErr  = makeErr();
    private final Label nameErr  = makeErr();
    private final Label mailErr  = makeErr();

    /* ======================================================= */
    public BookingView() {
        root.setPrefSize(1280,720);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding:32;");

        Label title = new Label("Prenota un tavolo immerso tra i gatti 🐾");

        
        populateTimeBox();

       
        Label activityTitle = new Label("Attività disponibili:");
        activitySection.getChildren().add(activityTitle);

        
        root.getChildren().addAll(
                title,
                new Label("Giorno"), data,  dataErr,
                new Label("Ora di arrivo"), ora, oraErr,
                new Label("Numero partecipanti"), numeroPartecipanti, seatErr,
                new Label("Nome prenotazione"), nomePrenotazione, nameErr,
                new Label("E-mail di conferma"), email, mailErr,
                new Separator(), activitySection,
                confirmButton, cancelButton
        );
    }

 
    public VBox   getRoot()  { 
    	return root; 
    	}
    public Button getConfirmButton(){
    	return confirmButton; 
    	}
    public Button getCancelButton(){
    	return cancelButton;
    	}

  
    public LocalDate  getDate()  {
    	return data.getValue();  
    	}
    public LocalTime  getTime()  {
    	return ora.getValue();   
    	}
    public int        getParticipants()    {
        try { 
        	return Integer.parseInt(numeroPartecipanti.getText().trim()); 
        	}
        catch (NumberFormatException _) {
        	return 0; 
        	}
    }
    public String     getNomePrenotazione(){
    	return nomePrenotazione.getText().trim(); 
    	}
    public String     getConfirmationEmail(){
    	return email.getText().trim(); 
    	}

    
    public String getSelectedActivityName() {
        Toggle sel = activityGroup.getSelectedToggle();
        return sel == null ? null : ((RadioButton) sel).getText();
    }

    
    public void hideAllErrors() {
    	dataErr.setVisible(false); oraErr.setVisible(false);
        seatErr.setVisible(false); nameErr.setVisible(false); mailErr.setVisible(false);
        }
    public void setDataError(String m){
    	dataErr.setText(m); dataErr.setVisible(true);
    	}
    public void setOraError (String m){
    	oraErr .setText(m); oraErr .setVisible(true);
    	}
    public void setPartecipantiError(String m){
    	seatErr.setText(m); seatErr.setVisible(true);}
    public void setNomeError(String m){ nameErr.setText(m); nameErr.setVisible(true);
    }
    public void setEmailError(String m){
    	mailErr.setText(m); mailErr.setVisible(true);
    	}

  
    
    public void addActivityRadios(List<String> activityNames) {
        for (String name : activityNames) {
            RadioButton rb = new RadioButton(name);
            rb.setToggleGroup(activityGroup);     
            activitySection.getChildren().add(rb);
        }
    }

  
    private void populateTimeBox() {
        var times = IntStream.rangeClosed(32,95)   
                .mapToObj(i -> LocalTime.of(i/4,(i%4)*15))
                .toList();
        ora.setItems(FXCollections.observableArrayList(times));
        ora.setButtonCell(timeCell()); ora.setCellFactory(_->timeCell());
    }
    private ListCell<LocalTime> timeCell(){ return new ListCell<>() {
        @Override protected void updateItem(LocalTime t, boolean empty){
            super.updateItem(t,empty); setText(empty||t==null?"":t.toString());
        }};}
}