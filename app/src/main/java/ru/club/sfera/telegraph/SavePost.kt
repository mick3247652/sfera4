package ru.club.sfera.telegraph

import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import org.json.JSONException
import org.json.JSONObject
import ru.club.sfera.app.App
import ru.club.sfera.constants.Constants
import ru.club.sfera.model.Item
import ru.club.sfera.util.CustomRequest
import java.util.*

class SavePost {
    private val item = Item()

    init {
        item.imgUrl = ""
        item.videoUrl = ""
        item.previewVideoImgUrl = ""
        item.mediaList.clear()
    }


    fun newPost(text: String,imageUrl: String) {
        item.setPost(text)
        item.imgUrl = imageUrl
        val jsonReq = object : CustomRequest(
            Request.Method.POST, Constants.METHOD_ITEMS_NEW, null,
            Response.Listener { response ->
                try {

                    if (!response.getBoolean("error")) {


                    }

                } catch (e: JSONException) {

                    e.printStackTrace()

                } finally {

                    //sendPostSuccess()
                }
            }, Response.ErrorListener {
                //sendPostSuccess()

                //                     Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }) {

            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["accountId"] = java.lang.Long.toString(App.getInstance().id)
                params["accessToken"] = App.getInstance().accessToken
                params["groupId"] = java.lang.Long.toString(0)
                params["rePostId"] = java.lang.Long.toString(item.getRePostId())
                params["postMode"] = Integer.toString(item.getAccessMode())
                params["postText"] = item.getPost()
                params["postImg"] = item.getImgUrl()
                params["postArea"] = item.getArea()
                params["postCountry"] = item.getCountry()
                params["postCity"] = item.getCity()
                params["postLat"] = java.lang.Double.toString(item.getLat()!!)
                params["postLng"] = java.lang.Double.toString(item.getLng()!!)

                params["feeling"] = Integer.toString(item.getFeeling())

                if (item.getMediaList().size != 0) {

                    Collections.reverse(item.getMediaList())

                    for (i in 0 until item.getMediaList().size) {

                        if (item.getMediaList().get(i).getType() == 0) {

                            params["images[$i]"] = item.getMediaList().get(i).getImageUrl()
                        }
                    }
                }

                params["videoImgUrl"] = item.getPreviewVideoImgUrl()
                params["videoUrl"] = item.getVideoUrl()

                return params
            }
        }

        val socketTimeout = 0//0 seconds - change to what you want
        val policy = DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        jsonReq.retryPolicy = policy

        App.getInstance().addToRequestQueue(jsonReq)
    }

}