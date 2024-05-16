package org.example;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    private static final String ROOM_ID = "马赛克"; // 你要监控的直播间ID
    private static final OkHttpClient client = new OkHttpClient();
    private static final String API_URL = "https://api.live.bilibili.com/room/v1/Room/get_info?room_id=" + ROOM_ID;

    static int live_status=0;
    public static void main(String[] args) {
        Timer timer = new Timer();
        timer.schedule(new CheckLiveTask(), 0, 57000); // 每隔30s检查一次直播状态
    }

    static class CheckLiveTask extends TimerTask {
        @Override
        public void run() {
            try {
                Request request = new Request.Builder()
                        .url(API_URL)
                        .build();

                Response response = client.newCall(request).execute();
                String jsonData = response.body().string();
                JSONObject jsonObject = new JSONObject(jsonData);
                JSONObject data = jsonObject.getJSONObject("data");

                int liveStatus2 = data.getInt("live_status");
                String startTime=data.getString("live_time");
                // System.out.println(startTime);
                if (liveStatus2==1) {
                    System.out.println("直播间正在直播！");
                } else {
                    System.out.println("直播间未直播。");
                }
                if (live_status != liveStatus2){
                    // System.out.println("live_status not equal live_status2");
                    live_status=liveStatus2;

                    ITelegramBot bot=new ITelegramBot();
                    bot.botConnect();
                    bot.sendTextMessage(-1002117523401L, "直播间直播状态改变");
                    if (live_status==1){
                        bot.sendTextMessage(-1002117523401L, "开始时间: " + startTime);
                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}