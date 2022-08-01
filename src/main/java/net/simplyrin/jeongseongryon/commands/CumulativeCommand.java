package net.simplyrin.jeongseongryon.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import net.simplyrin.jeongseongryon.JeongSeongRyon;
import net.simplyrin.jeongseongryon.classes.BaseCommand;
import net.simplyrin.jeongseongryon.classes.CommandPermission;
import net.simplyrin.jeongseongryon.classes.CommandType;
import net.simplyrin.jeongseongryon.classes.PandaMessageEvent;

/**
 * Created by SimplyRin on 2022/08/01.
 *
 * Copyright (c) 2022 SimplyRin
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
public class CumulativeCommand extends BaseCommand {
	
	@Override
	public String getCommand() {
		return "!comulative";
	}

	@Override
	public String getDescription() {
		return "戦績情報を表示します。";
	}

	@Override
	public boolean sendTyping() {
		return false;
	}

	@Override
	public CommandData getCommandData() {
		return new CommandDataImpl("戦績表示", this.getDescription());
	}

	@Override
	public List<String> getAlias() {
		return Arrays.asList("!戦績表示");
	}

	@Override
	public CommandType getType() {
		return CommandType.EqualsIgnoreCase;
	}

	@Override
	public CommandPermission getPermission() {
		return CommandPermission.Everyone;
	}

	@Override
	public void execute(JeongSeongRyon instance, PandaMessageEvent event, String[] args) {
		if (args.length > 0) {
			if ((args[0].equalsIgnoreCase("file") || args[0].equals("ファイル")) && instance.isBotOwner(event.getUser())) {
				event.getChannel().sendFile(new File("data.yml")).complete();
				return;
			}
		}
		
		HashMap<String, Integer> map = new HashMap<>();
		
		for (String name : instance.getData().getSection("data").getKeys()) {
			System.out.println("key: " + name);
			
			for (String messageId : instance.getData().getSection("data." + name + ".stats").getKeys()) {
				// String date = instance.getData().getString("data." + name + ".stats." + stats + ".date");
				boolean win = instance.getData().getBoolean("data." + name + ".stats." + messageId + ".win");
				
				// System.out.println("name: " + name + ", date: " + date + ", win: " + win);
				
				if (map.get(name) == null) {
					map.put(name, 0);
				}
				
				if (win) {
					map.put(name, map.get(name) + 1);
				}
			}
		}
		
		List<Entry<String, Integer>> list = new ArrayList<Entry<String, Integer>>(map.entrySet());
		
		Collections.sort(list, new Comparator<Entry<String, Integer>>() {
			@Override
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});

		int previous = 0, rank = 1;
		
		var sb = new StringBuilder();
		
		for (int i = 0; i < list.size(); i++) {
			var value = list.get(i);
			
			if (previous != value.getValue()) {
				rank = i + 1;
			}
			
			String line = this.getRankTag(rank) + "　" + value.getValue() + "勝　" + value.getKey();
			sb.append(line + "\n");
			
			System.out.println(line);
			
			previous = value.getValue();
		}
		
		event.reply(sb.toString());
	}
	
	public String getRankTag(int rank) {
		switch (rank) {
			case 1:
				return "1️⃣";
			case 2:
				return "2️⃣";
			case 3:
				return "3️⃣";
			default:
				var s = String.valueOf(rank);
				
				var sb = new StringBuilder(s);
				for (int i = 0; i < s.length(); i++) {
					char c = s.charAt(i);
					if (0x30 <= c && c <= 0x39) {
						sb.setCharAt(i, (char) (c + 0xFEE0));
					}
				}
				
				return sb.toString();
		}
	}

}
