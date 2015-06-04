package cristiano.com.tvseriestoday;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Lavoro on 09/04/2015.
 */
public class Utility {

    public static String formatDateString(String date) {
        Date dateF;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            dateF = sdf.parse(date);
        } catch (ParseException e) {
           return "";
        }
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return sdf2.format(dateF);
    }



}
