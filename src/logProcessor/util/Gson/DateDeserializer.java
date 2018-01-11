package logProcessor.util.Gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Date;

/**
 * Created by cao.zm on 2017/10/12.
 */
public class DateDeserializer implements JsonDeserializer<Date> {

  public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    return new Date(json.getAsJsonPrimitive().getAsLong());
  }
}
