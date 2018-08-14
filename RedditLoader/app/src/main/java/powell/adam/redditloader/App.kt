package powell.adam.redditloader

import android.app.Application


class App : Application() {

    override fun onCreate() {
        super.onCreate()
        VolleyService.init(this)
    }
}
