package com.thinkpalm.keepintouch.utils.sharedPrefHelper;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * Created  on 08-03-2017.
 */

public class SharedPreferenceHandler
{

    private static SharedPreferences preferences;
    private static String SHRARED_PREF_NAME = "com.moon.njoud.shared";
    private static SharedPreferenceHandler sharedPreferenceHandler;

    public static SharedPreferenceHandler getInstance(Context context)
    {
        if (preferences == null)
        {
            preferences = context.getSharedPreferences(SHRARED_PREF_NAME, context.MODE_PRIVATE);
        }
        if (sharedPreferenceHandler == null)
            sharedPreferenceHandler = new SharedPreferenceHandler();
        return sharedPreferenceHandler;
    }

    public void saveString(String key, String value)
    {
        try
        {
            preferences.edit().putString(key, value).apply();
        } catch (Exception e)
        {

        }
    }

    public void saveInt(String key, int value)
    {
        try
        {
            preferences.edit().putInt(key, value).apply();
        } catch (Exception e)
        {

        }
    }

    public void saveBoolean(String key, Boolean value)
    {
        try
        {
            preferences.edit().putBoolean(key, value).apply();
        } catch (Exception e)
        {

        }
    }

    public String getString(String key)
    {
        try
        {
            return preferences.getString(key, "");
        } catch (Exception e)
        {

        }
        return "";
    }

    public Boolean getBoolean(String key)
    {
        try
        {
            return preferences.getBoolean(key, false);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public Integer getInt(String key)
    {
        try
        {
            return preferences.getInt(key, 0);
        } catch (Exception e)
        {
            e.printStackTrace();
        }return 0;
    }

    public void delete(String key)
    {
        try
        {
            if (preferences.contains(key))
            {
                preferences.edit().remove(key).commit();
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void save(String key, Object value)
    {
        try
        {
            SharedPreferences.Editor editor = preferences.edit();
            if (value instanceof Boolean)
            {
                editor.putBoolean(key, (Boolean) value);
            } else if (value instanceof Integer)
            {
                editor.putInt(key, (Integer) value);
            } else if (value instanceof Float)
            {
                editor.putFloat(key, (Float) value);
            } else if (value instanceof Long)
            {
                editor.putLong(key, (Long) value);
            } else if (value instanceof String)
            {
                editor.putString(key, (String) value);
            } else if (value instanceof Enum)
            {
                editor.putString(key, value.toString());
            } else if (value != null)
            {
                throw new RuntimeException("Attempting to save non-supported preference");
            }

            editor.commit();
        } catch (RuntimeException e)
        {
            e.printStackTrace();
        }
    }

}
