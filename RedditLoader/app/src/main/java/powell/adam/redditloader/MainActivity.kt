package powell.adam.redditloader

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
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
        val urlData = "https://www.reddit.com/.json"
        val request = JsonObjectRequest(Request.Method.GET, urlData, null, Response.Listener<JSONObject> { response ->

            val posts = response
                    .getJSONObject("data")
                    .getJSONArray("children")

            recyclerView.adapter = PostAdapter(posts)

        },
                Response.ErrorListener {
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                })

        VolleyService.requestQueue.add(request)
        VolleyService.requestQueue.start()

    }

    class PostAdapter(val post: JSONArray) : RecyclerView.Adapter<PostViewHolder>() {

        override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
            holder.bind(post.getJSONObject(position))
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.post_item, parent, false)
            return PostViewHolder(view)
        }

        override fun getItemCount(): Int = post.length()
    }

    class PostViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(item: JSONObject) {

            val cardView = view.findViewById(R.id.item_view) as CardView
            val title = view.findViewById(R.id.post_title) as TextView
            val imageView = view.findViewById(R.id.post_image) as NetworkImageView
            val subredditTextView = view.findViewById(R.id.sub_reddit_text) as TextView


            title.text = Html.fromHtml(item.getJSONObject("data")["title"].toString(), 0)
            val urlTest = item.getJSONObject("data")["thumbnail"].toString()
            imageView.setImageUrl(urlTest, VolleyService.imageLoader)
            subredditTextView.text = Html.fromHtml(item.getJSONObject("data")["subreddit_name_prefixed"].toString(), 0)

            cardView.setOnClickListener {
                val intent = Intent(view.context, WebViewActivity::class.java)
                intent.putExtra("LINK", item.getJSONObject("data")["url"].toString())
                view.context.startActivity(intent)
            }
        }
    }
}
