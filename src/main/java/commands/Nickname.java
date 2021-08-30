package commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import utils.MessageException;

import java.util.List;

public class Nickname extends Command {


    public Nickname() {
        this.name = "nickname";
        this.aliases = new String[]{"rename", "bijnaam"};
        this.description = "Edits the nickname of a given person";
    }


    @Override
    public void run(String[] args, GuildMessageReceivedEvent e) throws Exception{
        Message m = e.getMessage();
        List<Member> mentionedmembers = m.getMentionedMembers();
        Member target;
        try {
            target = mentionedmembers.size() == 1 && args[0].startsWith("<@") ? mentionedmembers.get(0) : e.getGuild().getMemberById(args[0]);
        } catch (Exception exc) {
            throw new MessageException("Usage: !nickname <Member> <Nickname>");
        }
        if (args.length >= 2 && (target != null)) {
            try {
                StringBuilder sb = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    sb.append(args[i]).append(" ");
                }
                if (sb.toString().trim().length() <= 32) {
                    target.modifyNickname(sb.toString().trim()).queue();
                } else {
                    throw new MessageException("Nickname too long");
                }
            } catch (HierarchyException hexc) {
                throw new MessageException("Sorry, I can't change the nickname of my master");
            }
        }
    }
}
