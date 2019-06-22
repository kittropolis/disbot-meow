package discordbot;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.requests.restaction.pagination.MessagePaginationAction;

public class Main {
	public static final Bot bot = new Bot();
	private static Map<Guild, ArrayList<Thread>> threads;

	@SuppressWarnings("rawtypes")
	public static Object getRandomFromList(final List l) {
		if (l.size() > 0) {
			return l.get((int) (Math.random() * l.size()));
		}
		return null;
	}

	public static void main(final String[] args) throws Exception {
		final String token = "\u004e\u0054\u006b\u0077\u004e\u006a\u0059\u0078\u004d\u0054\u006b\u0078\u004d\u0044\u0067\u0078\u004d\u007a\u006b\u0030\u004d\u006a\u0041\u0034\u002e\u0058\u0051\u006c\u0065\u004c\u0077\u002e\u0056\u0059\u0039\u006e\u007a\u0072\u0033\u0052\u0071\u0052\u0051\u0036\u0052\u0078\u004a\u0038\u0076\u004f\u005f\u0048\u0045\u0064\u0061\u0044\u0030\u0043\u0059";
		Main.bot.setToken(token);
		Main.bot.init();
		Main.threads = new HashMap<>();
		// bot.addCustomCommand("command", "name", "regex", "help", (event, params) ->
		// {});
		Main.bot.addCustomCommand("Ping", "ping", "ping", "Ping the bot", (event, params) -> {
			Main.bot.sendMessage(event.getChannel(), "Pong");
		});
		Main.bot.addCustomCommand("Stahp", "stahp", "stahp", "Stahp running actions", (event, params) -> {
			if (!Bot.hasPermission(Permission.ADMINISTRATOR, event.getMember())) {
				Main.bot.sendMessage(event.getChannel(), "Nice try " + event.getAuthor().getAsMention()
						+ ", but I only listen to cool kids, like the server admins");
			} else {
				for (final Thread t : Main.threads.get(event.getGuild())) {
					t.interrupt();
				}
			}
		});
		Main.bot.addCustomCommand("Copy", "copy", "copy (.*)", "Copy message", (event, params) -> {
			if (!Bot.hasPermission(Permission.ADMINISTRATOR, event.getMember())) {
				Main.bot.sendMessage(event.getChannel(), "Nice try " + event.getAuthor().getAsMention()
						+ ", but I only listen to cool kids, like the server admins");
			} else {
				Main.bot.sendMessage(event.getChannel(), params[0]);
			}
		});
		Main.bot.addCustomCommand("Spam", "spam", "spam (\\d+) (.*)", "Spam message", (event, params) -> {
			if (!Bot.hasPermission(Permission.ADMINISTRATOR, event.getMember())) {
				Main.bot.sendMessage(event.getChannel(), "Nice try " + event.getAuthor().getAsMention()
						+ ", but I only listen to cool kids, like the server admins");
			} else {
				if (!Main.threads.containsKey(event.getGuild())) {
					Main.threads.put(event.getGuild(), new ArrayList<Thread>());
				}
				final Thread t = new Thread(() -> {
					for (int i = 0; i < Integer.parseInt(params[0]); i++) {
						Main.bot.sendMessage(event.getChannel(), params[1]);
					}
					Main.threads.get(event.getGuild()).remove(Thread.currentThread());
				});
				t.start();
				Main.threads.get(event.getGuild()).add(t);
			}
		});
		Main.bot.addCustomCommand("Get Invite", "getinvite", "getinvite", "Get invite link", (event, params) -> {
			Main.bot.sendMessage(event.getChannel(),
					event.getGuild().getDefaultChannel().createInvite().complete().getURL());
		});
		Main.bot.addCustomCommand("Stop Bot", "stop", "stop", "Stop Bot", (event, params) -> {
			if (!Bot.hasPermission(Permission.ADMINISTRATOR, event.getMember())) {
				Main.bot.sendMessage(event.getChannel(), "Nice try " + event.getAuthor().getAsMention()
						+ ", but I only listen to cool kids, like the server admins");
			} else {
				System.exit(1);
			}
		});
		Main.bot.addCustomCommand("Get Permissions", "getperms", "getperms(?: (?:(<@!?(\\d+)>)|(\\d+)))?",
				"Get permissions for user or role", (event, params) -> {
					if (!Bot.hasPermission(Permission.ADMINISTRATOR, event.getMember())) {
						Main.bot.sendMessage(event.getChannel(), "Nice try " + event.getAuthor().getAsMention()
								+ ", but I only listen to cool kids, like the server admins");
					} else {
						final EmbedBuilder t = new EmbedBuilder();
						t.setColor(Color.HSBtoRGB((float) Math.random(), 1.0f, 1.0f));
						String s = "";
						String h = "";
						if (params[0].equals("")) {
							for (final Permission f : Permission.getPermissions(Permission.ALL_PERMISSIONS)) {
								s += f.getName() + "\n";
							}
							h = "All Permissions";
						} else if (params[0].matches("<@!?(\\d+)>")) {
							for (final Permission f : event.getGuild().getMemberById(params[1]).getPermissions()) {
								s += f.getName() + "\n";
							}
							h = event.getGuild().getMemberById(params[1]).getEffectiveName() + "'s permissions";
						} else {
							for (final Permission f : event.getGuild().getRoleById(params[0]).getPermissions()) {
								s += f.getName() + "\n";
							}
							h = event.getGuild().getRoleById(params[2]).getName() + "'s permissions";
						}
						t.addField(h, s.trim(), false);
						Main.bot.sendMessage(event.getChannel(), t.build());
					}
				});
		Main.bot.addCustomCommand("Prune", "prune", "prune (?:<@!?(\\d+)> )?(\\d+) ?(clean)?", "Prune Messages",
				(event, params) -> {
					if (!Bot.hasPermission(Permission.ADMINISTRATOR, event.getMember())) {
						Main.bot.sendMessage(event.getChannel(), "Nice try " + event.getAuthor().getAsMention()
								+ ", but I only listen to cool kids, like the server admins");
					} else {
						final MessagePaginationAction history = event.getChannel().getIterableHistory();
						final List<Message> list = new LinkedList<>();
						if (params[0].length() != 0) {
							for (final Message message : history) {
								if (!message.getId().equals(event.getMessage().getId())
										&& message.getAuthor().getId().equals(params[0]) && !message.isPinned()) {
									list.add(message);
								}
								if (list.size() >= Integer.parseInt(params[1])) {
									break;
								}
							}
						} else {
							for (final Message message : history) {
								if (!message.getId().equals(event.getMessage().getId())
										&& (list.size() < Integer.parseInt(params[1])) && !message.isPinned()) {
									list.add(message);
								}
							}
						}
						System.out.println(params[2]);
						if (!params[2].equals("clean")) {
							Main.bot.sendMessage(event.getChannel(), "Deleting " + list.size() + " messages");
						} else {
							event.getMessage().delete().queue();
						}
						event.getChannel().purgeMessages(list);
					}
				});
		Main.bot.addCustomCommand("Get Avatar", "getavatar", "getavatar <@!?(\\d+)>", "Get users avatar",
				(event, params) -> {
					if (!Bot.hasPermission(Permission.ADMINISTRATOR, event.getMember())) {
						Main.bot.sendMessage(event.getChannel(), "Nice try " + event.getAuthor().getAsMention()
								+ ", but I only listen to cool kids, like the server admins");
					} else {
						Main.bot.sendMessage(event.getChannel(),
								event.getGuild().getMemberById(params[0]).getUser().getAvatarUrl());
					}
				});
		Main.bot.addCustomCommand("Reputation", "rep", "rep ((?:(?:\\+|-)\\d+)|list|get)(?: <@!?(\\d+)>)?(?: ?(.+))?",
				"Add or Remove Reputation", (event, params) -> {
					if (params[0].equals("list")) {
						try (BufferedReader br = new BufferedReader(new FileReader("rep.txt"))) {
							final EmbedBuilder t = new EmbedBuilder();
							t.setColor(Color.HSBtoRGB((float) Math.random(), 1.0f, 1.0f));
							t.setTitle("Player Rep List");
							String line;
							while ((line = br.readLine()) != null) {
								final Matcher m = Pattern
										.compile("(\\d+) (-?\\d+)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE)
										.matcher(line);
								if (m.find()) {
									final String id = m.group(1);
									final String rep = m.group(2);
									t.addField("" + Main.bot.getMember(event.getGuild(), id).getEffectiveName() + "#"
											+ Main.bot.getMember(event.getGuild(), id).getUser().getDiscriminator(),
											rep, true);
								}
							}
							Main.bot.sendMessage(event.getChannel(), t.build());
						} catch (final Exception e) {
							System.out.println(e);
						}
					} else if (params[0].equals("get")) {
						try (BufferedReader br = new BufferedReader(new FileReader("rep.txt"))) {
							final EmbedBuilder t = new EmbedBuilder();
							t.setColor(Color.HSBtoRGB((float) Math.random(), 1.0f, 1.0f));
							t.setTitle("Player Rep List");
							String line;
							while ((line = br.readLine()) != null) {
								final Matcher m = Pattern
										.compile("(\\d+) (-?\\d+)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE)
										.matcher(line);
								if (m.find()) {
									final String id = m.group(1);
									final String name = m.group(2);
									if (id.equals(params[1])) {
										t.addField(Main.bot.getMember(event.getGuild(), id).getEffectiveName() + "#"
												+ Main.bot.getMember(event.getGuild(), id).getUser().getDiscriminator(),
												name, true);
									}
								}
							}
							Main.bot.sendMessage(event.getChannel(), t.build());
						} catch (final Exception e) {
							System.out.println(e);
						}
					} else {
						final int rep = Integer.parseInt(params[0]);
						if ((rep != 1) && !Bot.hasPermission(Permission.ADMINISTRATOR, event.getMember())) {
							Main.bot.sendMessage(event.getChannel(), "Nice try " + event.getAuthor().getAsMention()
									+ ", but I only listen to cool kids, like the server admins");
						} else {
							if (!Bot.hasPermission(Permission.ADMINISTRATOR, event.getMember())) {
								try (BufferedReader br = new BufferedReader(new FileReader("repTimer.txt"))) {
									String line;
									while ((line = br.readLine()) != null) {
										final Matcher m = Pattern.compile("(\\d+) (-?\\d+)",
												Pattern.CASE_INSENSITIVE | Pattern.MULTILINE).matcher(line);
										if (m.find() && m.group(1).equals(params[1])) {
											final Date last = new Date();
											last.setTime(Long.parseLong(m.group(2)));
											final long delay = 1 * 7 * 24 * 60 * 60 * 1000;
											last.setTime(last.getTime() + delay);
											final Date now = new Date();
											if (!now.after(last)) {
												Main.bot.sendMessage(event.getChannel(),
														"Sorry, you need to wait a little longer before you can give rep");
												return;
											}
										}
									}
								} catch (final Exception e) {
									System.out.println(e);
								}
							}
							final HashMap<String, String> reps = new HashMap<>();
							try (BufferedReader br = new BufferedReader(new FileReader("rep.txt"))) {
								String line;
								while ((line = br.readLine()) != null) {
									final Matcher m = Pattern
											.compile("(\\d+) (-?\\d+)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE)
											.matcher(line);
									if (m.find()) {
										reps.put(m.group(1), m.group(2));
									}
								}
							} catch (final Exception e) {
								System.out.println(e);
							}
							reps.put(params[1], "" + (rep + Integer.parseInt(reps.getOrDefault(params[1], "0"))));
							try (FileWriter fw = new FileWriter("rep.txt")) {
								for (final String key : reps.keySet()) {
									fw.write(key + " " + reps.get(key) + "\n");
								}
							} catch (final Exception e) {
								System.out.println(e);
							}
							final HashMap<String, String> dates = new HashMap<>();
							try (BufferedReader br = new BufferedReader(new FileReader("repTimer.txt"))) {
								String line;
								while ((line = br.readLine()) != null) {
									final Matcher m = Pattern
											.compile("(\\d+) (\\d+)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE)
											.matcher(line);
									if (m.find()) {
										dates.put(m.group(1), m.group(2));
									}
								}
							} catch (final Exception e) {
								System.out.println(e);
							}
							dates.put(params[1], "" + new Date().getTime());

							try (FileWriter fw = new FileWriter("repTimer.txt")) {
								for (final String key : dates.keySet()) {
									fw.write(key + " " + dates.get(key) + "\n");
								}
							} catch (final Exception e) {
								System.out.println(e);
							}
							new Date();
						}
					}
				});
		Main.bot.addCustomCommand("IGN", "ign", "ign (list|find|<@!?(\\d+)>) ?(.*)", "IGN commands",
				(event, params) -> {
					if (params[0].equalsIgnoreCase("list")) {
						try (BufferedReader br = new BufferedReader(new FileReader("ign.txt"))) {
							final EmbedBuilder t = new EmbedBuilder();
							t.setColor(Color.HSBtoRGB((float) Math.random(), 1.0f, 1.0f));
							t.setTitle("Player IGN List");
							String line;
							while ((line = br.readLine()) != null) {
								final Matcher m = Pattern
										.compile("(\\d+) (.+)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE)
										.matcher(line);
								if (m.find()) {
									final String id = m.group(1);
									final String name = m.group(2);
									t.addField("" + Main.bot.getMember(event.getGuild(), id).getEffectiveName() + "#"
											+ Main.bot.getMember(event.getGuild(), id).getUser().getDiscriminator(),
											name, true);
								}
							}
							Main.bot.sendMessage(event.getChannel(), t.build());
						} catch (final Exception e) {
							System.out.println(e);
						}
					} else if (params[0].equalsIgnoreCase("find")) {
						final Matcher p = Pattern.compile("<@!?(\\d+)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE)
								.matcher(params[2]);
						final String str = ((p.find()) ? p.group(1) : params[2]).trim();
						try (BufferedReader br = new BufferedReader(new FileReader("ign.txt"))) {
							final EmbedBuilder t = new EmbedBuilder();
							t.setColor(Color.HSBtoRGB((float) Math.random(), 1.0f, 1.0f));
							t.setTitle("Player IGN List");
							String line;
							while ((line = br.readLine()) != null) {
								final Matcher m = Pattern
										.compile("(\\d+) (.+)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE)
										.matcher(line);
								if (m.find()) {
									final String id = m.group(1);
									final String name = m.group(2);
									if (id.equals(str) || name.toLowerCase().contains(str.toLowerCase())) {
										t.addField(Main.bot.getMember(event.getGuild(), id).getEffectiveName() + "#"
												+ Main.bot.getMember(event.getGuild(), id).getUser().getDiscriminator(),
												name, true);
									}
								}
							}
							Main.bot.sendMessage(event.getChannel(), t.build());
						} catch (final Exception e) {
							System.out.println(e);
						}
					} else {
						if (!params[1].equals(event.getAuthor().getId())
								&& !Bot.hasPermission(Permission.ADMINISTRATOR, event.getMember())) {
							Main.bot.sendMessage(event.getChannel(), "Nice try " + event.getAuthor().getAsMention()
									+ ", but I only listen to cool kids, like the server admins");
						} else {
							final HashMap<String, String> igns = new HashMap<>();
							try (BufferedReader br = new BufferedReader(new FileReader("ign.txt"))) {
								String line;
								while ((line = br.readLine()) != null) {
									final Matcher m = Pattern
											.compile("(\\d+) (.+)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE)
											.matcher(line);
									if (m.find()) {
										igns.put(m.group(1), m.group(2));
									}
								}
							} catch (final Exception e) {
								System.out.println(e);
							}
							if (!igns.containsKey(params[1])) {
								try (FileWriter fw = new FileWriter("ign.txt", true)) {
									fw.write(params[1] + " " + params[2].trim() + "\n");
								} catch (final Exception e) {
									System.out.println(e);
								}
							} else {
								try (FileWriter fw = new FileWriter("ign.txt")) {
									boolean written = false;
									for (final String key : igns.keySet()) {
										if (!written && key.equals(params[1])) {
											fw.write(params[1] + " " + params[2] + "\n");
											written = true;
										} else {
											fw.write(key + " " + igns.get(key) + "\n");
										}
									}
								} catch (final Exception e) {
									System.out.println(e);
								}
							}
						}
					}
				});
		Main.bot.addCustomListener((event) -> {
			if (event.getClass().equals(GuildMemberJoinEvent.class)) {
				final GuildMemberJoinEvent e = (GuildMemberJoinEvent) event;
				Main.bot.sendMessage(e.getGuild().getSystemChannel(), "Welcome! " + e.getMember().getAsMention()
						+ ", check out our " + Main.bot.getChannel(e.getGuild(), "rules").getAsMention()
						+ " and then post an app in "
						+ Main.bot.getChannel(e.getGuild(), "whitelist-apps").getAsMention()
						+ " and leadership will either message you privately or in "
						+ Main.bot.getChannel(e.getGuild(), "general").getAsMention()
						+ ". You can also choose to hang out in general and get to know some of us before you post an app. Any questions you might have can be posted in general as well! If you have been accepted please head over to "
						+ Main.bot.getChannel(e.getGuild(), "role-claim").getAsMention()
						+ " and add the server(s) you wish to get updates for!");
			} else if (event.getClass().equals(GuildMessageReactionAddEvent.class)) {
				final GuildMessageReactionAddEvent e = (GuildMessageReactionAddEvent) event;
				Main.bot.sendMessage(e.getChannel(),
						e.getMember().getAsMention() + " reacted with " + e.getReactionEmote().getName());
			}
		});
		/*
		 * bot.addCustomCommand("", "", "", "", (event,params) -> { if
		 * (!Bot.hasPermission(Permission.ADMINISTRATOR, event.getMember())) {
		 * bot.sendMessage(event.getChannel(),"Nice try " +
		 * event.getAuthor().getAsMention() +
		 * ", but I only listen to cool kids, like the server admins and " +
		 * event.getJDA().getUserById(Bot.ME).getAsMention()); } else {
		 *
		 * } });
		 */
		/*
		 *
		 */
	}
}
