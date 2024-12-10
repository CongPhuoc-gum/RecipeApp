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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AddRecipe extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageButton btn_back;
    private Uri imageUri;
    private ImageView selectedImageView;
    private EditText inputRecipeName, inputDescription, inputIngredients, inputSteps;
    private Spinner spinnerCookingTime, spinnerServings, spinnerCountry;
    private DatabaseReference recipeRef, usersRef;
    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_recipe);

        // Firebase setup
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        recipeRef = FirebaseDatabase.getInstance().getReference("Recipes");
        usersRef = FirebaseDatabase.getInstance().getReference("Users"); // Reference to "Users"
        storageReference = FirebaseStorage.getInstance().getReference("images");

        // Giao diện
        initializeUI();

        // Xử lý nút "Thêm Ảnh"
        findViewById(R.id.btn_add_image).setOnClickListener(view -> openFileChooser());

        // Xử lý nút "Submit"
        findViewById(R.id.button_submit).setOnClickListener(view -> uploadRecipeData());

        // Xử lý nút "Back"
        btn_back.setOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());
    }

    private void initializeUI() {
        btn_back = findViewById(R.id.btn_back);
        selectedImageView = findViewById(R.id.selected_image);
        inputRecipeName = findViewById(R.id.input_recipe_name);
        inputDescription = findViewById(R.id.input_description);
        inputIngredients = findViewById(R.id.input_ingredient);
        inputSteps = findViewById(R.id.input_steps);
        spinnerCookingTime = findViewById(R.id.spinner_cooking_time);
        spinnerServings = findViewById(R.id.spinner_servings);
        spinnerCountry = findViewById(R.id.spinner_country);

        setUpSpinners();
    }

    private void setUpSpinners() {
        // Spinner for Cooking Time
        String[] cookingTimes = {"10 phút", "20 phút", "30 phút", "40 phút", "50 phút" , "60 phút"};
        setUpSpinner(spinnerCookingTime, cookingTimes);

        // Spinner for Servings
        String[] servings = {"1 người", "2 người", "4 người", "6 người", "10 người"};
        setUpSpinner(spinnerServings, servings);

        // Spinner for Country
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
        setUpSpinner(spinnerCountry, countries);
    }

    private void setUpSpinner(Spinner spinner, String[] data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            selectedImageView.setImageURI(imageUri);
        }
    }

    private void uploadRecipeData() {
        String recipeName = inputRecipeName.getText().toString().trim();
        String description = inputDescription.getText().toString().trim();
        String ingredients = inputIngredients.getText().toString().trim();
        String steps = inputSteps.getText().toString().trim();
        String cookingTime = spinnerCookingTime.getSelectedItem().toString();
        String servings = spinnerServings.getSelectedItem().toString();
        String country = spinnerCountry.getSelectedItem().toString();

        if (currentUser == null) {
            Toast.makeText(this, "Bạn cần đăng nhập để thêm công thức!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userUid = currentUser.getUid();

        // Fetch the username from the Users node in Firebase Realtime Database
        usersRef.child(userUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userName = dataSnapshot.child("name").getValue(String.class);
                if (userName == null) {
                    userName = "Không rõ";  // Default name if not found
                }

                if (recipeName.isEmpty() || description.isEmpty() || ingredients.isEmpty() || steps.isEmpty() || imageUri == null) {
                    Toast.makeText(AddRecipe.this, "Vui lòng điền đầy đủ thông tin và chọn ảnh!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String recipeId = recipeRef.push().getKey(); // Generate unique recipe ID
                String fileName = System.currentTimeMillis() + ".jpg";
                StorageReference fileReference = storageReference.child(fileName);

                // Upload image to Firebase Storage
                String finalUserName = userName;
                fileReference.putFile(imageUri)
                        .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    String imageUrl = uri.toString();
                                    Recipe recipe = new Recipe(recipeId, recipeName, description, ingredients, steps, imageUrl, cookingTime, servings, country, finalUserName, userUid);
                                    saveRecipeToDatabase(recipe);
                                })
                                .addOnFailureListener(e -> Toast.makeText(AddRecipe.this, "Không thể lấy URL ảnh!", Toast.LENGTH_SHORT).show()))
                        .addOnFailureListener(e -> Toast.makeText(AddRecipe.this, "Tải ảnh lên thất bại!", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(AddRecipe.this, "Lỗi khi lấy thông tin người dùng!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveRecipeToDatabase(Recipe recipe) {
        // Use Firebase's `push()` method to generate a unique recipeId
        String recipeId = recipeRef.push().getKey(); // Generates a unique ID for the recipe

        if (recipeId != null) {
            recipe.setRecipeId(recipeId); // Set the generated recipeId to the recipe object

            // Save the recipe to the database
            recipeRef.child(recipeId).setValue(recipe)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(AddRecipe.this, "Công thức đã được lưu thành công!", Toast.LENGTH_SHORT).show();

                        // Now that the recipe is saved, navigate to RecipeDetail directly
                        Intent intent = new Intent(AddRecipe.this, RecipeDetail.class);
                        intent.putExtra("recipeId", recipeId);  // Only pass the recipeId to fetch data in RecipeDetail
                        startActivity(intent);  // Open RecipeDetail activity
                        finish();  // Close AddRecipe activity
                    })
                    .addOnFailureListener(e -> Toast.makeText(AddRecipe.this, "Lưu công thức thất bại!", Toast.LENGTH_SHORT).show());
        }
    }

}
