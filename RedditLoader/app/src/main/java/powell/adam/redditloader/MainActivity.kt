package powell.adam.redditloader

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
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
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Listener
        toolbar.setNavigationOnClickListener {
            showDialog(recyclerView)
        }

        // Get frontpage data
        getData(null, recyclerView)
    }

    private fun getData(subredditString: String?, recyclerView: RecyclerView) {
        val urlData = if (subredditString.isNullOrBlank()) {
            "https://www.reddit.com/.json"
        }
        else {
            "https://www.reddit.com/r/$subredditString/.json"
        }

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

    class PostAdapter(val redditPosts: JSONArray) : RecyclerView.Adapter<PostViewHolder>() {

        override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
            holder.bind(redditPosts.getJSONObject(position))
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.post_item, parent, false)
            return PostViewHolder(view)
        }

        override fun getItemCount(): Int = redditPosts.length()
    }

    class PostViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(item: JSONObject) {

            // Define views
            val cardView = view.findViewById<CardView>(R.id.item_view)
            val titleView = view.findViewById<TextView>(R.id.post_title)
            val imageView = view.findViewById<NetworkImageView>(R.id.post_image)
            val subredditTextView = view.findViewById<TextView>(R.id.sub_reddit_text)

            // Set views
            titleView.text = Html.fromHtml(item.getJSONObject("data")["title"].toString(), 0)
            val url = item.getJSONObject("data")["thumbnail"].toString()
            imageView.setImageUrl(url, VolleyService.imageLoader)
            subredditTextView.text = Html.fromHtml(item.getJSONObject("data")["subreddit_name_prefixed"].toString(), 0)

            cardView.setOnClickListener {
                val intent = Intent(view.context, WebViewActivity::class.java)
                intent.putExtra("LINK", item.getJSONObject("data")["url"].toString())
                view.context.startActivity(intent)
            }
        }
    }

    private fun showDialog(recyclerView: RecyclerView) {
        val subredditArray = arrayOf("Frontpage", "funny", "rarepuppers", "nfl", "gaming", "androiddev") // Could do something fancier here if I had the time.

        AlertDialog.Builder(this)
            .setTitle("Subreddits")
            .setSingleChoiceItems(subredditArray, 0) { _, which ->
                val currentItem = subredditArray[which]
                if (currentItem == "Frontpage") {
                    getData(null, recyclerView)
                }
                else {
                    getData(currentItem, recyclerView)
                }
            }
            .setNegativeButton("Okay") { dialog, which ->

            }
            .create()
            .show()
    }
}
