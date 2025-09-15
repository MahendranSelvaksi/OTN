package com.mobile.dataregisteration

import android.content.Context
import android.graphics.Bitmap
import android.util.Base64
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream

fun loadJsonFromAssets(context: Context, fileName: String): JSONObject {
    val json = context.assets.open(fileName).bufferedReader().use { it.readText() }
    return JSONObject(json)
}

object LoginKeys {
    val STATUS = booleanPreferencesKey("status")
    val USER_ID = intPreferencesKey("user_id")
    val MESSAGE = stringPreferencesKey("message")
    val ACCESS_TOKEN = stringPreferencesKey("accesstoken")
    val USERNAME = stringPreferencesKey("username")
    val EMAIL = stringPreferencesKey("email")
    val PHONE = stringPreferencesKey("phone")
}

data class Village(val villageName: String)
data class Panchayat(val panchayatName: String, val villageList: List<Village>)
data class Union(val unionName: String, val panchayatList: List<Panchayat>)
data class District(val districtName: String, val unionList: List<Union>)

fun loadLocationData(context: Context): List<District> {
    val jsonString = context.assets.open("locations.json").bufferedReader().use { it.readText() }
    val jsonArray = JSONArray(jsonString)

    return List(jsonArray.length()) { dIndex ->
        val districtObj = jsonArray.getJSONObject(dIndex)
        val unions = districtObj.getJSONArray("unionList")
        val unionList = List(unions.length()) { uIndex ->
            val unionObj = unions.getJSONObject(uIndex)
            val panchayats = unionObj.getJSONArray("panchayatList")
            val panchayatList = List(panchayats.length()) { pIndex ->
                val panchayatObj = panchayats.getJSONObject(pIndex)
                val villages = panchayatObj.getJSONArray("villageList")
                val villageList = List(villages.length()) { vIndex ->
                    val villageObj = villages.getJSONObject(vIndex)
                    Village(villageObj.getString("villageName"))
                }
                Panchayat(panchayatObj.getString("panchayatName"), villageList)
            }
            Union(unionObj.getString("unionName"), panchayatList)
        }
        District(districtObj.getString("districtName"), unionList)
    }
}

fun bitmapToBase64(bitmap: Bitmap): String {
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
    val byteArray = byteArrayOutputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.NO_WRAP)
}
