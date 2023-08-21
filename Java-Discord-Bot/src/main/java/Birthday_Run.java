

import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class Birthday_Run extends TimerTask{
    public static HashMap<String, Date>  BdayList = new HashMap<>();
    private MessageChannel chan;

    public void run() {
        LocalDate day = LocalDate.now();
        int currentMonth = day.getMonthValue();
        int currentDay = day.getDayOfMonth();
        for (String event : BdayList.keySet()) {
             Date Bday = BdayList.get(event);
            int currentMonth2 = day.getMonthValue();
            int currentDay2 = day.getDayOfMonth();
             LocalDate Bday2 = Bday.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if ((currentMonth == currentMonth2 && currentDay == currentDay2)) {
                chan.sendMessage("@everyone HEY EVERYONE ITS " + event + "'s Birthday today!").queue();
                chan.sendMessage("They're turning " + (Bday2.getYear()-Bday.getYear()) + " this year!!!!!!").queue();
                break;
            }
        }
    }
}
