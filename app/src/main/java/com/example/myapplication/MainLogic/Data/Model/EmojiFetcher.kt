package com.example.myapplication.MainLogic.Data.Model

import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


@Test
fun callApi(apiUri: String, token: String): String {
    val res = StringBuilder()

    try {
        val uri = URL(apiUri);
        val connection: HttpURLConnection = uri.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"

        connection.setRequestProperty("Content-Type", "application/json")
        connection.setRequestProperty("Accept", "application/json")
        connection.setRequestProperty("Authorization", "Bearer $token")

        val respCode = connection.responseCode
        if (respCode == HttpURLConnection.HTTP_OK || respCode == HttpURLConnection.HTTP_CREATED) {
            BufferedReader(InputStreamReader(connection.inputStream, "utf-8")).use { br ->
                br.forEachLine { s ->
                    Log.v("TAPI", s)
                }
            }
        }
    } catch (e: Exception) {
        return "Invalid Request!"
    }
    return "Invalid Request!"
}
