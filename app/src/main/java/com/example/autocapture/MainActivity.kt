package com.example.autocapture

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.hardware.Camera
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.autocapture.databinding.ActivityMainBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import java.util.*


class MainActivity : AppCompatActivity() {
    lateinit var btn: Button
    lateinit var btnFetch: Button
    lateinit var btnStop: Button

    lateinit var binding: ActivityMainBinding
    var imgUri: Uri? = null
    var bitmapImage: Bitmap? = null

    var handler = Handler()
    var runnable: Runnable? = null
    var delay = 1000 * 60

    val baseUrl = "http://10.10.10.157/api/api/"

    lateinit var storageReference: StorageReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val serviceIntent = Intent(this, WorkerClass::class.java)
        //   startService(serviceIntent)
        val storage: FirebaseStorage = FirebaseStorage.getInstance()
        storageReference = storage.reference

/*        img = findViewById(R.id.iv)
        recyclerView = findViewById(R.id.ivFetch)
        btn = findViewById(R.id.btnCapture)
        btnFetch = findViewById(R.id.btnFetch)
        btnStop = findViewById(R.id.btnStop)*/
        FirebaseApp.initializeApp(this)

        binding.btnFetch.setOnClickListener {

            //     fetchDetails()
            /* val mToast = Toast.makeText(applicationContext, "Sample Toast", Toast.LENGTH_LONG)
             mDisplayToast(mToast)*/

            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val retrofitAPI: RetrofitApi = retrofit.create(RetrofitApi::class.java)
            retrofitAPI.fetchImage().enqueue(object : Callback<ImageResponse> {
                override fun onResponse(
                    call: Call<ImageResponse>,
                    response: Response<ImageResponse>
                ) {
                    val res: ImageResponse = response.body()!!
                    val recyclerviewItemAdapter = RecyclerAdapter(res)
                    binding.ivFetch.setHasFixedSize(true)
                    binding.ivFetch.layoutManager = LinearLayoutManager(this@MainActivity)
                    binding.ivFetch.adapter = recyclerviewItemAdapter
                    Log.d("MYTAG", "onResponse: fetch data " + res)
                }

                override fun onFailure(call: Call<ImageResponse>, t: Throwable) {
                    Log.d("MYTAG", "onFailure: " + t.message)
                }
            })
        }

        binding.btnCapture.setOnClickListener {
            captureFrontPhoto()
        }

        binding.btnStop.setOnClickListener {
            onDestroy()
            finish()
            Toast.makeText(this, "closed service", Toast.LENGTH_SHORT).show()
        }

        /*   val uploadWorkRequest: WorkRequest = OneTimeWorkRequestBuilder<WorkerClass>().build()

           // Submit the WorkRequest to the system
           WorkManager.getInstance(this).enqueue(uploadWorkRequest)*/
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(Runnable {
            handler.postDelayed(runnable!!, delay.toLong())
            captureFrontPhoto()
        }.also { runnable = it }, delay.toLong())
    }

    override fun onDestroy() {
        super.onDestroy()
        val serviceIntent = Intent(this, WorkerClass::class.java)
        startService(serviceIntent)
    }

    // Function to display counter
    private fun mDisplayTimer(view: TextView) {
        Thread {
            val mToastDurationSecs = 10000 / 1000
            for (i in 1..mToastDurationSecs) {
                runOnUiThread {
                    view.text = i.toString()
                }
                Thread.sleep(30000)
            }
        }.start()
    }

    // Function to invoke Toast
    private fun mDisplayToast(toast: Toast) {
        Thread {
            for (i in 1..10000 / 2000) {
                toast.show()
                Thread.sleep(1000 * 60 * 2)
                Log.d("MYYYYYTAG", "mDisplayToast: toast calling...")
                toast.cancel()
            }
        }.start()

    }

    private fun fetchDetails() {

        val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()

        val databaseReference: DatabaseReference = firebaseDatabase.getReference()

        val getImage: DatabaseReference = databaseReference.child("images/")

        getImage.addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val link = dataSnapshot.getValue(String::class.java)
                    Log.d("MYTAG", "onDataChange: " + link)
                }

                override fun onCancelled(
                    databaseError: DatabaseError
                ) {
                    // we are showing that error message in
                    // toast
                    Toast.makeText(this@MainActivity, "Error Loading Image", Toast.LENGTH_SHORT)
                        .show()
                }
            })


        storageReference.getBytes(1024 * 1024).addOnSuccessListener {
            val bitmap: Bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
            //  imgFetch.setImageBitmap(bitmap)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun uploadImage(base64: Bitmap) {
        // firebase data uploaded
        /*      val date = Calendar.getInstance().time
              Log.d("MYTAG", "uploadImage: " + imgUri.toString())
              Log.d("MYTAG", "uploadImage: " + date)
              FirebaseStorage.getInstance().getReference().child("images/" + date)
                  .putFile(imgUri!!)
                  .addOnSuccessListener(object : OnSuccessListener<UploadTask.TaskSnapshot> {
                      override fun onSuccess(p0: UploadTask.TaskSnapshot?) {
                          Log.d("MYTAG", "success: " + p0)
                          Toast.makeText(this@MainActivity, "Success", Toast.LENGTH_SHORT)
                              .show()
                      }
                  }).addOnFailureListener { p0 ->
                      Log.d("MYTAG", "failed: " + p0.message)
                      Toast.makeText(this@MainActivity, "Failed", Toast.LENGTH_SHORT).show()
                  }*/

        //local database upladed
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val retrofitAPI: RetrofitApi = retrofit.create(RetrofitApi::class.java)

        val date = Calendar.getInstance().time

        val imageUri: Uri = imgUri!!
        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
        Log.d("MYTAG", "uploadImage: image uri " + imageUri)
        Log.d("MYTAG", "uploadImage: bitmap  " + bitmap)
        Log.d("MYTAG", "uploadImage: base64  " + base64)
        val image = encodeImage(base64)
        /* datamodel.date = date.toString()
         datamodel.image = base64*/
        retrofitAPI.uploadImage(DataModal(image, date.toString()))
            .enqueue(object : Callback<UploadResponse> {
                override fun onResponse(
                    call: Call<UploadResponse>,
                    response: Response<UploadResponse>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@MainActivity,
                            "succes data" + response.body(),
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("MYTAG", "onSuccess: upload image " + response.body())
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "erros found" + response.errorBody(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }

                override fun onFailure(call: Call<UploadResponse?>, t: Throwable) {
                    Toast.makeText(
                        this@MainActivity,
                        "error found " + t.message,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    Log.d("MYTAG", "onFailure: " + t.message)
                }
            })
    }

    fun captureFrontPhoto() {
        Log.d("MYTAG", "Preparing to take photo")
        var camera: Camera? = null
        val cameraInfo = Camera.CameraInfo()
        val backCamera = 0
        val frontCamera = 1
        Camera.getCameraInfo(frontCamera, cameraInfo)
        try {
            camera = Camera.open(frontCamera)
            camera.enableShutterSound(false)
        } catch (e: Exception) {
            Log.d("MYTAG", "Camera not available: " + e.message)
            camera = null
        }
        try {
            if (null == camera) {
                Log.d("MYTAG", "Could not get camera instance")
            } else {
                Log.d("MYTAG", "Got the camera, creating the dummy surface texture")
                try {
                    camera.setPreviewTexture(SurfaceTexture(0))
                    camera.startPreview()
                } catch (e: Exception) {
                    Log.d("MYTAG", "Could not set the surface preview texture")
                    e.printStackTrace()
                }
                camera.takePicture(null, null, object : Camera.PictureCallback {
                    @Deprecated("Deprecated in Java")
                    @RequiresApi(Build.VERSION_CODES.O)
                    override fun onPictureTaken(data: ByteArray, camera: Camera) {
                        try {
                            val bmp = BitmapFactory.decodeByteArray(data, 0, data.size)
                            //  img.setImageBitmap(bmp)
                            imgUri = getImageUri(this@MainActivity, bmp)
                            Log.d("MYTAG", "onPictureTaken: " + imgUri.toString())
                            uploadImage(bmp)
                        } catch (e: Exception) {
                            Log.d("MYTAG", "onPictureTaken: bitmap error " + e.message)
                        }
                        camera.release()
                    }
                })
            }
        } catch (e: Exception) {
            Log.d("MYTAG", "onPictureTaken: camere error " + e.message)
            camera?.release()
        }
    }

    fun getImageUri(inContext: Context, inImage: Bitmap?): Uri? {
        val OutImage = Bitmap.createScaledBitmap(inImage!!, 1000, 1000, true)

        val path = MediaStore.Images.Media.insertImage(
            inContext.getContentResolver(),
            OutImage,
            "Title" + Calendar.getInstance().time,
            null
        )
        return Uri.parse(path)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun encodeImage(bm: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 80, baos)
        val b = baos.toByteArray()
        return Base64.getEncoder().encodeToString(b)
    }
}