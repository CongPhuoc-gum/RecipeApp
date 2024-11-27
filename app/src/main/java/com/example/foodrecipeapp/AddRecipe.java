package com.example.foodrecipeapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AddRecipe extends AppCompatActivity {

    ImageButton btn_back;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private ImageView selectedImageView;
    private EditText inputRecipeName, inputDescription, inputIngredients, inputSteps;
    private DatabaseReference recipeRef;
    private StorageReference storageReference;
    private Spinner spinnerCookingTime, spinnerServings, spinnerCountry;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_recipe);
        btn_back = findViewById(R.id.btn_back);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Khởi tạo Firebase references
        recipeRef = FirebaseDatabase.getInstance().getReference("recipes");
        storageReference = FirebaseStorage.getInstance().getReference("images");

        // Khởi tạo các view
        selectedImageView = findViewById(R.id.selected_image);
        inputRecipeName = findViewById(R.id.input_recipe_name);
        inputDescription = findViewById(R.id.input_description);
        inputIngredients = findViewById(R.id.input_ingredient);
        inputSteps = findViewById(R.id.input_steps);
        spinnerCookingTime = findViewById(R.id.spinner_cooking_time);
        spinnerServings = findViewById(R.id.spinner_servings);
        spinnerCountry = findViewById(R.id.spinner_country);

        Button btnAddimage = findViewById(R.id.btn_add_image);
        Button btnSubmit = findViewById(R.id.button_submit);

        setUpSpinners();

        // Chọn ảnh từ gallery
        btnAddimage.setOnClickListener(view -> openFileChooser());

        // Gửi công thức lên Firebase
        btnSubmit.setOnClickListener(view -> uploadRecipeData());


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

    }

    private void setUpSpinners() {
        // Cooking time options
        String[] cookingTimes = {"10 phút", "20 phút", "30 phút", "40 phút", "50 phút"};
        ArrayAdapter<String> cookingTimeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cookingTimes);
        cookingTimeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCookingTime.setAdapter(cookingTimeAdapter);
        spinnerCookingTime.setSelection(0);

        // Servings options
        String[] servings = {"1 người", "2 người", "4 người", "6 người"};
        ArrayAdapter<String> servingsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, servings);
        servingsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerServings.setAdapter(servingsAdapter);
        spinnerServings.setSelection(0);

        // Country options
        String[] countries = {"Việt Nam", "Afghanistan", "Albania", "Algeria", "Andorra", "Angola", "Antigua and Barbuda",
                "Argentina", "Armenia", "Australia", "Austria", "Azerbaijan", "Bahamas", "Bahrain", "Bangladesh",
                "Barbados", "Belarus", "Belgium", "Belize", "Benin", "Bhutan", "Bolivia", "Bosnia and Herzegovina",
                "Botswana", "Brazil", "Brunei", "Bulgaria", "Burkina Faso", "Burundi", "Cabo Verde", "Cambodia", "Cameroon",
                "Canada", "Central African Republic", "Chad", "Chile", "China", "Colombia", "Comoros", "Congo",
                "Costa Rica", "Croatia", "Cuba", "Cyprus", "Czech Republic", "Denmark", "Djibouti", "Dominica",
                "Dominican Republic", "Ecuador", "Egypt", "El Salvador", "Equatorial Guinea", "Eritrea", "Estonia",
                "Eswatini", "Ethiopia", "Fiji", "Finland", "France", "Gabon", "Gambia", "Georgia", "Germany", "Ghana",
                "Greece", "Grenada", "Guatemala", "Guinea", "Guinea-Bissau", "Guyana", "Haiti", "Honduras", "Hungary",
                "Iceland", "India", "Indonesia", "Iran", "Iraq", "Ireland", "Israel", "Italy", "Jamaica", "Japan", "Jordan",
                "Kazakhstan", "Kenya", "Kiribati", "Korea (North)", "Korea (South)", "Kuwait", "Kyrgyzstan", "Laos", "Latvia",
                "Lebanon", "Lesotho", "Liberia", "Libya", "Liechtenstein", "Lithuania", "Luxembourg", "Madagascar", "Malawi",
                "Malaysia", "Maldives", "Mali", "Malta", "Marshall Islands", "Mauritania", "Mauritius", "Mexico", "Micronesia",
                "Moldova", "Monaco", "Mongolia", "Montenegro", "Morocco", "Mozambique", "Myanmar", "Namibia", "Nauru", "Nepal",
                "Netherlands", "New Zealand", "Nicaragua", "Niger", "Nigeria", "North Macedonia", "Norway", "Oman", "Pakistan",
                "Palau", "Panama", "Papua New Guinea", "Paraguay", "Peru", "Philippines", "Poland", "Portugal", "Qatar",
                "Romania", "Russia", "Rwanda", "Saint Kitts and Nevis", "Saint Lucia", "Saint Vincent and the Grenadines",
                "Samoa", "San Marino", "Sao Tome and Principe", "Saudi Arabia", "Senegal", "Serbia", "Seychelles", "Sierra Leone",
                "Singapore", "Slovakia", "Slovenia", "Solomon Islands", "Somalia", "South Africa", "South Sudan", "Spain",
                "Sri Lanka", "Sudan", "Suriname", "Sweden", "Switzerland", "Syria", "Taiwan", "Tajikistan", "Tanzania", "Thailand",
                "Timor-Leste", "Togo", "Tonga", "Trinidad and Tobago", "Tunisia", "Turkey", "Turkmenistan", "Tuvalu", "Uganda",
                "Ukraine", "United Arab Emirates", "United Kingdom", "United States", "Uruguay", "Uzbekistan", "Vanuatu",
                "Vatican City", "Venezuela", "Vietnam", "Yemen", "Zambia", "Zimbabwe"};
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, countries);
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCountry.setAdapter(countryAdapter);
        spinnerCountry.setSelection(0);
    }

    private void uploadRecipeData() {
        String recipeName = inputRecipeName.getText().toString().trim();
        String description = inputDescription.getText().toString().trim();
        String ingredients = inputIngredients.getText().toString().trim();
        String steps = inputSteps.getText().toString().trim();
        String cookingTime = spinnerCookingTime.getSelectedItem().toString();
        String servings = spinnerServings.getSelectedItem().toString();
        String country = spinnerCountry.getSelectedItem().toString();
        String userName = (currentUser != null) ? currentUser.getDisplayName() : "Unknown";

        if (recipeName.isEmpty() || description.isEmpty() || ingredients.isEmpty() || steps.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đủ thông tin công thức!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri != null) {
            StorageReference fileReference = storageReference.child(System.currentTimeMillis() + ".jpg");
            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                String imageUrl = uri.toString();
                                // Tạo đối tượng Recipe
                                Recipe recipe = new Recipe(recipeName, description, ingredients, steps, imageUrl, userName, cookingTime, servings, country);
                                saveRecipeToDatabase(recipe);
                            }))
                    .addOnFailureListener(e -> {
                        Toast.makeText(AddRecipe.this, "Lỗi tải ảnh lên Firebase", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void saveRecipeToDatabase(Recipe recipe) {
        String recipeId = recipeRef.push().getKey(); // Tạo ID tự động cho công thức
        if (recipeId != null) {
            recipeRef.child(recipeId).setValue(recipe)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(AddRecipe.this, "Công thức đã được lưu!", Toast.LENGTH_SHORT).show();
                        finish(); // Đóng Activity sau khi lưu
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(AddRecipe.this, "Lỗi khi lưu công thức!", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Nhận kết quả từ activity chọn ảnh
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            selectedImageView.setImageURI(imageUri); // Hiển thị ảnh lên ImageView
        }
    }
}