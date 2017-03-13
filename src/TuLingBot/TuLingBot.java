package TuLingBot;

import javax.websocket.Session;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by LYY on 2017/3/13.
 */
public class TuLingBot {
    private String key = "1b9aad96226f4a0988d27551f878c166";
    private Session session;
    private String apiURL = "http://www.tuling123.com/openapi/api?key=";
    TuLingBot(Session session){
        this.session = session;
    }

    public String bot(String info)  {

        URL getUrl = null;
        try {

            String getURL = apiURL+key+"&info="+ URLEncoder.encode(info,"utf-8");
            getUrl = new URL(getURL);
            HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
            connection.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader( connection.getInputStream(), "utf-8"));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
            // 断开连接
            connection.disconnect();
            return sb.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();

        }
        return null;
    }

    public static void main(String[] args) {
        System.out.print(new TuLingBot(null).bot("我想看新闻"));

    }
}
