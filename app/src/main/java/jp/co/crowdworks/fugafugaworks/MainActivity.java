package jp.co.crowdworks.fugafugaworks;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private static final String MESSAGE_STORE = "message";
    private FirebaseListAdapter<Message> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        setupComposer();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // TODO: すごくてきとう
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user==null) {
            new UserLoginDialogFragment().show(getSupportFragmentManager(),"login");
        }
        else {
            new UserLogoutDialogFragment().show(getSupportFragmentManager(), "logout");
        }

        mAdapter = new FirebaseListAdapter<Message>(this, Message.class, android.R.layout.simple_list_item_1, getMessageRef()) {
            @Override
            protected void populateView(View v, Message model, int position) {
                ((TextView) v).setText(model.author+": "+model.content);
            }
        };
        ListView listview = (ListView) findViewById(R.id.listview);
        listview.setAdapter(mAdapter);
    }

    @Override
    protected void onPause() {
        if (mAdapter!=null) {
            mAdapter.cleanup();
            mAdapter = null;
        }
        super.onPause();
    }

    private DatabaseReference getMessageRef() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        return database.getReference(MESSAGE_STORE);
    }

    private void setupComposer() {
        findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = getTextString(R.id.txt_content);
                if(TextUtils.isEmpty(content)) return;

                sendMessage(content);
            }
        });
    }

    private String getTextString(@IdRes int txt) {
        return ((TextView) findViewById(txt)).getText().toString();
    }

    private void sendMessage(String content) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // TODO: すごくてきとう
        if(user==null) return;

        getMessageRef().push().setValue(new Message(user.getUid(), content)).continueWith(new Continuation<Void, Object>() {
            @Override
            public Object then(@NonNull Task<Void> task) throws Exception {
                if (!task.isSuccessful()) {
                    Log.e("FugaFugaWorks","error", task.getException());
                    return null;
                }

                ((TextView) findViewById(R.id.txt_content)).setText("");
                return null;
            }
        });
    }

}
