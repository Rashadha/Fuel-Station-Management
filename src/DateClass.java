import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateClass
{
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private LocalDateTime now = LocalDateTime.now();
    public String displayTodayDate()
    {
        return dtf.format(now);
    }
}
