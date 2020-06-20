package ir.cafebazaar.notepad.activities.home;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.navigation.NavigationView;
import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.callback.SelectCallback;
import com.huantansheng.easyphotos.models.album.entity.Photo;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import ir.cafebazaar.notepad.R;
import ir.cafebazaar.notepad.activities.editfolders.EditFoldersActivity;
import ir.cafebazaar.notepad.activities.editfolders.EditFoldersActivityIntentBuilder;
import ir.cafebazaar.notepad.database.FoldersDAO;
import ir.cafebazaar.notepad.models.Folder;
import ir.cafebazaar.notepad.utils.GlideEngine;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MohMah on 8/17/2016.
 * Tony Stalk Note Pad
 */
public class HomeActivity extends AppCompatActivity{
	private static final String TAG = "HomeActivity";
	private static final int ALL_NOTES_MENU_ID = -1;
	private static final int EDIT_FOLDERS_MENU_ID = -2;
	private static final int SAVE_DATABASE_MENU_ID = -3;
	private static final int IMPORT_DATABASE_MENU_ID = -4;

	private static final int USER_LOGIN_MENU_ID = -5;

	private static final int PHOTO_MENU_ID = -6;

	@BindView(R.id.navigation_view) NavigationView mNavigationView;
	@BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
	List<Folder> latestFolders;
	BackupRestoreDelegate backupRestoreDelegate;

	@Override protected void onCreate(@Nullable Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		ButterKnife.bind(this);
		mDrawerLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener(){
			@Override public void onGlobalLayout(){
				mDrawerLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				setFragment(null);
			}
		});
		backupRestoreDelegate = new BackupRestoreDelegate(this);
		if (getIntent().getData() != null) backupRestoreDelegate.handleFilePickedWithIntentFilter(getIntent().getData());
			mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){
				@Override public boolean onNavigationItemSelected(MenuItem item){
					Log.e(TAG, "onNavigationItemSelected() called with: " + "item id = [" + item.getItemId() + "]");
					int menuId = item.getItemId();
					if (menuId == ALL_NOTES_MENU_ID){
						setFragment(null);
					}else if (menuId == EDIT_FOLDERS_MENU_ID){
						startActivity(new EditFoldersActivityIntentBuilder().build(HomeActivity.this));
					}else if (menuId == SAVE_DATABASE_MENU_ID){
						backupRestoreDelegate.backupDataToFile();
					}else if (menuId == IMPORT_DATABASE_MENU_ID){
						backupRestoreDelegate.startFilePickerIntent();
					}else if (menuId == USER_LOGIN_MENU_ID){
						Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
						startActivity(intent );
					}else if (menuId == PHOTO_MENU_ID ){
						Intent intent = new Intent(HomeActivity.this, SsfPhotoCameraActivity.class);
//						Intent intent = new Intent(HomeActivity.this, SimpleActivity.class);
						startActivity(intent );
//						EasyPhotos.createAlbum(HomeActivity.this, true, GlideEngine.getInstance())
//								.setFileProviderAuthority("ir.cafebazaar.notepad.fileprovider")
//								.start(101);
					}else{
						setFragment(FoldersDAO.getFolder(menuId));
					}
					mDrawerLayout.closeDrawer(Gravity.LEFT);
					inflateNavigationMenus(menuId);
					return true;
				}
			});
	}

	@Override protected void onStart(){
		super.onStart();
		inflateNavigationMenus(ALL_NOTES_MENU_ID);

		requestPermission();
	}

	public void requestPermission() {
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
				ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

			Toast.makeText(this, "申请权限", Toast.LENGTH_SHORT).show();
			// 申请 相机 麦克风权限
			ActivityCompat.requestPermissions(this, new String[]{
					Manifest.permission.WRITE_EXTERNAL_STORAGE,
					Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
		}
	}

	public void inflateNavigationMenus(int checkedItemId){
		Menu menu = mNavigationView.getMenu();
		menu.clear();
		menu
				.add(Menu.NONE, ALL_NOTES_MENU_ID, Menu.NONE, "Notes")
				.setIcon(R.drawable.ic_note_white_24dp)
				.setChecked(checkedItemId == ALL_NOTES_MENU_ID);
		final SubMenu subMenu = menu.addSubMenu("Folders");
		latestFolders = FoldersDAO.getLatestFolders();
		for (Folder folder : latestFolders){
			subMenu
					.add(Menu.NONE, folder.getId(), Menu.NONE, folder.getName())
					.setIcon(R.drawable.ic_folder_black_24dp)
					.setChecked(folder.getId() == checkedItemId);
		}
		menu
				.add(Menu.NONE, EDIT_FOLDERS_MENU_ID, Menu.NONE, "Create or edit folders")
				.setIcon(R.drawable.ic_add_white_24dp);
		SubMenu backupSubMenu = menu.addSubMenu("Backup and restore");
		backupSubMenu
				.add(Menu.NONE, SAVE_DATABASE_MENU_ID, Menu.NONE, "Backup data")
				.setIcon(R.drawable.ic_save_white_24dp);

		backupSubMenu
				.add(Menu.NONE, IMPORT_DATABASE_MENU_ID, Menu.NONE, "Restore data")
				.setIcon(R.drawable.ic_restore_white_24dp);
		// Login
		backupSubMenu
				.add(Menu.NONE, USER_LOGIN_MENU_ID, Menu.NONE, "Login User")
				.setIcon(R.drawable.icon_user_login);
		backupSubMenu
				.add(Menu.NONE, PHOTO_MENU_ID, Menu.NONE, "相册")
				.setIcon(R.drawable.icon_user_login);
	}

	@Override public void onBackPressed(){
		if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)){
			mDrawerLayout.closeDrawer(Gravity.LEFT);
		}else{
			super.onBackPressed();
		}
	}

	public void setFragment(Folder folder){
		// Create a new fragment and specify the fragment to show based on nav item clicked
		Fragment fragment = new NoteListFragment();
		if (folder != null){
			Bundle bundle = new Bundle();
			bundle.putParcelable(NoteListFragment.FOLDER, folder);
			fragment.setArguments(bundle);
		}
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
	}

	@Override protected void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == BackupRestoreDelegate.PICK_RESTORE_FILE_REQUEST_CODE){
			backupRestoreDelegate.handleFilePickedWithFilePicker(resultCode, data);
		}
	}
}
