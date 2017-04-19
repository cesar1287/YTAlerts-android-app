package free.ytalerts.app.gui.activities;

import android.content.ActivityNotFoundException;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import free.ytalerts.app.R;
import free.ytalerts.app.businessobjects.MainActivityListener;
import free.ytalerts.app.businessobjects.YouTubeChannel;
import free.ytalerts.app.gui.fragments.ChannelBrowserFragment;
import free.ytalerts.app.gui.fragments.MainFragment;
import free.ytalerts.app.gui.fragments.SearchVideoGridFragment;

/**
 * Main activity (launcher).  This activity holds {@link free.ytalerts.app.gui.fragments.VideosGridFragment}.
 */
public class MainActivity extends AppCompatActivity implements MainActivityListener {
	public static final String ACTION_VIEW_CHANNEL = "MainActivity.ViewChannel";
	public static final String MAIN_FRAGMENT = "MainActivity.MainFragment";
	public static final String CHANNEL_BROWSER_FRAGMENT = "MainActivity.ChannelBrowserFragment";
	public static final String SEARCH_FRAGMENT = "MainActivity.SearchFragment";

	@Bind(R.id.fragment_container)
	FrameLayout fragmentContainer;

	MainFragment mainFragment;
	ChannelBrowserFragment channelBrowserFragment;
	SearchVideoGridFragment searchVideoGridFragment;

	SharedPreferences sharedPref;
	SharedPreferences.Editor editor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);

		String action = getIntent().getAction();

		sharedPref = getPreferences(Context.MODE_PRIVATE);
		editor = sharedPref.edit();

		int numExecApp = sharedPref.getInt(getString(R.string.number_exec_app), 0);

		if(action==null) {
			editor.putInt(getString(R.string.number_exec_app), numExecApp + 1);
			editor.apply();
		}

		if(numExecApp==20){
			rateItApp();
		}

		if (fragmentContainer != null) {

			if (savedInstanceState != null) {
				mainFragment = (MainFragment) getSupportFragmentManager().getFragment(savedInstanceState, MAIN_FRAGMENT);
				channelBrowserFragment = (ChannelBrowserFragment) getSupportFragmentManager().getFragment(savedInstanceState, CHANNEL_BROWSER_FRAGMENT);
				searchVideoGridFragment = (SearchVideoGridFragment) getSupportFragmentManager().getFragment(savedInstanceState, SEARCH_FRAGMENT);
			}
			if (action != null && action.equals(ACTION_VIEW_CHANNEL)) {

				//YouTubeChannel channel = (YouTubeChannel) getIntent().getSerializableExtra(ChannelBrowserFragment.CHANNEL_OBJ);
				//onChannelClick(channel);
				String channel = (String) getIntent().getSerializableExtra(ChannelBrowserFragment.CHANNEL_ID);
				onChannelClick(channel);
			} else {
				if (mainFragment == null) {
					mainFragment = new MainFragment();
					getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mainFragment).commit();
				}
			}
		}
	}

	private void launchMarket() {
		Uri uri = Uri.parse("market://details?id=" + getPackageName());
		Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
		try {
			startActivity(myAppLinkToMarket);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, " unable to find market app", Toast.LENGTH_LONG).show();
		}
	}

	public void rateItApp(){
		//Cria o gerador do AlertDialog
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		//define o titulo
		builder.setTitle("Nos avalie");
		//define a mensagem
		builder.setMessage("Parece que você está gostando do aplicativo, que bom!!! Gostaria de nos avaliar na Play Store agora?");
		//define um botão como positivo
		builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				launchMarket();
				editor.putInt(getString(R.string.number_exec_app), -1000000);
				editor.apply();
			}
		});
		//define um botão como negativo.
		builder.setNegativeButton("Depois", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				editor.putInt(getString(R.string.number_exec_app), 0);
				editor.apply();
			}
		});
		//cria o AlertDialog
		AlertDialog alert = builder.create();
		//Exibe
		alert.show();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		if (mainFragment != null)
			getSupportFragmentManager().putFragment(outState, MAIN_FRAGMENT, mainFragment);
		if (channelBrowserFragment != null && channelBrowserFragment.isVisible())
			getSupportFragmentManager().putFragment(outState, CHANNEL_BROWSER_FRAGMENT, channelBrowserFragment);
		if (searchVideoGridFragment != null && searchVideoGridFragment.isVisible())
			getSupportFragmentManager().putFragment(outState, SEARCH_FRAGMENT, searchVideoGridFragment);
		super.onSaveInstanceState(outState);
	}


	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_activity_menu, menu);

		// setup the SearchView (actionbar)
		final MenuItem searchItem = menu.findItem(R.id.menu_search);
		final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

		searchView.setQueryHint(getString(R.string.search_videos));
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextChange(String newText) {
				return false;
			}

			@Override
			public boolean onQueryTextSubmit(String query) {
				// hide the keyboard
				searchView.clearFocus();

				// open SearchVideoGridFragment and display the results
				searchVideoGridFragment = new SearchVideoGridFragment();
				Bundle bundle = new Bundle();
				bundle.putString(SearchVideoGridFragment.QUERY, query);
				searchVideoGridFragment.setArguments(bundle);
				switchToFragment(searchVideoGridFragment);

				return true;
			}
		});

		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_about:
				Intent u = new Intent(this, AboutActivity.class);
				startActivity(u);
				return true;
			case R.id.menu_doubts:
				Intent j = new Intent(this, DoubtsActivity.class);
                startActivity(j);
				return true;
			case R.id.menu_preferences:
				Intent i = new Intent(this, PreferencesActivity.class);
				startActivity(i);
				return true;
			case R.id.menu_enter_video_url:
				displayEnterVideoUrlDialog();
				return true;
			case android.R.id.home:
				if (mainFragment == null || !mainFragment.isVisible()) {
					onBackPressed();
					return true;
				}
		}

		return super.onOptionsItemSelected(item);
	}


	/**
	 * Display the Enter Video URL dialog.
	 */
	private void displayEnterVideoUrlDialog() {
		final AlertDialog alertDialog = new AlertDialog.Builder(this)
				.setView(R.layout.dialog_enter_video_url)
				.setTitle(R.string.enter_video_url)
				.setPositiveButton(R.string.play, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
                    // get the inputted URL string
                    final String videoUrl = ((EditText) ((AlertDialog) dialog).findViewById(R.id.dialog_url_edittext)).getText().toString();

                    // play the video
                    Intent i = new Intent(MainActivity.this, YouTubePlayerActivity.class);
                    i.setAction(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(videoUrl));
                    startActivity(i);
					}
				})
				.setNegativeButton(R.string.cancel, null)
				.show();

		// paste whatever there is in the clipboard (hopefully it is a video url)
		((EditText) alertDialog.findViewById(R.id.dialog_url_edittext)).setText(getClipboardItem());

		// clear URL edittext button
		alertDialog.findViewById(R.id.dialog_url_clear_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((EditText) alertDialog.findViewById(R.id.dialog_url_edittext)).setText("");
			}
		});
	}


	/**
	 * Return the last item stored in the clipboard.
	 *
	 * @return    {@link String}
	 */
	private String getClipboardItem() {
		String item = "";

		ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
		if (clipboard.hasPrimaryClip()) {
			android.content.ClipDescription description = clipboard.getPrimaryClipDescription();
			android.content.ClipData data = clipboard.getPrimaryClip();
			if (data != null && description != null && description.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN))
				item = String.valueOf(data.getItemAt(0).getText());
		}

		return item;
	}


	@Override
	public void onBackPressed() {
		// If coming here from the video player (channel was pressed), exit when the back button is pressed
		if (getIntent().getAction() != null && getIntent().getAction().equals(ACTION_VIEW_CHANNEL))
			finish();
		else
			super.onBackPressed();
	}


	private void switchToFragment(Fragment fragment) {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

		transaction.replace(R.id.fragment_container, fragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}


	@Override
	public void onChannelClick(YouTubeChannel channel) {
		Bundle args = new Bundle();
		args.putSerializable(ChannelBrowserFragment.CHANNEL_OBJ, channel);
		switchToChannelBrowserFragment(args);
	}


	@Override
	public void onChannelClick(String channelId) {
		Bundle args = new Bundle();
		args.putString(ChannelBrowserFragment.CHANNEL_ID, channelId);
		switchToChannelBrowserFragment(args);
	}


	private void switchToChannelBrowserFragment(Bundle args) {
		channelBrowserFragment = new ChannelBrowserFragment();
		channelBrowserFragment.setArguments(args);
		switchToFragment(channelBrowserFragment);
	}
}