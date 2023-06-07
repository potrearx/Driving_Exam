package nemosofts.driving.exam.asyncTask;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import nemosofts.driving.exam.callback.Callback;
import nemosofts.driving.exam.interfaces.LanguageListener;
import nemosofts.driving.exam.item.ItemLanguage;
import nemosofts.driving.exam.utils.ApplicationUtil;
import nemosofts.driving.exam.utils.DBHelper;
import okhttp3.RequestBody;

public class LoadLanguage extends AsyncTask<String, String, String> {

    private final RequestBody requestBody;
    private final LanguageListener listener;
    private final ArrayList<ItemLanguage> arrayList = new ArrayList<>();
    private final DBHelper dbHelper;
    private String verifyStatus = "0", message = "";

    public LoadLanguage(Context context, LanguageListener listener, RequestBody requestBody) {
        this.listener = listener;
        this.requestBody = requestBody;
        dbHelper = new DBHelper(context);
    }

    @Override
    protected void onPreExecute() {
        dbHelper.removeAllLanguage();
        listener.onStart();
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
                    String id = objJson.getString("lid");
                    String name = objJson.getString("language_name");

                    ItemLanguage objItem = new ItemLanguage(id, name);
                    arrayList.add(objItem);
                    dbHelper.addedLanguageList(objItem);
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
        listener.onEnd(s, verifyStatus, message, arrayList);
        super.onPostExecute(s);
    }
}