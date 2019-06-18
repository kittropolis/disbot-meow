package discordbot;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;

public class Main {
	public static final Bot bot = new Bot();
	private static Map<Guild,ArrayList<Thread>> threads;
	public static void main(String[] args) throws Exception {
		String token = "\u004e\u0054\u006b\u0077\u004e\u006a\u0059\u0078\u004d\u0054\u006b\u0078\u004d\u0044\u0067\u0078\u004d\u007a\u006b\u0030\u004d\u006a\u0041\u0034\u002e\u0058\u0051\u006c\u0065\u004c\u0077\u002e\u0056\u0059\u0039\u006e\u007a\u0072\u0033\u0052\u0071\u0052\u0051\u0036\u0052\u0078\u004a\u0038\u0076\u004f\u005f\u0048\u0045\u0064\u0061\u0044\u0030\u0043\u0059";
		bot.setToken(token);
		bot.init();
		threads = new HashMap<Guild,ArrayList<Thread>>();
//		bot.addCustomCommand("command", "name", "regex", "help", (event, params) -> {});
		bot.addCustomCommand("Ping","ping","ping","Ping the bot", (event, params) -> {
			event.getChannel().sendMessage("Pong").queue();
		});
		bot.addCustomCommand("Stahp", "stahp", "stahp", "Stahp running actions", (event,params) -> {
			if (!Bot.hasPermission(Permission.ADMINISTRATOR, event.getMember())) {
				event.getChannel().sendMessage("Nice try " + event.getAuthor().getAsMention() + ", but I only listen to cool kids, like the server admins").queue();
			} else {
				for (Thread t: threads.get(event.getGuild())) {
					t.interrupt();
				}
			}
		});
		bot.addCustomCommand("Copy", "copy", "copy (.*)", "Copy message", (event,params) -> {
			if (!Bot.hasPermission(Permission.ADMINISTRATOR, event.getMember())) {
				event.getChannel().sendMessage("Nice try " + event.getAuthor().getAsMention() + ", but I only listen to cool kids, like the server admins").queue();
			} else {
				event.getChannel().sendMessage(params[0]).queue();
			}
		});
		bot.addCustomCommand("Spam", "spam", "spam (\\d+) (.*)", "Spam message", (event,params) -> {
			if (!Bot.hasPermission(Permission.ADMINISTRATOR, event.getMember())) {
				event.getChannel().sendMessage("Nice try " + event.getAuthor().getAsMention() + ", but I only listen to cool kids, like the server admins").queue();
			} else {
				if (!threads.containsKey(event.getGuild())) {
					threads.put(event.getGuild(), new ArrayList<Thread>());
				}
				Thread t = new Thread(() -> {
					for (int i = 0; i < Integer.parseInt(params[0]); i++) {
						event.getChannel().sendMessage(params[1]).queue();
					}
					threads.get(event.getGuild()).remove(Thread.currentThread());
				});
				t.start();
				threads.get(event.getGuild()).add(t);
			}
		});
		bot.addCustomCommand("Get Invite", "getinvite", "getinvite", "Get invite link", (event,params) -> {
			event.getChannel().sendMessage(event.getGuild().getDefaultChannel().createInvite().complete().getURL()).queue();
		});
		bot.addCustomCommand("Stop Bot", "stop", "stop", "Stop Bot", (event,params) -> {
			if (!Bot.hasPermission(Permission.ADMINISTRATOR, event.getMember())) {
				event.getChannel().sendMessage("Nice try " + event.getAuthor().getAsMention() + ", but I only listen to cool kids, like the server admins").queue();
			} else {
				System.exit(1);
			}
		});
		bot.addCustomCommand("Get Permissions", "getperms", "getperms(?: (?:(<@!?(\\d+)>)|(\\d+)))?", "Get permissions for user or role", (event,params) -> {
			if (!Bot.hasPermission(Permission.ADMINISTRATOR, event.getMember())) {
				event.getChannel().sendMessage("Nice try " + event.getAuthor().getAsMention() + ", but I only listen to cool kids, like the server admins").queue();
			} else {
				EmbedBuilder t = new EmbedBuilder();
				t.setColor(Color.HSBtoRGB((float)Math.random(), 1.0f, 1.0f));
				String s = "";
				String h = "";
				System.out.println(String.join("\t", params));
				if (params[0].equals("")) {
					for (Permission f: Permission.getPermissions(Permission.ALL_PERMISSIONS)) {
						s+=f.getName()+"\n";
					}
					h = "All Permissions";
				} else if (params[0].matches("<@!?(\\d+)>")) {
					for (Permission f: event.getGuild().getMemberById(params[1]).getPermissions()) {
						s+=f.getName()+"\n";
					}
					h = event.getGuild().getMemberById(params[1]).getEffectiveName()+"'s permissions";
				} else {
					for (Permission f: event.getGuild().getRoleById(params[0]).getPermissions()) {
						s+=f.getName()+"\n";
					}
					h = event.getGuild().getRoleById(params[2]).getName()+"'s permissions";
				}
				t.addField(h, s.trim(), false);
				event.getChannel().sendMessage(t.build()).queue();
			}
		});
		bot.addCustomCommand("Get Avatar","getavatar","getavatar <@!?(\\d+)>","Get users avatar", (event, params) -> {
			if (!Bot.hasPermission(Permission.ADMINISTRATOR, event.getMember())) {
				event.getChannel().sendMessage("Nice try " + event.getAuthor().getAsMention() + ", but I only listen to cool kids, like the server admins").queue();
			} else {
				event.getChannel().sendMessage(event.getGuild().getMemberById(params[0]).getUser().getAvatarUrl()).queue();
			}
		});
		/*
		bot.addCustomCommand("", "", "", "", (event,params) -> {
			if (!Bot.hasPermission(Permission.ADMINISTRATOR, event.getMember())) {
				event.getChannel().sendMessage("Nice try " + event.getAuthor().getAsMention() + ", but I only listen to cool kids, like the server admins and " + event.getJDA().getUserById(Bot.ME).getAsMention()).queue();
			} else {
				
			}
		});
		*/
		/*
		 * 
		 */
	}
	
	@SuppressWarnings("unused")
	private static int count(String string, String param) {
		int c = 0;
		while (string.length()>0) {
			int i = string.indexOf(param);
			if (i == -1) {
				string = "";
			} else {
				string = string.substring(i+param.length());
				c++;
			}
		}
		return c;
	}

	@SuppressWarnings("unused")
	private static Role roleFromNameOrId(String n, Guild g) {
		return n.matches("^-?\\d*")?g.getRoleById(n):g.getRolesByName(n, true).get(0);
	}

	@SuppressWarnings("rawtypes")
	public static Object getRandomFromList(List l) {
		if (l.size()>0) {
			return l.get((int)(Math.random()*l.size()));
		}
		return null;
	}
}