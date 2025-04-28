package com.example.tightbudget

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.example.tightbudget.data.AppDatabase
import com.example.tightbudget.databinding.ActivityAddTransactionBinding
import com.example.tightbudget.models.CategoryItem
import com.example.tightbudget.ui.CategoryPickerBottomSheet
import com.example.tightbudget.ui.CreateCategoryBottomSheet
import com.example.tightbudget.utils.EmojiUtils
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import com.example.tightbudget.utils.CategoryConstants
import kotlinx.coroutines.launch

class AddTransactionActivity : AppCompatActivity() {
    // Binds layout elements from activity_add_transaction.xml to this file
    private lateinit var binding: ActivityAddTransactionBinding
    private val TAG = "AddTransactionActivity"

    // Variables to track the current state
    private var selectedCategory: CategoryItem? = null
    private var isExpense = true
    private var isRecurring = false
    private var selectedDate = Calendar.getInstance()
    private var receiptImageUri: Uri? = null

    // Handles capturing a receipt photo using the device camera
    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && receiptImageUri != null) {
            binding.receiptImageView.setImageURI(receiptImageUri)
            binding.receiptImageView.visibility = View.VISIBLE
            binding.addPhotoButton.visibility = View.GONE
            Log.d(TAG, "Receipt image captured successfully")
        } else {
            Log.d(TAG, "Failed to capture receipt image")
        }
    }

    // Handles selecting an image from the device gallery
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            receiptImageUri = uri
            binding.receiptImageView.setImageURI(receiptImageUri)
            binding.receiptImageView.visibility = View.VISIBLE
            binding.addPhotoButton.visibility = View.GONE
            Log.d(TAG, "Receipt image selected from gallery")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        binding = ActivityAddTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewAllCategoriesButton.setOnClickListener {
            showCategoryPicker()
        }

        binding.addCategoryChip.setOnClickListener {
            showCategoryPicker()
        }

        binding.createNewCategoryButton.setOnClickListener {
            showCreateCategoryModal()
        }

        window.decorView.setOnApplyWindowInsetsListener { _, insets ->
            binding.headerFrame.setPadding(0, insets.systemWindowInsetTop, 0, 0)
            insets
        }

        setupTransactionTypeToggle() // Switch between Expense and Income
        setupCategoryChips()        // Category options with emojis
        setupTransactionDatePicker()// Main date of transaction
        setupRecurringSwitch()      // Toggle for recurring transaction
        setupPhotoButton()          // Option to attach photo
        setupSaveButton()           // Save and validate inputs
        setupBackButton()           // Handle back navigation

        Log.d(TAG, "AddTransactionActivity created")
    }

    // This function sets up toggle buttons to switch between 'Expense' and 'Income'
    private fun setupTransactionTypeToggle() {
        binding.expenseButton.isChecked = true

        binding.expenseButton.setOnClickListener {
            isExpense = true
            updateTransactionTypeUI()
        }

        binding.incomeButton.setOnClickListener {
            isExpense = false
            updateTransactionTypeUI()
        }
    }

    // Updates labels and hints when switching between Expense and Income
    private fun updateTransactionTypeUI() {
        if (isExpense) {
            binding.expenseButton.setTextColor(getColor(R.color.white))
            binding.expenseButton.setBackgroundColor(getColor(R.color.teal_light))
            binding.incomeButton.setTextColor(getColor(R.color.text_medium))
            binding.incomeButton.setBackgroundColor(getColor(R.color.white))

            binding.merchantInput.hint = "Who did you pay?"
            binding.merchantInputLabel.text = "Merchant"
        } else {
            binding.incomeButton.setTextColor(getColor(R.color.white))
            binding.incomeButton.setBackgroundColor(getColor(R.color.teal_light))
            binding.expenseButton.setTextColor(getColor(R.color.text_medium))
            binding.expenseButton.setBackgroundColor(getColor(R.color.white))

            binding.merchantInput.hint = "Where is the money from?"
            binding.merchantInputLabel.text = "Income Source"
        }
    }

    // Initialises the category chips and assigns emojis using EmojiUtils
    private fun setupCategoryChips() {
        binding.foodChip.isChecked = true

        binding.foodChip.text = EmojiUtils.getCategoryEmoji(CategoryConstants.FOOD)
        binding.transportChip.text = EmojiUtils.getCategoryEmoji(CategoryConstants.TRANSPORT)
        binding.entertainmentChip.text = EmojiUtils.getCategoryEmoji(CategoryConstants.ENTERTAINMENT)
        binding.housingChip.text = EmojiUtils.getCategoryEmoji(CategoryConstants.HOUSING)
        binding.addCategoryChip.text = EmojiUtils.getActionEmoji("add")

        // Chip click listeners
        binding.foodChip.setOnClickListener {
            selectedCategory = CategoryItem(
                name = CategoryConstants.FOOD,
                emoji = EmojiUtils.getCategoryEmoji(CategoryConstants.FOOD),
                color = "#FF9800",
                budget = 0.0 // Set 0.0 because this is a quick-pick
            )
            updateSelectedCategoryDisplay()
        }
        binding.transportChip.setOnClickListener {
            selectedCategory = CategoryItem(
                name = CategoryConstants.TRANSPORT,
                emoji = EmojiUtils.getCategoryEmoji(CategoryConstants.TRANSPORT),
                color = "#2196F3",
                budget = 0.0
            )
            updateSelectedCategoryDisplay()
        }
        binding.entertainmentChip.setOnClickListener {
            selectedCategory = CategoryItem(
                name = CategoryConstants.ENTERTAINMENT,
                emoji = EmojiUtils.getCategoryEmoji(CategoryConstants.ENTERTAINMENT),
                color = "#9C27B0",
                budget = 0.0
            )
            updateSelectedCategoryDisplay()
        }
        binding.housingChip.setOnClickListener {
            selectedCategory = CategoryItem(
                name = CategoryConstants.HOUSING,
                emoji = EmojiUtils.getCategoryEmoji(CategoryConstants.HOUSING),
                color = "#4CAF50",
                budget = 0.0
            )
            updateSelectedCategoryDisplay()
        }

        updateSelectedCategoryDisplay()
    }

    // Updates the text showing which category is currently selected
    private fun updateSelectedCategoryDisplay() {
        selectedCategory?.let { category ->
            binding.selectedCategoryDisplay.text = "${category.emoji} ${category.name}"
        }
    }

    // Allows the user to pick a transaction date (limited to the next 30 days)
    private fun setupTransactionDatePicker() {
        updateTransactionDateDisplay()

        binding.transactionDateButton.setOnClickListener {
            showTransactionDatePickerDialog()
        }
    }

    private fun showTransactionDatePickerDialog() {
        val year = selectedDate.get(Calendar.YEAR)
        val month = selectedDate.get(Calendar.MONTH)
        val day = selectedDate.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, y, m, d ->
            val pickedDate = Calendar.getInstance()
            pickedDate.set(Calendar.YEAR, y)
            pickedDate.set(Calendar.MONTH, m)
            pickedDate.set(Calendar.DAY_OF_MONTH, d)

            selectedDate = pickedDate
            updateTransactionDateDisplay()
        }, year, month, day)

        // Prevent selection outside of allowed range
        val today = Calendar.getInstance()
        val maxDate = Calendar.getInstance()
        maxDate.add(Calendar.DAY_OF_YEAR, 30)

        datePickerDialog.datePicker.minDate = today.timeInMillis
        datePickerDialog.datePicker.maxDate = maxDate.timeInMillis

        datePickerDialog.show()
    }

    // Formats and updates the date text on screen
    private fun updateTransactionDateDisplay() {
        val formattedDate =
            SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault()).format(selectedDate.time)
        binding.transactionDateButton.text = "$formattedDate 📅"
    }

    // This handles the recurring switch and displays a projected recurring date
    private fun setupRecurringSwitch() {
        binding.recurringSwitch.isChecked = false // Switch defaults to OFF
        binding.recurringDatePicker.visibility = View.GONE

        binding.recurringSwitch.setOnCheckedChangeListener { _, isChecked ->
            isRecurring = isChecked
            binding.recurringDatePicker.visibility = if (isChecked) View.VISIBLE else View.GONE

            if (isChecked) {
                val recurringDate = Calendar.getInstance()
                recurringDate.timeInMillis = selectedDate.timeInMillis
                recurringDate.add(Calendar.DAY_OF_YEAR, 30)

                val formatted = SimpleDateFormat(
                    "EEEE, d MMMM yyyy",
                    Locale.getDefault()
                ).format(recurringDate.time)
                binding.recurringDatePicker.text = "Repeats on: $formatted"
            }
        }
    }

    // Sets up the add photo button to use the camera or gallery
    private fun setupPhotoButton() {
        val cameraIconTextView =
            binding.addPhotoButton.findViewById<TextView>(R.id.cameraIconTextView)
        cameraIconTextView?.text = "📷"

        binding.addPhotoButton.setOnClickListener {
            showImageSourceDialog()
        }
    }

    // Opens a dialog allowing the user to select a photo source
    private fun showImageSourceDialog() {
        val options = arrayOf("Take Photo", "Choose from Gallery")

        android.app.AlertDialog.Builder(this)
            .setTitle("Add Receipt Photo")
            .setItems(options) { _, which ->
                if (which == 0) takePhoto() else chooseFromGallery()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // Takes a new photo with the camera
    private fun takePhoto() {
        val photoFile = createImageFile()
        photoFile?.let {
            receiptImageUri = FileProvider.getUriForFile(
                this,
                "${applicationContext.packageName}.fileprovider",
                it
            )
            takePictureLauncher.launch(receiptImageUri)
        }
    }

    // Selects an existing image from the gallery
    private fun chooseFromGallery() {
        pickImageLauncher.launch("image/*")
    }

    // Creates a temporary image file in the receipts directory
    private fun createImageFile(): File? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        val storageDir = getExternalFilesDir("receipts")
        return try {
            File.createTempFile(imageFileName, ".jpg", storageDir)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating image file: ${e.message}")
            null
        }
    }

    // Save button listener
    private fun setupSaveButton() {
        binding.saveTransactionButton.setOnClickListener {
            if (validateInputs()) saveTransaction()
        }
    }

    // Validates amount and merchant input before saving
    private fun validateInputs(): Boolean {
        val amount = binding.amountInput.text.toString()
        val merchant = binding.merchantInput.text.toString()

        if (amount.isEmpty() || amount == "0" || amount == "0.00") {
            Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
            return false
        }

        if (merchant.isEmpty()) {
            Toast.makeText(this, "Please enter a merchant/income source", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    // Saves the transaction details and returns to the Dashboard
    private fun saveTransaction() {
        val amount = binding.amountInput.text.toString()
        val merchant = binding.merchantInput.text.toString()
        val description = binding.descriptionInput.text.toString()

        Log.d(
            TAG,
            "Saving transaction: $amount, $selectedCategory, ${if (isExpense) "Expense" else "Income"}"
        )
        Log.d(TAG, "Merchant/Source: $merchant")
        Log.d(TAG, "Description: $description")
        Log.d(
            TAG,
            "Date: ${SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.time)}"
        )
        Log.d(TAG, "Recurring: $isRecurring")

        Toast.makeText(this, "Transaction saved successfully!", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }

    // Opens a modal to create a new category
    private fun showCategoryPicker() {
        val db = AppDatabase.getDatabase(this)
        val categoryDao = db.categoryDao()

        lifecycleScope.launch {
            try {
                val categories = categoryDao.getAllCategories()

                // Log for debugging
                Log.d("showCategoryPicker", "Fetched ${categories.size} categories from database.")
                categories.forEach { category ->
                    Log.d("showCategoryPicker", "Category: ${category.name}, Emoji: ${category.emoji}, Budget: ${category.budget}")
                }

                val categoryItems = categories.map { category ->
                    CategoryItem(
                        name = category.name,
                        emoji = category.emoji,
                        color = category.color,
                        budget = category.budget
                    )
                }

                val picker = CategoryPickerBottomSheet(
                    categoryList = categoryItems,
                    onCategorySelected = { selectedCategoryItem ->
                        selectedCategory = selectedCategoryItem
                        updateSelectedCategoryDisplay()
                    },
                    onCreateNewClicked = {
                        showCreateCategoryModal()
                    }
                )

                if (!isFinishing) {
                    picker.show(supportFragmentManager, "CategoryPicker")
                }
            } catch (e: Exception) {
                Log.e("showCategoryPicker", "Error showing category picker", e)
                Toast.makeText(this@AddTransactionActivity, "Error loading categories", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showCreateCategoryModal() {
        Log.d("AddTransactionActivity", "Showing CreateCategoryBottomSheet")

        val createSheet = CreateCategoryBottomSheet()
        createSheet.show(supportFragmentManager, "CreateCategory")
    }

    private fun updateSelectedCategoryDisplay(category: CategoryItem) {
        binding.selectedCategoryDisplay.text = "${category.emoji} ${category.name}"
    }

    // Handles the back button click
    private fun setupBackButton() {
        binding.backButton.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}