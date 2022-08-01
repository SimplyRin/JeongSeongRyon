package net.simplyrin.jeongseongryon.listeners;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.simplyrin.jeongseongryon.JeongSeongRyon;

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
@RequiredArgsConstructor
public class EventHandler extends ListenerAdapter {
	
	private final JeongSeongRyon instance;
	
	@Override
	public void onMessageDelete(MessageDeleteEvent event) {
		var rChannelId = this.instance.getConfig().getLong("ReceiveChannel", 0L);
		
		var channel = event.getChannel();
		
		if (rChannelId != channel.getIdLong()) {
			return;
		}
		
		var messageId = event.getMessageId();
		var deleted = false;
		
		System.out.println("メッセージ削除: " + messageId);
		
		for (String name : this.instance.getData().getSection("data").getKeys()) {
			for (String msgId : this.instance.getData().getSection("data." + name + ".stats").getKeys()) {
				if (messageId.equals(msgId)) {
					deleted = true;
					
					this.instance.getData().set("data." + name + ".stats." + msgId, null);
				}
			}
		}
		
		if (deleted) {
			this.instance.saveData();
			
			channel.sendMessage("削除したメッセージの登録済み結果を削除しました。").complete();
		}
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		var rChannelId = this.instance.getConfig().getLong("ReceiveChannel", 0L);
		
		var channel = event.getChannel();
		
		if (rChannelId != channel.getIdLong()) {
			return;
		}
		
		var genericMessage = event.getMessage();
		var messageId = genericMessage.getId();
		var message = genericMessage.getContentRaw();
		
		System.out.println(message);
		
		var sdf = new SimpleDateFormat("yyyy-MM-dd (E) HH:mm:ss", Locale.JAPANESE);
		var date = sdf.format(new Date());
		
		var lines = message.split("\n");
		
		boolean added = false;
		
		for (int i = 0; i < lines.length; i++) {
			var line = lines[i];
			
			if (!(line.contains("vs") || line.contains("ｖｓ"))) {
				continue;
			}

			String[] vs;
			if (line.contains("vs")) {
				vs = line.split("vs");
			} else {
				vs = line.split("ｖｓ");
			}

			if (vs.length != 2) {
				return;
			}
			
			added = true;
			
			var p1 = new ComulativeData(vs[0]);
			var p2 = new ComulativeData(vs[1]);
			
			for (var value : Arrays.asList(p1, p2)) {
				this.instance.getData().set("data." + value.getName() + ".stats." + messageId + ".date", date);
				this.instance.getData().set("data." + value.getName() + ".stats." + messageId + ".win", value.isWon());
				
				System.out.println("追加: " + value.getName() + " [date: " + date + ", win: " + value.isWon() + "]");
			}
		}
		
		if (added) {
			this.instance.saveData();
			
			System.out.println("データ追加: " + messageId);
			
			genericMessage.addReaction(Emoji.fromFormatted("👌")).complete();
		}
	}
	
	@AllArgsConstructor
	public class ComulativeData {
		private String data;
		
		public String getName() {
			return this.data.replace("○", "").replace("×", "").replace(" ", "").replace("　", "").trim();
		}
		
		public boolean isWon() {
			return this.data.contains("○");
		}
	}

}
