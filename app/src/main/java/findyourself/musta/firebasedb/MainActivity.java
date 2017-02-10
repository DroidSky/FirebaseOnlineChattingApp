package findyourself.musta.firebasedb;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

	private ListView mChatRoomList = null;
	private Button mBtnAddChatRoomName = null;
	private EditText mEtChatRoomName = null;
	private ArrayList<String> mListOfRooms = new ArrayList<>();
	private ArrayAdapter<String> arrayAdapter = null;
	private String username = null;
	boolean userEntered = false, onBackPressed = false, okElseClicked = false;
	private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				/*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
						.setAction("Action", null).show();*/

			}
		});
		mChatRoomList = (ListView) findViewById(R.id.chat_room_list);
		mBtnAddChatRoomName = (Button) findViewById(R.id.btn_add_chat_room_name);
		mEtChatRoomName = (EditText) findViewById(R.id.et_add_chat_room_name);
		arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mListOfRooms);
		mChatRoomList.setAdapter(arrayAdapter);
		addUsername();
		mBtnAddChatRoomName.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put(mEtChatRoomName.getText().toString(), "");
				//mListOfRooms.add(mEtChatRoomName.getText().toString());
				root.updateChildren(map);
				mEtChatRoomName.setText("");
			}
		});
		root.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				Set<String> set = new HashSet<String>();
				Iterator iterator = dataSnapshot.getChildren().iterator();
				while (iterator.hasNext()){
					set.add(((DataSnapshot)iterator.next()).getKey());
				}
				mListOfRooms.clear();
				mListOfRooms.addAll(set);
				arrayAdapter.notifyDataSetChanged();
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {

			}
		});
		mChatRoomList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(getApplicationContext(), GoppoActivity.class);
				intent.putExtra("room_name", ((TextView)view).getText().toString());
				intent.putExtra("username", username);
				startActivity(intent);
			}
		});
	}

	private void addUsername(){
		//userEntered = false;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Give your username");
		final EditText nameField = new EditText(this);
		builder.setView(nameField);
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (!nameField.getText().toString().isEmpty())
				{
					username = nameField.getText().toString();
					userEntered = true;
				}
				else {
					Toast.makeText(getApplicationContext(), "No user info provided.", Toast.LENGTH_SHORT).show();
					//userEntered = true;
					okElseClicked = true;
					addUsername();
				}
			}
		});
		builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
					if (okElseClicked)
					{
						Toast.makeText(getApplicationContext(), "Enter username.", Toast.LENGTH_SHORT).show();
						addUsername();
					}
					else if (!userEntered)
					{
						Toast.makeText(getApplicationContext(), "Exiting application as your wish.", Toast.LENGTH_SHORT).show();
						MainActivity.this.finish();
					}
				}
			});
		}
		builder.show();
	}

	@Override
	public void onBackPressed() {
		onBackPressed = true;
		super.onBackPressed();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
