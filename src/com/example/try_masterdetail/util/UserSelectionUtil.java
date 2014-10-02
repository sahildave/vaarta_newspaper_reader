package com.example.try_masterdetail.util;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.try_masterdetail.R;
import com.example.try_masterdetail.homescreen.adapter.CellModel;
import com.example.try_masterdetail.homescreen.adapter.NewsCatModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

public class UserSelectionUtil {
	private String TAG = "HomeScreen";

	private static Context context;

	public UserSelectionUtil(Context context) {
		this.context = context;
	}

	public void updateUserSelectionSharedPrefs() {

        List<CellModel> cellListFromPrefs = CellListUtil.getCellListFromSharedPrefs();
        HashMap<Integer, boolean[]> mChildCheckStates = new HashMap<Integer, boolean[]>();

        for (CellModel objects : cellListFromPrefs) {
            String npImage = objects.getNewspaperImage();
            String catName = objects.getTitleCategory();

            if (npImage!=null && !npImage.equals("add_new")) {
                CsvFileUtil readCsv = new CsvFileUtil(context);
                NewsCatModel csvObject = readCsv.getObjectByNPImage(npImage, catName);

                if (csvObject != null && csvObject.getNpId() != null) {
                    String npIdString = csvObject.getNpId();
					String catIdString = csvObject.getCatId();

					int npIdint = Integer.parseInt(npIdString);
					int catIdint = Integer.parseInt(catIdString);

					if (!mChildCheckStates.containsKey(npIdint)) {
						boolean value[] = new boolean[5];
						mChildCheckStates.put(npIdint, value);
					}

					mChildCheckStates.get(npIdint)[catIdint] = true;
                }
                readCsv.closeUtilObject();
                saveUserSelectionSharedPrefs(mChildCheckStates);
            }
        }

    }

	public void saveUserSelectionSharedPrefs(HashMap<Integer, boolean[]> mChildCheckStates) {

		Gson gson = new Gson();
		String feedsChecked = gson.toJson(mChildCheckStates);
		SharedPreferences feedPrefs = context.getSharedPreferences("UserFeedSelection", Context.MODE_PRIVATE);
		SharedPreferences.Editor prefEditor = feedPrefs.edit();
		prefEditor.putString("userFeedSelection", feedsChecked);
		prefEditor.commit();
	}

    public static HashMap<Integer, boolean[]> getUserFeedSelection() {
        SharedPreferences sharedPref = context.getSharedPreferences("UserFeedSelection", Context.MODE_PRIVATE);
        String defValue = context.getResources().getString(R.string.pref_feeds_selected_defvalue);
        String feedList =  sharedPref.getString("userFeedSelection", defValue);

        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<Integer, boolean[]>>() {}.getType();

        return gson.fromJson(feedList, type);
    }
}