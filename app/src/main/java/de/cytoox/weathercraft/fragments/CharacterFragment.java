package de.cytoox.weathercraft.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import de.cytoox.weathercraft.R;
import de.cytoox.weathercraft.listener.AsyncResponse;
import de.cytoox.weathercraft.util.CharacterAPI;

/**
 * The type Character fragment.
 *
 * @author Marcel Steffen
 */
public class CharacterFragment extends Fragment implements AsyncResponse {
    /**
     * The View.
     */
    View view;

    private CharacterAPI characterHead;
    private CharacterAPI character;
    private Button setChar;
    private EditText selectChar;

    /**
     * The constant SHARED_PREFS.
     */
    public static final String SHARED_PREFS = "charName";

    public CharacterFragment() {}
    /**
     * Instantiates a new Character fragment.
     *
     * @param character     the character
     * @param characterHead the character head
     */
    public CharacterFragment(CharacterAPI character, CharacterAPI characterHead) {
        this.character = character;
        this.characterHead = characterHead;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_character, container, false);

        selectChar = view.findViewById(R.id.selectedCharEditText);
        setChar = view.findViewById(R.id.selectCharSetButton);

        selectChar.setText(character.getName());

        setChar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCharacterName();
            }
        });

        refreshViews();

        return view;
    }

    /**
     * Save username in shared preferences.
     */
    public void saveCharacterName() {
        //write new name in sharedPreferences
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("text", selectChar.getText().toString());
        editor.apply();

        //write new name to character object
        this.character.setName(selectChar.getText().toString());

        //request new character head
        CharacterAPI NewCharacterHead = new CharacterAPI(selectChar.getText().toString(), 10, true);
        NewCharacterHead.delegate = this;
        NewCharacterHead.execute();

        Toast.makeText(this.getContext(), "Gespeichert: " + selectChar.getText(), Toast.LENGTH_LONG).show();
    }

    /**
     * Refresh views.
     */
    public void refreshViews() {
        ImageView selectedCharImage = view.findViewById(R.id.selectedCharImageView);
        selectedCharImage.setImageBitmap(characterHead.getBitmap());
    }

    @Override
    public void processFinish(Object result) {
        this.characterHead.setRender((String)result);
        this.refreshViews();
    }
}