package powell.adam.redditloader

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.NetworkImageView
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)


        // Views
        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Data
        val urlData = "https://www.reddit.com/r/rarepuppers/.json"
        val request = JsonObjectRequest(Request.Method.GET, urlData, null, Response.Listener<JSONObject> { response ->

            val redditPosts = response
                    .getJSONObject("data")
                    .getJSONArray("children")

            recyclerView.adapter = PostAdapter(redditPosts)

        },
                Response.ErrorListener {
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                })

        VolleyService.requestQueue.add(request)
        VolleyService.requestQueue.start()

    }

    class PostAdapter(val post: JSONArray) : RecyclerView.Adapter<PostViewHolder>() {

        override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
            holder.bind(post.getJSONObject(position), position)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.post_item, parent, false)
            return PostViewHolder(view)
        }

        override fun getItemCount(): Int = post.length()
    }

    class PostViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(item: JSONObject, position: Int) {
            val title = view.findViewById(R.id.post_title) as TextView
            val imageView = view.findViewById(R.id.post_image) as NetworkImageView
            title.text = Html.fromHtml(item.getJSONObject("data")["title"].toString(), 0)
            val urlTest = item.getJSONObject("data")["thumbnail"].toString()
            imageView.setImageUrl(urlTest, VolleyService.imageLoader)
        }
    }
}
