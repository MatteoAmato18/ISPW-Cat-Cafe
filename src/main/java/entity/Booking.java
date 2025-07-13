package entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;


public class Booking {

   
    private int             id;
    private String          title;
    private LocalDate       date;
    private LocalTime       time;
    private int             seats;
    private String          email;
    private List<String>    freeActivities = List.of();  
    private BookingStatus   status         = BookingStatus.PENDING;

    

    /* ---------------- getter/setter ---------------- */
    public int          getId()                     { 
    	return id;
}
    public void         setId(int id)               {
    	this.id = id; 
    	}

    public String       getTitle()                  {
    	return title;
    	}
    public void         setTitle(String title)      {
    	this.title = title; 
    	}

    public LocalDate    getDate()                   {
    	return date; 
    	}
    public void         setDate(LocalDate date)     {
    	this.date = date; 
    	}

    public LocalTime    getTime()                   {
    	return time; 
    	}
    public void         setTime(LocalTime time)     {
    	this.time = time; 
    	}

    public int          getSeats()                  {
    	return seats; 
    	}
    public void         setSeats(int seats)         { 
    	this.seats = seats;
    	}

    public String       getConfirmationEmail()      {
    	return email; 
    	}
    public void         setConfirmationEmail(String email) {
    	this.email = email; 
    	}

    public BookingStatus getStatus()                {
    	return status; 
    	}
    public void          setStatus(BookingStatus s) {
    	this.status = s; 
    	}

    
    public List<String> getFreeActivities()         {
    	return Collections.unmodifiableList(freeActivities);
    	}
    public void         setFreeActivities(List<String> list) {
        this.freeActivities = list ;
    }

    @Override
    public String toString() {
        return "Booking[" + id + ", " + title + ", " + date + " " + time +
               ", " + seats + ", " + status + ']';
    }
}