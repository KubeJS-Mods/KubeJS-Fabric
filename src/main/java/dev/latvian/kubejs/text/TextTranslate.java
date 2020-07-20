package dev.latvian.kubejs.text;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.util.JsonUtilsJS;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;

import java.util.Objects;

/**
 * @author LatvianModder
 */
public class TextTranslate extends Text
{
	private static final Object[] NO_OBJECTS = {};

	private final String key;
	private final Object[] objects;

	public TextTranslate(String k, Object[] o)
	{
		key = k;
		objects = o;

		for (int i = 0; i < objects.length; i++)
		{
			if (objects[i] instanceof Text || !(objects[i] instanceof Text) && JsonUtilsJS.toPrimitive(JsonUtilsJS.of(objects[i])) == null)
			{
				objects[i] = Text.of(objects[i]);
			}
		}
	}

	public TextTranslate(String k)
	{
		key = k;
		objects = NO_OBJECTS;
	}

	public String getKey()
	{
		return key;
	}

	public Object[] getObjects()
	{
		return objects;
	}

	@Override
	public MutableText rawComponent()
	{
		Object[] o = new Object[objects.length];

		for (int i = 0; i < objects.length; i++)
		{
			if (objects[i] instanceof Text)
			{
				o[i] = ((Text) objects[i]).component();
			}
			else if (objects[i] instanceof net.minecraft.text.Text)
			{
				o[i] = ((net.minecraft.text.Text) objects[i]).shallowCopy();
			}
			else
			{
				o[i] = objects[i];
			}
		}

		return new TranslatableText(key, o);
	}

	@Override
	public Text rawCopy()
	{
		Object[] o = new Object[objects.length];

		for (int i = 0; i < objects.length; i++)
		{
			if (objects[i] instanceof Text)
			{
				o[i] = ((Text) objects[i]).copy();
			}
			else if (objects[i] instanceof net.minecraft.text.Text)
			{
				o[i] = ((net.minecraft.text.Text) objects[i]).shallowCopy();
			}
			else
			{
				o[i] = objects[i];
			}
		}

		return new TextTranslate(key, o);
	}

	@Override
	public JsonObject toJson()
	{
		JsonObject o = getPropertiesAsJson();
		o.addProperty("translate", key);

		if (objects.length > 0)
		{
			JsonArray array = new JsonArray();

			for (Object ob : objects)
			{
				array.add(JsonUtilsJS.of(ob));
			}

			o.add("with", array);
		}

		return o;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
		{
			return true;
		}
		else if (!(obj instanceof TextTranslate) || !key.equals(((TextTranslate) obj).key))
		{
			return false;
		}

		Object[] o = ((TextTranslate) obj).objects;

		if (objects.length == o.length)
		{
			for (int i = 0; i < objects.length; i++)
			{
				if (!Objects.equals(objects[i], o[i]))
				{
					return false;
				}
			}

			return super.equals(obj);
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		return (key.hashCode() * 31 + Objects.hash(objects)) * 31 + super.hashCode();
	}
}