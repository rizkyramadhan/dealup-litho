package id.dealup.dealup.libs

import android.util.Log
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.net.URLEncoder


class Api {
    companion object {
        val baseUrl = "http://192.168.43.7:5000"
        //                val baseUrl = "https://edumatis.plansys.co"
        var sessionId = ""

        fun login(username: String, password: String): String {
            try {
                val res = khttp.post(
                    url = Api.baseUrl + "/Account/Login",
                    json = mapOf("UserName" to username, "Password" to password)
                )

                if (res.statusCode == 200) {
                    return res.text
                }
                return ""
            } catch (e: Exception) {
                Log.e(Api::class.java.simpleName, e.toString())
                return ""
            }
        }

        fun getProfile(): JSONObject? {
            if (sessionId == "") return null

            val res = khttp.get(
                url = Api.baseUrl + "/Account/Profile",
                headers = mapOf("Authorization" to "Bearer " + sessionId)
            )

            if (res.statusCode != 200) {
                return null
            }
            return res.jsonObject
        }

        fun getMurid(muridId: Int): JSONObject? {
            if (sessionId == "") return null
            val url = Api.baseUrl + "/api/Postgre?p=xjoin?" + URLEncoder.encode(
                "_fields=[t1.nama_murid],[t1.nsa],[t1.nisn],[t1.id],[t2.nama_kelas],[t2.updated_at],[t2.id]|_page=1|_size=25|_where=(t0.murid_id,eq,$muridId)|_join=t0.kelas_murid,_lj,t1.murid,_lj,t2.kelas|_on0=(t0.murid_id,eq,t1.id)|_on1=(t0.kelas_id,eq,t2.id)",
                "UTF-8"
            )

            val res = khttp.get(
                url = url,
                headers = mapOf("Authorization" to "Bearer " + sessionId)
            )
            if (res.statusCode != 200) {
                return null
            }

            if (res.jsonArray.length() > 0) {
                val resObj = res.jsonArray.getJSONObject(0);
                var result = JSONObject()
                val iter = resObj.keys()
                while (iter.hasNext()) {
                    val key = iter.next()
                    try {
                        if (key == "t2_id") continue;
                        val value = resObj.get(key)
                        val keyClean = key
                            .replace("t1_", "")
                            .replace("t2_", "")

                        if (!result.has(keyClean))
                            result.putOpt(keyClean, value)
                    } catch (e: JSONException) {
                        // Something went wrong!
                    }
                }
                return result
            }
            return null
        }

        fun getKewajiban(muridId: Int, sekolahId: Int): JSONArray? {
            if (sessionId == "") return null
            val res = khttp.get(
                url = Api.baseUrl + "/Payment/Murid?id=$muridId&sid=$sekolahId",
                headers = mapOf("Authorization" to "Bearer " + sessionId)
            )
            if (res.statusCode != 200) {
                return null
            }
            return res.jsonArray
        }


        fun getPengumuman(sekolahId: Int): JSONArray? {
            if (sessionId == "") return null
            val res = khttp.get(
                url = Api.baseUrl + "/api/Postgre?p=pengumuman?" + URLEncoder.encode(
                    "_fields=[tgl],[pengumuman]|_where=(sekolah_id,eq,$sekolahId)",
                    "UTF-8"
                ),
                headers = mapOf("Authorization" to "Bearer " + sessionId)
            )
            if (res.statusCode != 200) {
                return null
            }
            return res.jsonArray
        }

        fun getSekolah(sekolahId: Int): JSONObject? {
            if (sessionId == "") return null

            val res = khttp.get(
                url = Api.baseUrl + "/api/Postgre?p=sekolah?" + URLEncoder.encode(
                    "_fields=[nama_sekolah],[nama_singkat],[is_active],[logo_url],[id],[created_at]|_where=(id,eq,$sekolahId)",
                    "UTF-8"
                ),
                headers = mapOf("Authorization" to "Bearer " + sessionId)
            )

            if (res.statusCode != 200) {
                return null
            }

            if (res.jsonArray.length() > 0) {
                return res.jsonArray.getJSONObject(0);
            }
            return null
        }


    }
}