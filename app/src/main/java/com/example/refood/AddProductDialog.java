package com.example.refood;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Date;

public class AddProductDialog extends BottomSheetDialogFragment {

    private final int GALLERY_REQ_CODE = 1000;
    private final int GALLERY_REQ_CODE_MAIN_IMAGE = 1001;

    public static Bundle mMyFragmentBundle = new Bundle();

    FirebaseAuth firebaseAuth;
    FirebaseStorage storage;
    String image_path = "";
    RecyclerView recyclerView;
    StepsAdapter stepsAdapter;
    ArrayList <Step> steps;
    FirebaseFirestore db;
    ImageView imageView;
    UpdateCall updateCall;

    public AddProductDialog(UpdateCall updateCall) {
        this.updateCall = updateCall;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_product, container, false);

        storage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        steps = new ArrayList<>();

        Spinner spinner = v.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.categories_food, R.layout.item_for_category_spinner);
        spinner.setAdapter(adapter);

        Button apply_button = v.findViewById(R.id.apply_button);
        imageView = v.findViewById(R.id.imageView);
        EditText title = v.findViewById(R.id.editText_title);
        EditText info = v.findViewById(R.id.editText_info);
        ImageView imageView = v.findViewById(R.id.imageView);
        View add_step_button = v.findViewById(R.id.add_step_button);
        recyclerView = v.findViewById(R.id.recycler_steps);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        stepsAdapter = new StepsAdapter(steps, v.getContext(), true, new StepsAdapter.AdapterCallback() {
            @Override
            public void onMethodCallback(String data, int position) {
                Intent iGallery = new Intent(Intent.ACTION_PICK);
                iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                mMyFragmentBundle.putInt("pos", position);
                startActivityForResult(iGallery, GALLERY_REQ_CODE);

            }
        }, new StepsAdapter.AdapterDeleteItemCallback() {
            @Override
            public void deleteAt(int position) {
                steps.remove(position);
                stepsAdapter.notifyItemRemoved(position);
                stepsAdapter.notifyItemRangeChanged(position, steps.size());
            }
        });
        recyclerView.setAdapter(stepsAdapter);

        add_step_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                steps.add(new Step("", steps.size() + 1, "", null));
                stepsAdapter.notifyItemInserted(steps.size() - 1);
            }
        });

        apply_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(title.getText().toString().equals("") || info.getText().toString().equals("") || image_path.equals(""))) {
                    db.collection(Post.COLLECTION_NAME).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (int x = recyclerView.getChildCount(), i = 0; i < x; i++) {
                                    StepsAdapter.ViewHolder holder = (StepsAdapter.ViewHolder) recyclerView.getChildViewHolder(recyclerView.getChildAt(i));
                                    Step step = steps.get(i);
                                    step.setInfo(holder.getInfo().getText().toString());
                                    step.setTime(holder.getTime().getText().toString());
                                    step.setNumber(i + 1);
                                }
                                Post post = new Post("i'm", "me", title.getText().toString(), info.getText().toString(), image_path, new Date(), true, 0, 0, steps, new ArrayList<>(), new ArrayList<>(), spinner.getSelectedItem() + "", spinner.getSelectedItemPosition());
                                if (Post.saveRecipe(post, getContext().getFilesDir() + "/Recipes", getContext().getContentResolver(), getContext())) {
                                    System.out.println("успешно сохранено");
                                } else {
                                    System.out.println("Ошибка!!!!!!!!!!!");
                                }
                                updateCall.update(post);
                                dismiss();
                            } else {
                                Toast.makeText(getContext(), R.string.fill, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }

            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iGallery = new Intent(Intent.ACTION_PICK);
                iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(iGallery, GALLERY_REQ_CODE_MAIN_IMAGE);
            }
        });


        return v;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {
            if (requestCode == GALLERY_REQ_CODE && data != null) {
                int pos = mMyFragmentBundle.getInt("pos");
                steps.get(pos).setImagePath(data.getData().toString());
                stepsAdapter.notifyItemChanged(pos);
            } else if (requestCode == GALLERY_REQ_CODE_MAIN_IMAGE && data != null) {
                image_path = data.getData().toString();
                imageView.setImageURI(data.getData());
            }

        }
}
public interface UpdateCall {
        void update(Post post);
}



}