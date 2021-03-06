package br.org.cesar.discordtime.stickysessions.ui.session;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

import javax.inject.Inject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import br.org.cesar.discordtime.stickysessions.R;
import br.org.cesar.discordtime.stickysessions.app.StickySessionApplication;
import br.org.cesar.discordtime.stickysessions.domain.model.Note;
import br.org.cesar.discordtime.stickysessions.presentation.session.SessionContract;
import br.org.cesar.discordtime.stickysessions.ui.ExtraNames;
import br.org.cesar.discordtime.stickysessions.ui.adapters.NoteAdapter;
import br.org.cesar.discordtime.stickysessions.ui.session.custom.ItemAnimator;
import br.org.cesar.discordtime.stickysessions.ui.session.custom.NoteGridLayoutManager;

public class SessionActivity extends AppCompatActivity implements SessionContract.View,
        View.OnClickListener, NoteAdapter.NoteAdapterCallback {

    private final static String TAG = "SessionActivity";
    private Context mContext;
    private ViewGroup parent;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;

    @Inject
    SessionContract.Presenter mPresenter;

    private NoteAdapter mNoteAdapter;
    private View mAddNewNoteView;
    private Animation mAnimationShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        ((StickySessionApplication)getApplicationContext()).inject(this);
        mContext = this;

        bindView();
        configureSession();
    }
    public TextView textView;

    private void bindView() {
        parent = findViewById(R.id.container);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mPresenter.attachView(this);

        mAddNewNoteView = findViewById(R.id.add_note_view);
        mAddNewNoteView.setOnClickListener(this);
        mAddNewNoteView.setVisibility(View.INVISIBLE);

        mRecyclerView = findViewById(R.id.user_notes_recyclerview);
        mRecyclerView.setLayoutManager(
            new NoteGridLayoutManager(this,
                getResources().getInteger(R.integer.session_grid_elements_columns)));
        mRecyclerView.setItemAnimator(new ItemAnimator(this));

        mNoteAdapter = new NoteAdapter(this);
        mNoteAdapter.setCallback(this);

        mRecyclerView.setAdapter(mNoteAdapter);
        mAnimationShow = AnimationUtils.loadAnimation(this, R.anim.show_animation);

        mProgressBar = findViewById(R.id.progress_bar);
    }

    private void configureSession() {
        Intent intent = getIntent();
        //Enter in a session by link
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri uri = intent.getData();
            if (uri != null) {
                String sessionId = uri.getQueryParameter(ExtraNames.SESSION_ID);
                Log.d(TAG, "sessionId " + sessionId);
                mPresenter.currentSession(sessionId);
            } else {
                //TODO error message to null data
                Log.d(TAG, "null sessionId.");
            }
            //Enter in a session by Lobby
        } else if(!TextUtils.isEmpty(intent.getStringExtra(ExtraNames.SESSION_ID))) {
            String sessionId = intent.getStringExtra(ExtraNames.SESSION_ID);
            Log.d(TAG, "sessionId " + sessionId);
            mPresenter.currentSession(sessionId);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        cleanNotes();
        configureSession();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.session_menu, menu);
        return true;
    }

    @Override
    public void onClick(android.view.View view) {
        switch (view.getId()) {
            case R.id.add_note_view:
                mPresenter.onAddNoteClicked();
                break;

            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_share) {
            mPresenter.onShareSession();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void displayAddNoteDialog(final List<String> topics) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        final android.view.View view =
            inflater.inflate(R.layout.dialog_add_new_note, parent, false);

        final ChipGroup chipGroup = view.findViewById(R.id.session_elements);

        for (int i = 0; i < topics.size(); i++) {
            String topic = topics.get(i);
            Chip chip = (Chip) inflater.inflate(R.layout.chip_element, parent, false);
            chip.setText(topic);
            chip.setId(i);
            chipGroup.addView(chip);
        }

        final EditText editText = view.findViewById(R.id.note_description);
        textView = view.findViewById(R.id.char_count);
        editText.addTextChangedListener(descriptionWatcher);

        builder.setView(view);


        builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int position = chipGroup.getCheckedChipId();
                if (position >= 0) {
                    String topic = topics.get(position);

                    String description = editText.getText().toString();

                    mPresenter.addNewNote(topic, description);
                } else {
                    showShouldChoiceTopicMessage();
                }
            }
        });

        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private final TextWatcher descriptionWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }

        @Override
        public void afterTextChanged(Editable s) {
            textView.setText(String.valueOf(s.length()));
        }
    };

    private void showShouldChoiceTopicMessage() {
        Toast.makeText(this, R.string.select_topic_dialog_message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void displayNoteContent(Note note) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        final android.view.View view =
            inflater.inflate(R.layout.note_element_dialog, parent, false);

        TextView title = view.findViewById(R.id.title_note_element);
        title.setText(note.topic);

        TextView content = view.findViewById(R.id.description_note_element);
        content.setText(note.description);

        builder.setPositiveButton(getString(R.string.remove), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPresenter.removeNote(note);
            }
        });

        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.setView(view);
        builder.show();
    }

    @Override
    public void addNoteToNoteList(Note note) {
        mNoteAdapter.addNote(note);
        mRecyclerView.scrollToPosition(mNoteAdapter.getItemCount() - 1);
    }

    @Override
    public void cleanNotes() {
        mNoteAdapter = new NoteAdapter(this);
        mRecyclerView.setAdapter(mNoteAdapter);
    }

    @Override
    public void displaySession() {
        mAddNewNoteView.startAnimation(mAnimationShow);
        mAddNewNoteView.setVisibility(View.VISIBLE);
    }

    @Override
    public void displayError(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void startLoadingAllNotes() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void stopLoadingAllNotes() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void startLoadingNote() {
        mNoteAdapter.startLoading();
        mAddNewNoteView.setEnabled(false);
    }

    @Override
    public void stopLoadingNote() {
        mNoteAdapter.stopLoading();
        mAddNewNoteView.setEnabled(true);
    }

    @Override
    public void shareSession(String sessionId){
        Intent sendIntent=new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT,
        String.format(getString(R.string.share_session),sessionId));
        startActivity(sendIntent);
    }

    @Override
    public void displayNotes(List<Note> notes) {
        mNoteAdapter.setNotes(notes);
    }

    @Override
    public void displayErrorInvalidNotes() {
        Toast.makeText(mContext, "Invalid Notes for Session", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    @Override
    public void onItemClicked(Note note) {
        mPresenter.onNoteWidgetClicked(note);
    }

    @Override
    public void removeNote(Note note) {
        mNoteAdapter.removeNote(note);
    }
}
