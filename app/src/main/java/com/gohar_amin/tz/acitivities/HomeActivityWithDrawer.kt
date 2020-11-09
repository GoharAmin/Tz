package com.gohar_amin.tz.acitivities

import android.R.attr
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.model.Image
import com.gohar_amin.tz.R
import com.gohar_amin.tz.utils.Utils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar


class HomeActivityWithDrawer : AppCompatActivity() {
     val SELECTED_IMAGE_NAME: String="selectedImageName"
    val SELECTED_IMAGE_PATH: String="selectedImagePath"
    val SELECT_IMAGE: String="selectImage"
    private lateinit var appBarConfiguration: AppBarConfiguration
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_with_drawer)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val btmNavView: BottomNavigationView = findViewById(R.id.btm_nav_view)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_sell,
                R.id.allUsersFragment,
                R.id.allUsersFragment,
                R.id.navigation_setting
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        btmNavView.setupWithNavController(navController)
        val receiverId=intent.getStringExtra("openChat")
        if(receiverId!=null){
            val b=Bundle()
            b.putString("openChat",receiverId);

        }

    }
  /*  override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.nav, menu)
        return true
    }*/

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode === RESULT_OK && data != null) {
            val list: List<Image>? = ImagePicker.getImages(data)
            if (list != null && !list.isEmpty()) {
                sendBroadcastForImage(list[0].getPath(), list[0].getName())
            }
        }
        if (requestCode==201 &&resultCode == RESULT_OK && data != null) {
            sendBroadcastForImage(data.data!!.toString(), data.data!!.lastPathSegment.toString())
        }
    }
    private fun sendBroadcastForImage(path: String, name: String) {
        Log.e("SelectedImagesPath", "" + path)
        val i = Intent(SELECT_IMAGE)
        i.putExtra(SELECTED_IMAGE_PATH, path)
        i.putExtra(SELECTED_IMAGE_NAME, name)
        sendBroadcast(i)
    }
}