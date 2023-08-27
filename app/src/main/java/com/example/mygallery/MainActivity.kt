package com.example.mygallery

import android.content.ContentUris
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mygallery.databinding.ActivityMainBinding
import android.Manifest
import android.content.pm.PackageManager
import kotlin.concurrent.timer

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val requestPermissionLauncher =
        registerForActivityResult(
        ActivityResultContracts.RequestPermission()){isGranted ->
            if(isGranted){
                getAllPhotos()
            }else{
                Toast.makeText(this,"권한이 거부 됨", Toast.LENGTH_LONG).show()
            }
        }
//    val cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//    null, null, null, "${MediaStore.Images.ImageColumns.DATE_TAKEN} DESC")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        if(ContextCompat.checkSelfPermission(this,
            Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            if (
                ActivityCompat.shouldShowRequestPermissionRationale(
                    this, Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                AlertDialog.Builder(this).apply {
                    setTitle("권한이 필요한 이유")
                    setMessage("사징 정보를 이용하기 위해 외부 저장소 권한이 필요")
                    setPositiveButton("권한 요청") { _, _ ->
                        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                    setNegativeButton("거부", null)
                }.show()

            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            return
        }
            getAllPhotos()
    }
    private fun getAllPhotos() {
        //mutableListOf = 수정 가능한 리스트 안에는 Uri 가 들어감
        val uris = mutableListOf<Uri>()
        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            "${MediaStore.Images.ImageColumns.DATE_TAKEN} DESC"
            //use 는 cursor 사용이 끝나면 클로즈 함 id/contentUri 을 들고 옴
        )?.use { cursor ->
            while (cursor.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id
                )
                uris.add(contentUri)
            }
        }
        //Log 를 이용 하면 내가 추가한 것을 알수 있음(개발을 다하고 나면 빠르게 실행하기 위해 없앰)
        Log.d("MainActivity", "getAllPhotos: $uris")
        val adapter = MyPagerAdapter(supportFragmentManager, lifecycle)
        adapter.uris = uris

        binding.viewPager.adapter = adapter

        timer(period = 3000){
            runOnUiThread {
                if(binding.viewPager.currentItem < adapter.itemCount - 1){
                    binding.viewPager.currentItem = binding.viewPager.currentItem +1
                }else{
                    binding.viewPager.currentItem = 0
                }
            }
        }
    }
}