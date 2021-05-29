package com.demo.chatapp.ui.userProfile

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.demo.chatapp.R
import com.demo.chatapp.databinding.UserProfileFragmentBinding
import com.demo.chatapp.glide.GlideApp
import com.demo.chatapp.ui.loginUi.LoginActivity
import com.demo.chatapp.utils.FirestoreUtil
import com.demo.chatapp.utils.StorageUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.user_profile_fragment.*
import java.io.ByteArrayOutputStream


@AndroidEntryPoint
class UserProfileFragment : Fragment() {


    private lateinit var binding: UserProfileFragmentBinding
    private val viewModel: UserProfileViewModel by viewModels()

    private val RC_SELECT_IMAGE = 2
    private lateinit var selectedImageBytes: ByteArray
    private var pictureJustChanged = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = UserProfileFragmentBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageViewProfilePicture.setOnClickListener {
            val intent = Intent().apply {
                type = "image/*"
                action = Intent.ACTION_GET_CONTENT
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
            }
            startActivityForResult(Intent.createChooser(intent, "Select Image"), RC_SELECT_IMAGE)

        }


        binding.btnSave.setOnClickListener {

            if (::selectedImageBytes.isInitialized)
                StorageUtil.uploadProfilePhoto(selectedImageBytes) { imagePath ->
                    FirestoreUtil.updateCurrentUser(
                        editText_name.text.toString(),
                        editText_bio.text.toString(), imagePath
                    )
                }
            else
                FirestoreUtil.updateCurrentUser(
                    editText_name.text.toString(),
                    editText_bio.text.toString(), null
                )

            Toast.makeText(requireActivity(), "Saving", Toast.LENGTH_SHORT).show()


        }

        binding.btnSignOut.setOnClickListener {
            viewModel.logout {
                val intent = Intent(requireActivity(), LoginActivity::class.java)
                startActivity(intent)
            }
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SELECT_IMAGE && resultCode == Activity.RESULT_OK &&
            data != null && data.data != null
        ) {
            val selectedImagePath = data.data
            val selectedImageBmp = MediaStore.Images.Media
                .getBitmap(activity?.contentResolver, selectedImagePath)

            val outputStream = ByteArrayOutputStream()
            selectedImageBmp.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            selectedImageBytes = outputStream.toByteArray()

            GlideApp.with(this)
                .load(selectedImageBytes)
                .into(imageView_profile_picture)

            pictureJustChanged = true
        }
    }

    override fun onStart() {
        super.onStart()

        FirestoreUtil.getCurrentUser {
            if (this.isVisible) {
                binding.editTextName.setText(it.name)
                binding.editTextBio.setText(it.bio)
                if (!pictureJustChanged && it.profilePicturePath != null)
                    GlideApp.with(this)
                        .load(StorageUtil.pathToReference(it.profilePicturePath))
                        .placeholder(R.drawable.baseline_account_circle_purple_600_24dp)
                        .into(imageView_profile_picture)
            }

        }
    }

}