package commands;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;

public abstract class Command {

    protected String name;
    protected String[] aliases = new String[0];
    protected String category = "Other";
    protected ArrayList<Long> bannedChannels = new ArrayList<>();
    protected String arguments = "";

    public abstract void run(String[] args, GuildMessageReceivedEvent e) throws Exception;

    public abstract String getDescription();

    public String getName()  {
        //if (name == null) throw new ExecutionControl.NotImplementedException("Command should have a name");
        return name;
    }

    public String[] getAliases() {
        return aliases;
    }

    public String getCategory() {
        return category;
    }

    public ArrayList<Long> getBannedChannels() {
        return bannedChannels;
    }

    public String getUsage(){
        return String.format("Usage: !%s %s\n%s", name, arguments, this.getDescription());
    }

    public boolean isCommandFor(String s) {
        if (s.equalsIgnoreCase(name)) {
            return true;
        }
        int ctr = 0;
        while (ctr < aliases.length && !s.equalsIgnoreCase(aliases[ctr])) {
            //System.out.println(String.format("%s: %s", s, aliases[ctr]));
            ctr++;
        }

        return ctr < aliases.length;
    }

    public static Long isLong(String s) {
        Long l = null;
        try {
            l = Long.parseLong(s);
        } catch (Exception ignored) {
        }
        return l;
    }
}
