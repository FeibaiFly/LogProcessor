package logProcessor.util.Gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;

/**
 * Created by cao.zm on 2017/10/12.
 * 本方法使Gson对Date类型进行json转换时输出为时间戳格式
 */
public class GsonEx {
  public static Gson create() {
    GsonBuilder gb = new GsonBuilder();
    gb.registerTypeAdapter(java.util.Date.class, new DateSerializer()).setDateFormat(DateFormat.LONG);
    gb.registerTypeAdapter(java.util.Date.class, new DateDeserializer()).setDateFormat(DateFormat.LONG);
    Gson gson = gb.create();
    return gson;
  }

  public static Gson GSON = GsonEx.create();
}
