
package com.example.lifeafterdorm

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.isNotEmpty
import androidx.databinding.DataBindingUtil.setContentView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.lifeafterdorm.data.Location
import com.example.lifeafterdorm.databinding.FragmentRegisterBinding
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.example.lifeafterdorm.controller.*
import com.example.lifeafterdorm.data.User
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.FileOutputStream
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private lateinit var national: String
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var currentLocation: Location
    private lateinit var locationRequest: LocationRequest
    private lateinit var imagePath:Uri
    private lateinit var dbRef : DatabaseReference
    private lateinit var storageRef : StorageReference
    private var auth = FirebaseAuth.getInstance()
    private var phoneExist = false


    @SuppressLint("WrongThread")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 5000
            fastestInterval = 2000
        }
        val view = binding.root
        val spinner = binding.spinNationality
        val nationalList = resources.getStringArray(R.array.countries_array)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, nationalList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        val defaultImageResource = R.drawable.default_user
        val bitmap = BitmapFactory.decodeResource(resources, defaultImageResource)
        val file = File(requireContext().cacheDir, "default_user.png")
        file.createNewFile()
        imagePath = Uri.fromFile(file)
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.close()

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                national = nationalList[position]
                Toast.makeText(requireContext(), "Selected nationality: $national", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                Toast.makeText(requireContext(), "Please select your nationality", Toast.LENGTH_SHORT).show()
            }
        }

        val spanMap = SpannableString(binding.tvLocateDetail.text)
        val clickableMap = object : ClickableSpan() {
            override fun onClick(widget: View) {
                if (::currentLocation.isInitialized) {
                    openGoogleMaps(currentLocation)
                } else {
                    Toast.makeText(requireContext(), "Press 'Get Me' to get location data.", Toast.LENGTH_SHORT).show()
                }
            }
        }
        spanMap.setSpan(clickableMap, 0, spanMap.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvLocateDetail.text = spanMap
        binding.tvLocateDetail.movementMethod = LinkMovementMethod.getInstance()

        val btnUpload = binding.btnUploadPic
        btnUpload.setOnClickListener{
            startChooseImg()
        }

        val spanTerm = SpannableString(binding.cbTerms.text)
        val clickableToTerm = object : ClickableSpan(){
            override fun onClick(widget: View) {
                widget.cancelPendingInputEvents();
                showTermsAndCondition()
            }
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = true
            }
        }
        spanTerm.setSpan(clickableToTerm, 11, spanTerm.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.cbTerms.text = spanTerm
        binding.cbTerms.movementMethod = LinkMovementMethod.getInstance()
        binding.btnLocation.setOnClickListener {
            getCurrentLocation()
        }

        binding.btnBack.setOnClickListener{
            findNavController().navigateUp()
        }

        binding.btnRegister.setOnClickListener {
            val email: String = binding.tfEmail.text.toString().trim()
            val password: String = binding.tfPassword.text.toString().trim()
            val phoneNum: String = binding.tfPhoneNum.text.toString().trim()
            val errorMsg = ArrayList<String>()
            var gender:String = ""

            if(email.isNotEmpty() && password.isNotEmpty() && phoneNum.isNotEmpty() &&
                ::currentLocation.isInitialized && national.isNotEmpty() && binding.rbgGender.isNotEmpty()){
                if(!passwordFormat(password)){
                    errorMsg.add("Password format wrong must be 1 Upper & lowercase, special character and number.")
                }

                val checkedRadioButtonId = binding.rbgGender.checkedRadioButtonId
                if (checkedRadioButtonId != -1) {
                    gender = when (checkedRadioButtonId) {
                        binding.rbMale.id -> "M"
                        binding.rbFemale.id -> "F"
                        else -> ""
                    }
                } else {
                    errorMsg.add("Please select a gender.")
                }
                dbRef = FirebaseDatabase.getInstance().getReference("User")
                val phoneListener = object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            for (userSnap in snapshot.children) {
                                val user = userSnap.getValue(User::class.java)
                                if (user != null && user.phoneNum == phoneNum) {
                                   phoneExist = true
                                    break
                                }
                            }
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(context, "Error: $error", Toast.LENGTH_LONG).show()
                    }
                }
                dbRef.addValueEventListener(phoneListener)
                if(phoneExist)
                    errorMsg.add("This phone number already had been registered.")
                if(!isValidEmail(email)){
                    errorMsg.add("Email format wrong.")
                }
                if(!isValidPhoneNumber(phoneNum)){
                    errorMsg.add("Your phone number format is wrong.")
                }

                if(!binding.cbTerms.isChecked)
                    errorMsg.add("Please read our terms and conditions and check it.")

                if (errorMsg.isEmpty()) {
                    isUserIDExists(requireContext()) { idExisted ->
                        if (idExisted) {
                            readLatestUserIDFromFirebase(requireContext()) { latestID ->
                                if (latestID != null) {
                                    val newUserid = incrementID(latestID)
                                    val newUser = User(
                                        id = newUserid,
                                        name = "User $newUserid",
                                        email = email,
                                        gender = gender,
                                        location = currentLocation,
                                        nationality = national,
                                        phoneNum = phoneNum,
                                    )
                                    signUpWithUserClass(newUser, password, newUserid)
                                }
                            }
                        } else {
                            val newUser = User(
                                id = "U0001",
                                name = "User U0001",
                                email = email,
                                gender = gender,
                                location = currentLocation,
                                nationality = national,
                                phoneNum = phoneNum,
                            )
                            signUpWithUserClass(newUser, password, "U0001")
                        }
                    }
                } else {
                    showErrorDialog(errorMsg.joinToString("\n"))
                }
            }else{
                errorMsg.add("Please field in all mandatory details.\nClick 'Get Me' button to get location")
            }
        }

        return view
    }

    private fun uploadImageToFirebase(id:String){
        storageRef = FirebaseStorage.getInstance().reference
        val imgRef = storageRef.child("user_image/${id}.png")
        imgRef.putFile(imagePath)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Image Upload successful", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener{
                Toast.makeText(requireContext(), "Fail to upload image", Toast.LENGTH_LONG).show()
            }
    }

    private fun signUpWithUserClass(user: User, password:String, userID:String) {
        auth.createUserWithEmailAndPassword(user.email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    sendVerificationEmail(requireContext())
                    writeToFirebase(user)
                    uploadImageToFirebase(userID)
                    showSuccessDialog()
                } else {
                    showErrorDialog("Email is repeated registered.")
                }
            }
    }


    private fun writeToFirebase(user: User) {
        dbRef = FirebaseDatabase.getInstance().getReference("User")
        dbRef.child(user.id).setValue(user)
            .addOnCompleteListener{
                Toast.makeText(requireContext(), "Data saved", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener{
                Toast.makeText(requireContext(), "Error ${it.toString()}", Toast.LENGTH_LONG).show()
            }
    }

    private fun showErrorDialog(message: String) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Error")
        alertDialogBuilder.setMessage(message)
        alertDialogBuilder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun showSuccessDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Welcome")
        alertDialogBuilder.setMessage("Hello, nice to meet you.")
        alertDialogBuilder.setPositiveButton("Success") { _, _ ->
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
    private fun startChooseImg(){
        val i = Intent()
        i.setType("image/*")
        i.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(i, "Choose an image for avatar"), 111)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val contentResolver = context?.contentResolver
        if (requestCode == 111 && resultCode == Activity.RESULT_OK && data != null){
            imagePath = data.data!!
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imagePath)
            binding.ivAvatar.setImageBitmap(bitmap)
        }
    }

    private fun showTermsAndCondition() {
        val dialogView = layoutInflater.inflate(R.layout.terms_and_conditions, null)
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogView)
            .setTitle("Terms and Conditions")
            .setCancelable(true)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (isGPSEnabled()) {
                    getCurrentLocation()
                } else {
                    turnOnGPS()
                }
            }
        }
    }
    @SuppressLint("ObsoleteSdkInt")
    private fun getCurrentLocation() {
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Getting current location...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        if (Build.VERSION.SDK_INT >= Build. VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (isGPSEnabled()) {
                    LocationServices.getFusedLocationProviderClient(requireContext())
                        .requestLocationUpdates(locationRequest, object : LocationCallback() {
                            override fun onLocationResult(locationResult: LocationResult) {
                                super.onLocationResult(locationResult)
                                LocationServices.getFusedLocationProviderClient(requireContext())
                                    .removeLocationUpdates(this)
                                if (locationResult.locations.isNotEmpty()) {
                                    val index = locationResult.locations.size - 1
                                    val latitude = locationResult.locations[index].latitude
                                    val longitude = locationResult.locations[index].longitude
                                    Toast.makeText(requireContext(), "Latitude: $latitude\nLongitude: $longitude", Toast.LENGTH_LONG).show()
                                    currentLocation = Location(longitude, latitude)
                                    progressDialog.dismiss()
                                }
                            }
                        }, Looper.getMainLooper())
                } else {
                    progressDialog.dismiss()
                    turnOnGPS()
                }
            } else {
                progressDialog.dismiss() // Dismiss the dialog box
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            }
        }
    }


    private fun turnOnGPS() {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)

        val result = LocationServices.getSettingsClient(requireContext())
            .checkLocationSettings(builder.build())

        result.addOnCompleteListener { task ->
            try {
                task.getResult(ApiException::class.java)
                Toast.makeText(requireContext(), "GPS is already turned on", Toast.LENGTH_SHORT).show()
            } catch (e: ApiException) {
                when (e.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        val resolvableApiException = e as ResolvableApiException
                        resolvableApiException.startResolutionForResult(requireContext() as Activity, 2)
                    } catch (ex: IntentSender.SendIntentException) {
                        ex.printStackTrace()
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    }
                }
            }
        }
    }

    private fun isGPSEnabled(): Boolean {
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        return locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) ?: false
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun openGoogleMaps(location: Location) {
        val uri = "geo:${location.latitude},${location.longitude}?q=${location.latitude},${location.longitude}"
        val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        mapIntent.setPackage("com.google.android.apps.maps")
        if (mapIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(mapIntent)
        } else {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/?q=${location.latitude},${location.longitude}"))
            startActivity(browserIntent)
        }
    }

}
