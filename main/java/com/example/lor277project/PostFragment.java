package com.example.lor277project;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import android.widget.AdapterView;
import android.widget.TextView;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import java.util.Arrays;
import com.google.android.libraries.places.api.model.Place;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.maps.OnMapReadyCallback;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

public class PostFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private ImageView photo;
    private Uri imageUri;
    private TextView time;
    private EditText name,location, price, description;
    private Spinner spinner;
    private Button post;
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private ProgressBar progressBar;
    private static String time_="";
    private static String category="None";
    private static String uri_ = "";
    private static String description_="";
    private static String price_="";
    private static String name_="";
    private static String location_="";
    public PostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post, container, false);
        photo = (ImageView) view.findViewById(R.id.photo);
        name = (EditText) view.findViewById(R.id.name);
        location = (EditText) view.findViewById(R.id.location);
        price = (EditText) view.findViewById(R.id.price);
        description = (EditText) view.findViewById(R.id.description);
        time = (TextView) view.findViewById(R.id.time);
        post = (Button) view.findViewById(R.id.post);
        mAuth = FirebaseAuth.getInstance();
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        spinner = view.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),R.array.category, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.clear();
        CalendarConstraints.Builder constraintBuilder = new CalendarConstraints.Builder();
        constraintBuilder.setValidator(DateValidatorPointForward.now());
        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("SELECT AVAILABLE DATE");
        long today = MaterialDatePicker.todayInUtcMilliseconds();
        calendar.setTimeInMillis(today);
        //builder.setSelection(today);
        builder.setCalendarConstraints(constraintBuilder.build());
        final MaterialDatePicker materialDatePicker = builder.build();


        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent , 2);
            }
        });

        /*location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Initialize Places.
                Places.initialize(getContext(), String.valueOf(R.string.google_api_key));

                // Create a new Places client instance.
                PlacesClient placesClient = Places.createClient(getActivity());

                // Set the fields to specify which types of place data to return.
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);
// Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.OVERLAY, fields)
                        .build(getActivity());
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);

            }});*/


        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDatePicker.show(getFragmentManager(),"DATE_PICKER");

            }
        });


        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                time_ = materialDatePicker.getHeaderText();
                time.setText(time_);
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (imageUri == null){
                    Toast.makeText(getActivity(), "Please Upload a photo!", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                name_ = name.getText().toString().trim();
                description_ = description.getText().toString().trim();
                price_ = price.getText().toString().trim();
                location_ = location.getText().toString().trim();
                String uid = mAuth.getCurrentUser().getUid();

                //if (!(category.equals("None")) && !(uri_.equals("")) && !(name_.equals("")) && !(description_.equals("")) && !(price_.equals("")) && !(location_.equals("")) && !(time_.equals(""))) {
                if (category.equals("None") || name_.isEmpty() || description_.isEmpty() || price_.isEmpty() || location_.isEmpty() || time_ == "") {
                    Toast.makeText(getActivity(), "Please fill in all fields!", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                    storage = FirebaseStorage.getInstance();
                    storageReference = storage.getReference();

                    final String randomKey1 = UUID.randomUUID().toString();
                    final String iid = UUID.randomUUID().toString();
                    StorageReference ref = storageReference.child("images/" + randomKey1);
                    ref.putFile(imageUri);
                    uri_ = imageUri.toString();
                    /*uri_ = ref.getDownloadUrl().toString();
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                       @Override
                       public void onSuccess(Uri uri) {

                           uri_ = uri.toString();
                       }
                   });*/


                    Item item = new Item(iid, uid, uri_, category, name_, description_, price_, location_, time_);
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("Items");
                    myRef.child(iid).setValue(item).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(), "Posted successfully!", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                                startActivity(new Intent(getActivity(), TabsActivity.class));
                            } else {
                                Toast.makeText(getActivity(), "Failed to post item, please try again", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                    // }
                    //else{
                    //  Toast.makeText(getActivity(), "Please fill in all fields!", Toast.LENGTH_LONG).show();
                    //}
                }

        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode ==2 && resultCode == Activity.RESULT_OK && data != null){

            imageUri = data.getData();
            photo.setImageURI(imageUri);

        }

       else if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                location.setText("Place: " + place.getName() + ", " + place.getId());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Toast.makeText(getActivity(), status.getStatusMessage(),Toast.LENGTH_LONG).show();

            } else if (resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
         category = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}