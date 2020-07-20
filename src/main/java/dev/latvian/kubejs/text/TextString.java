package dev.latvian.kubejs.text;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class TextString extends Text {
	private final String string;
	
	public TextString(@Nullable Object text) {
		string = String.valueOf(text);
	}
	
	public String getRawString() {
		return string;
	}
	
	@Override
	public MutableText rawComponent() {
		return new LiteralText(string);
	}
	
	@Override
	public Text rawCopy() {
		return new TextString(string);
	}
	
	@Override
	public JsonElement toJson() {
		JsonObject o = getPropertiesAsJson();
		
		if (o.size() == 0) {
			return new JsonPrimitive(string);
		}
		
		o.addProperty("text", string);
		return o;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (!(obj instanceof TextString) || !string.equals(((TextString) obj).string)) {
			return false;
		}
		
		return super.equals(obj);
	}
	
	@Override
	public int hashCode() {
		return string.hashCode() * 31 + super.hashCode();
	}
}