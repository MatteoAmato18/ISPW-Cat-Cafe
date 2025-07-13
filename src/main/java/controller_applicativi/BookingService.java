package controller_applicativi;

import bean.BookingBean;
import dao.*;
import entity.*;
import facade.ApplicationFacade;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.*;

public class BookingService {

    private static final Logger LOG = Logger.getLogger(BookingService.class.getName());

    private final GenericDao<Booking>bookingDao  =
             DaoFactory.getInstance().getBookingDao();

    
    /**
     * @return "success" | "error:validation" | "error:duplicate" | "error:db"
     */
    public String book(BookingBean bean) {

        
        if (bean.getTitle() == null || bean.getTitle().isBlank()
            || bean.getDate()  == null || bean.getTime() == null
            || bean.getSeats() <= 0 || bean.getSeats() > 50
            || bean.getConfirmationEmail() == null || bean.getConfirmationEmail().isBlank()
            || bean.getDate().isBefore(LocalDate.now()))
        {
            return "error:validation";
        }

        

        Booking b = new Booking();
        b.setTitle            (bean.getTitle());
        b.setDate             (bean.getDate());
        b.setTime             (bean.getTime());
        b.setSeats            (bean.getSeats());
        b.setConfirmationEmail(bean.getConfirmationEmail());
        b.setFreeActivities   (bean.getFreeActivities());

        
        try {
            bookingDao.create(b);                         
            ApplicationFacade.sendBookingReceivedEmail(b);
            return "success";

        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "DB error durante insert booking", ex);
            return "error:db";
        }
    }

    public List<Activity> getAvailableActivities() {
    	GenericDao<Activity> activityDao = DaoFactory.getInstance().getActivityDao();
        return activityDao.readAll();
    }
}