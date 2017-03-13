package TuLingBot;

import com.alibaba.fastjson.JSON;

/**
 * Created by LYY on 2017/3/13.
 */
public class BotResponse {
    public String getCode() {
        return code;
    }

    public String getText() {
        return text;
    }

    public String getUrl() {
        return url;
    }

    public String getList() {
        return list;
    }

    private String code;
    private String text;
    private String url;
    private String list;
    BotResponse(String json){
        this.text = JSON.parseObject(json, (Class<T>) BotResponse.class);
    }

}
