package commands.roles;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import utils.DataHandler;

import java.util.Objects;

public class EditRoleAssign extends RoleCommand {

    public EditRoleAssign() {
        this.name = "editRoleAssign";
        this.category = "moderation";
        this.aliases = new String[]{"editRA"};
        this.arguments = "<category> sort {emoji|name|none} {compact|supercompact|normal}\n<category> <emoji> <name>\n<category> {title} <newtitle>";
    }


    @Override
    public void run(String[] args, GuildMessageReceivedEvent e) throws Exception {
        DataHandler dh = new DataHandler();
        long guildId = e.getGuild().getIdLong();
        if (args.length == 1){
            e.getChannel().sendMessage("Please supply what you want to edit: sort, title or an emoji which name you want to edit\n" + getUsage()).queue();
        } else if (args.length >= 3 && dh.getRoleCategories(guildId).contains(args[0])) {
            if (args[1].equalsIgnoreCase("sort")) {
                RoleAssignData data = dh.getRoleAssignData(guildId, args[0]);
                final Compacting compact;
                final Sorting sort;
                if (args[2].equalsIgnoreCase("emoji")) {
                    sort = Sorting.EMOJI;
                } else if (args[2].equalsIgnoreCase("name")) {
                    sort = Sorting.NAME;
                } else if (!args[2].equalsIgnoreCase("none")) {
                    e.getChannel().sendMessage(String.format("%s is not an valid sorting method", args[1])).queue();
                    return;
                } else {
                     sort = Sorting.NONE;
                }
                if (args.length == 4) {
                    if (args[3].equalsIgnoreCase("compact")) {
                        compact = Compacting.COMPACT;
                    } else if (args[3].equalsIgnoreCase("supercompact")) {
                        compact = Compacting.SUPER_COMPACT;
                    } else if (!args[3].equalsIgnoreCase("normal")) {
                        e.getChannel().sendMessage(String.format("%s is not an valid compacting method", args[1])).queue();
                        return;
                    } else {
                        compact = Compacting.NORMAL;
                    }
                } else {
                    compact = Objects.requireNonNullElse(data.getCompacting(), Compacting.NORMAL);
                }
                dh.setCompacting(guildId, args[0], compact, sort);
                if (data.getMessageId() != null) {
                    e.getGuild().getTextChannelById(data.getChannelId()).retrieveMessageById(data.getMessageId()).queue(m -> {
                        if (m != null) {
                            EmbedBuilder eb = getRoleEmbed(dh.getRoles(guildId, args[0]), args[0], sort, compact, data.getTitle());
                            m.editMessage(eb.build()).queue();
                        }
                    });
                }
            } else if (args[1].equalsIgnoreCase("title")) {
                StringBuilder name = new StringBuilder();
                for (int i = 2; i < args.length; i++) {
                    name.append(args[i]).append(" ");
                }
                dh.setTitle(guildId, category, name.toString().trim());
                e.getMessage().addReaction("✅").queue();
            } else if (hasEmoji(e.getMessage(), args[1])) {
                StringBuilder name = new StringBuilder();
                for (int i = 2; i < args.length; i++) {
                    name.append(args[i]).append(" ");
                }
                boolean succeeded = dh.editRoleName(guildId, category, args[1], name.toString().trim());
                if (succeeded) {
                    e.getMessage().addReaction("✅").queue();
                    RoleAssignData data = dh.getRoleAssignData(guildId, args[0]);
                    if (data.getMessageId() != null) {
                        e.getGuild().getTextChannelById(data.getChannelId()).retrieveMessageById(data.getMessageId()).queue(m -> {
                            if (m != null) {
                                EmbedBuilder eb = getRoleEmbed(dh.getRoles(guildId, args[0]), args[0], data.getSorting(), data.getCompacting(), data.getTitle());
                                m.editMessage(eb.build()).queue();
                            }
                        });
                    }
                } else {
                    e.getMessage().addReaction("❌").queue();
                    e.getChannel().sendMessage(String.format("No such emoji for category %s", args[0])).queue();
                }
            } else {
                e.getChannel().sendMessage(String.format("%s is not a valid emoji.\n%s", args[1], getUsage())).queue();
            }
        } else {
            e.getChannel().sendMessage("No valid category provided\n" + getUsage()).queue();
        }
    }
}

