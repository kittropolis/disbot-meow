package discordbot;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class Bot {
	public static final String SELF = "427336454751125514";
	private Map<String,CustomMessageHandler> messageHandlers;
	private Set<CustomCommand> commands;
	String token;
	JDA api;
	public Bot() {
		commands = new HashSet<CustomCommand>();
		messageHandlers = new HashMap<String,CustomMessageHandler>();
	}
	public Bot(String token) {
		this();
		this.token = token;
	}
	public void init() throws Exception {
		api = new JDABuilder(AccountType.BOT).setToken(token).buildAsync();
		System.out.println("Logged in!");
		addCustomListener((evnt) -> {
			if (evnt instanceof MessageReceivedEvent) {
				MessageReceivedEvent event = (MessageReceivedEvent)evnt;
				for (String s: messageHandlers.keySet()) {
					messageHandlers.get(s).call(event);
				}
//				for (String s: event.getMessage().getContentRaw().split("\n")) {
					String s = event.getMessage().getContentRaw();
					for (CustomCommand c: commands) {
						Matcher m = c.getParameters().matcher(s);
						while (m.find()) {
							String[] params = new String[m.groupCount()];
							for (int i = 0; i < m.groupCount(); i++) {
								params[i] = m.group(i+1);
								params[i] = params[i] == null?"":params[i];
							}
							c.getHandler().call(event, params);
						}
					}
//				}
			}
		});
		addCustomCommand("Help", "help", "help(?: (.*))?", "Show help", (event, params) -> {
			EmbedBuilder t = new EmbedBuilder();
			t.setColor(Color.HSBtoRGB((float)Math.random(), 1.0f, 1.0f));
			for (CustomCommand c: commands) {
				if (params[0].equals("") || params[0].substring(0,Math.min(c.getCommand().length()+1,params[0].length())).equalsIgnoreCase(c.getCommand())) {
					t.addField(c.getName(), "`"+c.getParameters().pattern()+"`\n"+c.getHelpString(), false);
				}
			}
			if (t.isEmpty()) {
				t.addField("Fail", "No help found", false);
			}
			event.getChannel().sendMessage(t.build()).queue();
		});
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getToken() {
		return this.token;
	}
	public void addCustomListener(CustomListener listener) throws InitBotException {
		if (api == null) {
			throw new InitBotException("Initialize Bot before using it.");
		}
		api.addEventListener(new CustomEventListener() {
			public void onEvent(Event event) {
				listener.call(event);
			}
		});
	}
	public void setGame(String game) throws InitBotException {
		if (api == null) {
			throw new InitBotException("Initialize Bot before using it.");
		}
		api.getPresence().setGame(Game.playing(game));
	}
	public static boolean hasPermission(Permission permission, Member member) {
		if (!member.hasPermission(permission) && !member.getUser().getId().equals(Bot.SELF)) {
			return false;
		}
		return true;
	}
	public void sendMessage(MessageChannel channel, String message) {
		channel.sendMessage(message).queue();
	}
	public void sendMessage(MessageChannel channel, MessageEmbed message) {
		channel.sendMessage(message).queue();
	}
	public void addCustomCommand(String name, String command, String pattern, String helpString, CustomCommandHandler handler) {
		commands.add(new CustomCommand(name, command, Pattern.compile("^!"+pattern,Pattern.CASE_INSENSITIVE|Pattern.MULTILINE), handler, helpString));
	}
	public void addMessageListener(String id, CustomMessageHandler handler) {
		messageHandlers.put(id, handler);
	}
	public void removeMessageListener(String id) {
		messageHandlers.remove(id);
	}
	public boolean hasMessageListener(String id) {
		return messageHandlers.containsKey(id);
	}
	public TextChannel getChannel(Guild guild, String name) {
//		System.out.println(guild.getTextChannels());
//		System.out.println(name);
		return guild.getTextChannelsByName(name, true).get(0);
	}
}
