package com.thefarhany.tugas2.fragment

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.thefarhany.tugas2.data.dto.UpdateEmailRequest
import com.thefarhany.tugas2.data.dto.UpdateUsernameRequest
import com.thefarhany.tugas2.data.dto.UserResponse
import com.thefarhany.tugas2.data.local.SessionManager
import com.thefarhany.tugas2.data.remote.RetrofitClient
import com.thefarhany.tugas2.databinding.FragmentProfileBinding
import com.thefarhany.tugas2.ui.auth.LoginActivity
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import kotlin.math.absoluteValue

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var sessionManager: SessionManager
    private var selectedImageUri: Uri? = null
    private var currentProfile: UserResponse? = null

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                selectedImageUri = uri

                binding.ivAvatar.visibility = View.VISIBLE
                binding.tvInitials.visibility = View.GONE
                Glide.with(this).load(uri).into(binding.ivAvatar)

                Toast.makeText(requireContext(), "Foto dipilih, klik Upload", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())

        setupListener()
        loadProfile()
    }

    private fun setupListener() = with(binding) {
        btnLogout.setOnClickListener {
            sessionManager.clearSession()
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            requireActivity().finish()
        }

        btnChoosePhoto.setOnClickListener {
            pickImage.launch("image/*")
        }

        btnUploadPhoto.setOnClickListener {
            uploadSelectedPhoto()
        }

        btnSave.setOnClickListener {
            updateProfileFields()
        }
    }

    private fun cookieHeader(): String {
        val token = sessionManager.getToken()
        if (token.isNullOrBlank()) throw IllegalStateException("Token kosong. Silakan login ulang.")
        return "jwt=$token"
    }

    private fun loadProfile() {
        lifecycleScope.launch {
            try {
                val res = RetrofitClient.apiService.getProfile(cookieHeader())
                currentProfile = res
                renderProfile(res)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), e.message ?: "Gagal load profile", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun renderProfile(user: UserResponse) = with(binding) {
        etUsername.setText(user.username)
        etPhoneNumber.setText(user.phoneNumber)
        etEmail.setText(user.email)

        val initials = initialsOf(user.username)
        tvInitials.text = initials
        cardAvatar.setCardBackgroundColor(pickColor(user.username))

        val pic = user.profilePicture
        if (!pic.isNullOrBlank()) {
            // kalau backend balikin path relatif, sesuaikan jadi full URL:
            // contoh: val fullUrl = "http://10.0.2.2:8080/$pic"
            val fullUrl = pic

            ivAvatar.visibility = View.VISIBLE
            tvInitials.visibility = View.GONE

            Glide.with(this@ProfileFragment)
                .load(fullUrl)
                .into(ivAvatar)
        } else {
            ivAvatar.visibility = View.GONE
            tvInitials.visibility = View.VISIBLE
        }
    }

    private fun updateProfileFields() {
        val username = binding.etUsername.text?.toString()?.trim().orEmpty()
        val email = binding.etEmail.text?.toString()?.trim().orEmpty()

        if (username.isBlank() || email.isBlank()) {
            Toast.makeText(requireContext(), "Username dan email wajib diisi", Toast.LENGTH_SHORT).show()
            return
        }

        val old = currentProfile
        if (old == null) {
            Toast.makeText(requireContext(), "Profile belum ter-load", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                if (username != old.username) {
                    currentProfile = RetrofitClient.apiService.updateUsername(
                        cookieHeader(),
                        UpdateUsernameRequest(newUsername = username)
                    )
                }

                val latest = currentProfile ?: old
                if (email != latest.email) {
                    currentProfile = RetrofitClient.apiService.updateEmail(
                        cookieHeader(),
                        UpdateEmailRequest(newEmail = email)
                    )
                }

                val finalProfile = currentProfile ?: old
                renderProfile(finalProfile)
                Toast.makeText(requireContext(), "Profile berhasil diupdate", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), e.message ?: "Gagal update profile", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadSelectedPhoto() {
        val uri = selectedImageUri
        if (uri == null) {
            Toast.makeText(requireContext(), "Pilih foto dulu", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val file = uriToTempFile(uri)
                val reqBody = file.asRequestBody("image/*".toMediaTypeOrNull())
                val part = MultipartBody.Part.createFormData("file", file.name, reqBody)

                val res = RetrofitClient.apiService.uploadProfilePicture(cookieHeader(), part)
                currentProfile = res

                selectedImageUri = null

                Toast.makeText(requireContext(), "Upload foto berhasil", Toast.LENGTH_SHORT).show()

                loadProfile()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), e.message ?: "Gagal upload foto", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initialsOf(username: String): String {
        val parts = username.trim().split("\\s+".toRegex()).filter { it.isNotBlank() }
        return when {
            parts.isEmpty() -> "?"
            parts.size == 1 -> parts[0].take(1).uppercase()
            else -> (parts[0].take(1) + parts[1].take(1)).uppercase()
        }
    }

    private fun pickColor(seed: String): Int {
        val colors = listOf(
            Color.parseColor("#E57373"),
            Color.parseColor("#64B5F6"),
            Color.parseColor("#81C784"),
            Color.parseColor("#FFB74D"),
            Color.parseColor("#BA68C8"),
            Color.parseColor("#4DB6AC")
        )
        return colors[(seed.hashCode().absoluteValue) % colors.size]
    }

    private fun uriToTempFile(uri: Uri): File {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
            ?: throw IllegalStateException("Tidak bisa membaca file dari URI")

        val tempFile = File.createTempFile("profile_", ".jpg", requireContext().cacheDir)
        tempFile.outputStream().use { out ->
            inputStream.use { it.copyTo(out) }
        }
        return tempFile
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
