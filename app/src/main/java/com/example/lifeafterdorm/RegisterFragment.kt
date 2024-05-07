package com.example.lifeafterdorm

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.lifeafterdorm.databinding.FragmentRegisterBinding
import android.Manifest
import android.content.pm.PackageManager
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.lifeafterdorm.data.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.storage.FirebaseStorage

class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private var national: String? = null
    private val errorMsg = ArrayList<String>()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLocation: Location? = null
    private val storage = FirebaseStorage.getInstance()
    private val storageReference = storage.reference
    private val imageURL:String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        errorMsg.clear()
        val view = binding.root
        val spinner = binding.spinNationality
        val nationalList = resources.getStringArray(R.array.countries_array)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, nationalList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

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
                currentLocation?.let {
                    openGoogleMaps(it)
                } ?: run {
                    Toast.makeText(requireContext(), "Press 'Get Me' to get location data.", Toast.LENGTH_SHORT).show()
                }
            }
        }
        spanMap.setSpan(clickableMap, 0, spanMap.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvLocateDetail.text = spanMap
        binding.tvLocateDetail.movementMethod = LinkMovementMethod.getInstance()

        val btnUpload = binding.btnUploadPic
        btnUpload.setOnClickListener{
            uploadImage()
        }

        val spanTerm = SpannableString(binding.cbTerms.text)
        val clickableToTerm = object : ClickableSpan(){
            override fun onClick(widget: View) {
                widget.cancelPendingInputEvents();
                showTermsandCondition()
            }
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.setUnderlineText(true)
            }
        }
        spanTerm.setSpan(clickableToTerm, 11, spanTerm.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.cbTerms.text = spanTerm
        binding.cbTerms.movementMethod = LinkMovementMethod.getInstance()

        binding.btnLocation.setOnClickListener {
            findLocation()
        }

        binding.btnBack.setOnClickListener{
            findNavController().navigateUp()
        }

        binding.btnRegister.setOnClickListener {
            val email:String = binding.tfEmail.text.toString().trim()
            if(email != null){
                if(!isValidEmail(email))
                    errorMsg.add("Your email format is wrong.")
            }else{
                errorMsg.add("Please enter your email.")
            }

            val password:String = binding.tfPassword.text.toString().trim()
            if(password != null){
                isValidPassword(password)
            }else{
                errorMsg.add("Please enter your password.")
            }

            val phoneNum:String = binding.tfPhoneNum.text.toString().trim()
            if(phoneNum != null){
                isValidPhoneNumber(phoneNum)
            }else{
                errorMsg.add("Please enter your phone number.")
            }
        }

        return view
    }

    private fun uploadImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, FILE_PICK_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FILE_PICK_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val uri = data?.data
            if (uri != null && isImageFile(uri)) {
                val filePath = getRealPathFromURI(uri)
                if (filePath != null) {
                    Toast.makeText(requireContext(), "Selected file: $filePath", Toast.LENGTH_SHORT).show()
                    // Now you have the file path, you can use it as needed
                } else {
                    Toast.makeText(requireContext(), "Failed to retrieve file path", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Please select a valid image file", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getRealPathFromURI(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = requireActivity().contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            it.moveToFirst()
            return it.getString(columnIndex)
        }
        return null
    }


    private fun isImageFile(uri: Uri): Boolean {
        val contentResolver = requireContext().contentResolver
        val extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        return mimeType == "image/png" || mimeType == "image/jpeg"
    }

    private fun showTermsandCondition() {
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

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {
        val passwordPattern = "(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}"
        return password.matches(passwordPattern.toRegex())
    }

    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        val phonePattern = "^[0-9]{7,15}\$"
        return phoneNumber.matches(phonePattern.toRegex())
    }

    private fun findLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSIONS_REQUEST_CODE)
        } else {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    currentLocation = Location(location.longitude, location.latitude)
                    Toast.makeText(requireContext(), "Latitude: $latitude, Longitude: $longitude", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Could not retrieve location", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun openGoogleMaps(location: Location) {
        val uri = "geo:${location.latitude},${location.longitude}?q=${location.latitude},${location.longitude}"
        val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        mapIntent.setPackage("com.google.android.apps.maps")
        if (mapIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(mapIntent)
        } else {
            // Fallback option: Open Google Maps in browser
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/?q=${location.latitude},${location.longitude}"))
            startActivity(browserIntent)
        }
    }

    companion object {
        private const val PERMISSIONS_REQUEST_CODE = 1001
        private const val FILE_PICK_REQUEST_CODE = 123
    }
}
