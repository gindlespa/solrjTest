/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package indexbuilder41.Tests;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import net.sourceforge.jtds.jdbc.DateTime;

/**
 *
 * @author murphy
 */
public class checkdate {
    public static void main(String[] args)
    {
        String d = "2005-11-01T18:03:36Z\r\n";
        String dp = d.trim().toUpperCase().replace("Z", "").replaceAll("\\s", "");
        System.out.println(dp);
        LocalDateTime date = LocalDateTime.parse(dp, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String year = String.format("%04d", date.getYear());
        String month = String.format("%02d", date.getMonthValue());
        String day = String.format("%02d", date.getDayOfMonth());
                
        System.out.println(year+month+day);
    }
    
}
