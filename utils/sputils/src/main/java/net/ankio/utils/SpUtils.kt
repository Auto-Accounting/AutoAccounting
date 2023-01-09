package net.ankio.utils

import android.content.Context
import android.content.SharedPreferences


object SpUtils {
    private const val PREF_APP = "pref_app"
    /**
     * Gets boolean data.
     *
     * @param context the context
     * @param key     the key
     * @return the boolean data
     */
    fun getBooleanData(context: Context, key: String?): Boolean {
        return context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE).getBoolean(key, false)
    }

    /**
     * Gets int data.
     *
     * @param context the context
     * @param key     the key
     * @return the int data
     */
    fun getIntData(context: Context, key: String?): Int {
        return context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE).getInt(key, 0)
    }

    /**
     * Gets string data.
     *
     * @param context the context
     * @param key     the key
     * @return the string data
     */
    // Get Data
    fun getStringData(context: Context, key: String?): String? {
        return context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE).getString(key, null)
    }

    /**
     * Save data.
     *
     * @param context the context
     * @param key     the key
     * @param val     the val
     */
    // Save Data
    fun saveData(context: Context, key: String?, `val`: String?) {
        context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE).edit().putString(key, `val`)
            .apply()
    }

    /**
     * Save data.
     *
     * @param context the context
     * @param key     the key
     * @param val     the val
     */
    fun saveData(context: Context, key: String?, `val`: Int) {
        context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE).edit().putInt(key, `val`)
            .apply()
    }

    /**
     * Save data.
     *
     * @param context the context
     * @param key     the key
     * @param val     the val
     */
    fun saveData(context: Context, key: String?, `val`: Boolean) {
        context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(key, `val`)
            .apply()
    }

    fun getSharedPrefEditor(context: Context, pref: String?): SharedPreferences.Editor? {
        return context.getSharedPreferences(pref, Context.MODE_PRIVATE).edit()
    }

    fun saveData(editor: SharedPreferences.Editor) {
        editor.apply()
    }
    /**
     * Save data.
     *
     * @param context the context
     * @param key     the key
     * @param val     the val
     */
    fun saveData(context: Context, key: String?, value: Any?) {
        context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE).edit()
            .putString(key, value.toString())
            .apply()
    }
}