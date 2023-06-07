package nemosofts.driving.exam.asyncTask;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import nemosofts.driving.exam.callback.Callback;
import nemosofts.driving.exam.interfaces.CategoryListener;
import nemosofts.driving.exam.item.ItemCat;
import nemosofts.driving.exam.utils.ApplicationUtil;
import nemosofts.driving.exam.utils.DBHelper;
import okhttp3.RequestBody;

public class LoadCat extends AsyncTask<String, String, String> {

    private final RequestBody requestBody;
    private final CategoryListener categoryListener;
    private final ArrayList<ItemCat> arrayList = new ArrayList<>();
    private final DBHelper dbHelper;
    private String verifyStatus = "0", message = "";

    public LoadCat(Context context, CategoryListener categoryListener, RequestBody requestBody) {
        this.categoryListener = categoryListener;
        this.requestBody = requestBody;
        dbHelper = new DBHelper(context);
    }

    @Override
    protected void onPreExecute() {
        dbHelper.removeAllCat();
        categoryListener.onStart();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            String json = ApplicationUtil.responsePost(Callback.API_URL, requestBody);
            JSONObject mainJson = new JSONObject(json);
            JSONArray jsonArray = mainJson.getJSONArray(Callback.TAG_ROOT);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject objJson = jsonArray.getJSONObject(i);

                if (!objJson.has(Callback.TAG_SUCCESS)) {
                    String cid = objJson.getString(Callback.TAG_CID);
                    String category_name = objJson.getString(Callback.TAG_CAT_NAME);
                    String category_image = objJson.getString(Callback.TAG_CAT_IMAGE);

                    ItemCat objItem = new ItemCat(cid, category_name, category_image);
                    arrayList.add(objItem);
                    dbHelper.addedCatList(objItem);
                } else {
                    verifyStatus = objJson.getString(Callback.TAG_SUCCESS);
                    message = objJson.getString(Callback.TAG_MSG);
                }
            }
            return "1";
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        categoryListener.onEnd(s, verifyStatus, message, arrayList);
        super.onPostExecute(s);
    }
}