package id.dealup.dealup.libs

import android.content.Context
import android.util.Log
import org.json.JSONObject
import java.nio.charset.Charset

class Store {
    companion object {
        lateinit var context: Context;

        fun load(filename: String): JSONObject? {
            var fname = filename
            if (!fname.endsWith(".json")) {
                fname += ".json";
            }
            if (context == null) {
                return null;
            }

            try {
                context.openFileInput(fname).use {
                    try {
                        return JSONObject(it.reader(Charset.forName("UTF-8")).readText())
                    } catch (e: Exception) {
                        Log.e(Store::class.java.simpleName, e.toString());
                    }
                }
            } catch (e: Exception) {
                Log.e(Store::class.java.simpleName, e.toString());
            }
            return null
        }

        fun save(filename: String, obj: JSONObject) {
            var fname = filename
            if (!fname.endsWith(".json")) {
                fname += ".json";
            }

            try {
                context.openFileOutput(fname, Context.MODE_PRIVATE).use {
                    it.write(obj.toString().toByteArray())
                }
            } catch (e: Exception) {
                Log.e(Store::class.java.simpleName, e.toString());
            }
        }

        fun clear(filename: String) {
            var fname = filename
            if (!fname.endsWith(".json")) {
                fname += ".json";
            }

            try {
                context.deleteFile(fname)
            } catch (e: Exception) {
                Log.e(Store::class.java.simpleName, e.toString());
            }
        }
    }
}