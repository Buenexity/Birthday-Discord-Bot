import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import javax.security.auth.login.LoginException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Birthday_Bot1 extends ListenerAdapter{
    private static HashMap<String, Date> Birthday_List = new HashMap<>();
    private HashMap<String, Date> pendingBirthdays = new HashMap<>();
    private static MessageChannel birthdayChannel;
    private JDA jda;

    public static void main(String[] args) throws LoginException, InterruptedException
    {
        //Setting Bot w/ User Birthdays
        Birthday_Bot1 bot = new Birthday_Bot1();
        final String token = "REPLACE BOT_TOKEN";
        JDA jda = JDABuilder.createDefault(token).enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS)
                .addEventListeners(bot)
                .build();
        bot.setJDA(jda);
        bot.loadFile();

        //Timer
        Timer timer = new Timer("Timer");
        // 12 hours in milliseconds
        long delay = 12 * 60 * 60 * 1000;

      ; //Unique Server Id - CHANGE THIS TO YOUR SERVER ID
        Guild guild = jda.awaitReady().getGuildById("REPLACE SERVER_ID");
        //error checking
        if (guild != null) {
            //Unique channel id - CHANGE THIS THE CHANNEL ID YOU WANT THE SERVER TO SEND MESSAGE TO
            TextChannel birthdayChannel = guild.getTextChannelById("REPLACE CHANNEL_ID");
            if (birthdayChannel != null) {
                bot.setBirthdayChannel(birthdayChannel);
            } else {
                System.err.println("Failed to find the specified TextChannel with ID: CHANNEL_ID");
            }
             } else {
            System.err.println("Failed to find the specified Guild with ID: GUILD_ID");
            }
        //TimerTask object set to timer wait time
        TimerTask s = new TimerTask() {

            //This will actively check every certain hours to see if it's someone's birthday
            @Override
            public void run() {
                LocalDate day = LocalDate.now();
                int currentMonth = day.getMonthValue();
                int currentDay = day.getDayOfMonth();
                boolean isBirthdayToday = false; // Flag to track if any birthday matches the current date

                for (String event : Birthday_List.keySet()) {
                    Date Bday = Birthday_List.get(event);
                    LocalDate Bday2 = Bday.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    int birthdayMonth = Bday2.getMonthValue();
                    int birthdayDay = Bday2.getDayOfMonth();

                    if (currentMonth == birthdayMonth && currentDay == birthdayDay) {
                        // Ensure that birthdayChannel is not null before using it
                        if (birthdayChannel != null) {
                            birthdayChannel.sendMessage("@everyone ITS " + event + "'s Birthday today!").queue();
                            birthdayChannel.sendMessage("They're turning " + (day.getYear() - Bday2.getYear()) + " this year!!!!!!").queue();
                        }
                        isBirthdayToday = true; // Set the flag to true if any birthday matches the current date
                         }}}};
                        timer.schedule(s, delay, delay);
    }

    //Setter functions
    public void setBirthdayChannel(TextChannel e)
    {
        this.birthdayChannel = e;
    }

    public void setJDA(JDA jda) {
        this.jda = jda;
    }


    /*
        Description:
        This code grabs the list of birthdays within the Birthday file and stores them into the Birthday_list
        hashmap
        What to change:
        Change the file location to your txt file to keep track of all birthdays
    */

    public void loadFile() {
        Properties properties = new Properties();

        try {
            properties.load(new FileInputStream("REPLACE FILE_LOC"));

            for (String key : properties.stringPropertyNames()) {
                String dateString = properties.getProperty(key);
                Date date = parseDate(dateString); // Parse the date string into a Date object
                Birthday_List.put(key, date);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Convert String to appropriate Date object
    //Will be used to turn string objects within file back to object files
    private Date parseDate(String dateString) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd MM yyyy");
        try {
            return formatter.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }




    // Listener object method which looks for Birthday commands in server
    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        String userId = e.getAuthor().getId();
        String[] splits = e.getMessage().getContentRaw().split(" ");
        if (splits[0].equals("!print"))
        {
            printAllBirthdays(e.getChannel());
        }

        //Limited to all commands starting with !register
        else if (splits[0].startsWith("!register"))
        {
            //User can re-enter his birthday
            if (Birthday_List.containsKey(e.getAuthor().getName()))
            {
                e.getChannel().sendMessage("Looks like you've been registered already").queue();
                return;
            }

            e.getChannel().sendMessage("This is a command to register your birthday!").queue();

            //Check for Messages where user is entering !register DD MM YYY
            if (splits.length == 4 && Birthday_List.containsKey(userId) == false) {
                int day = Integer.parseInt(splits[1]);
                int month = Integer.parseInt(splits[2]);
                int year = Integer.parseInt(splits[3]);
                boolean isCorrectBirthday = Birthday_Check(day, month, year);
                //Birthday is within bounds
                if (isCorrectBirthday)
                {
                    Date birthday = createDate(year, month, day);
                    String formattedBirthday = formatDate(birthday);

                    e.getChannel().sendMessage("Your birthday: " + formattedBirthday + ". Is this correct? (yes/no)").queue();
                    pendingBirthdays.put(e.getAuthor().getId(), birthday);
                }
                else
                {
                    e.getChannel().sendMessage("Looks like your input was incorrect. Please provide a valid birthday (DD MM YYYY).").queue();
                }}}
            //Verifying User response only to those within the pending birthday list
            //This avoids other users affecting the verification process
            else if (pendingBirthdays.containsKey(userId)) {
                if (splits[0].equalsIgnoreCase("yes")) {
                    Properties properties = new Properties();
                    Date pendingBirthday = pendingBirthdays.get(userId);
                    String birthday = formatDate(pendingBirthday);

                    Birthday_List.put(e.getAuthor().getName(), pendingBirthday);

                    // Convert the Date object to a String representation before storing it
                    String formattedDate = formatDate(pendingBirthday);
                    properties.put(e.getAuthor().getName(), formattedDate);
                    //Output information to unique file destination - must create a file location
                    try
                    {
                    properties.store(new FileOutputStream("REPLACE FIL_LOC"), null);
                    } 
                    catch (IOException exception)
                    {
                    // Handle the exception here, e.g., print an error message
                    System.err.println("Error writing to file: " + exception.getMessage());
                    exception.printStackTrace();
                    }

                    pendingBirthdays.remove(userId);

                     e.getChannel().sendMessage("Username: " + e.getAuthor().getName() + " Birthday: " + birthday + " has been added to the list.").queue();
                    }
                    else if (splits[0].equalsIgnoreCase("no"))
                    {
                    pendingBirthdays.remove(userId);
                    e.getChannel().sendMessage("Your birthday registration has been canceled. Please provide a valid birthday using the !register command.").queue();
                    }
                    else
                    {
                    e.getChannel().sendMessage("Please respond with 'yes' or 'no' to verify your birthday.").queue();
                    }}}

    //Check if birthday is within appropriate bounds
    public boolean Birthday_Check(int day, int month, int year) {
        // Check if the year is a reasonable value
        return (year >= 1900 && month >= 1 && month <= 12 && day >= 1 && day <= 31);
    }

    //Print the list of birthdays within the hashmap
    public void printAllBirthdays(MessageChannel e) {
        // Check if the Birthday_List is empty
        if (Birthday_List.isEmpty())
            e.sendMessage("No birthdays have been registered yet.").queue();
            return;
        }

        // Create a DateTimeFormatter to format the birthday in the pattern "dd MM yyyy"
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MM yyyy");
        SimpleDateFormat dayFormat = new SimpleDateFormat("MM-dd-yyyy");

        // Loop through the entries in the Birthday_List
        for (Map.Entry<String, Date> entry : Birthday_List.entrySet()) 
        {
            Date birthday = entry.getValue();
            String Day1 = dayFormat.format(birthday);
            e.sendMessage(entry.getKey() + "'s Birthday: " + Day1).queue();
        }
    }

    // Create a Date object with the specified year, month, and day
    private Date createDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day); 
        return calendar.getTime();
    }
    //Formate Date object to DD-MM-YYY
    public String formatDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd MM yyyy");
        return formatter.format(date);
    }
}
