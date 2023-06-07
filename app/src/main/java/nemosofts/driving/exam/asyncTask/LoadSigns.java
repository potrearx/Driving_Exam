package nemosofts.driving.exam.asyncTask;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import nemosofts.driving.exam.callback.Callback;
import nemosofts.driving.exam.interfaces.SignsListener;
import nemosofts.driving.exam.item.ItemSigns;
import nemosofts.driving.exam.utils.ApplicationUtil;
import okhttp3.RequestBody;

public class LoadSigns extends AsyncTask<String, String, String> {

    private final RequestBody requestBody;
    private final SignsListener categoryListener;
    private final ArrayList<ItemSigns> arrayList = new ArrayList<>();
    private String verifyStatus = "0", message = "";

    public LoadSigns(SignsListener categoryListener, RequestBody requestBody) {
        this.categoryListener = categoryListener;
        this.requestBody = requestBody;
    }

    @Override
    protected void onPreExecute() {
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

                    String id = objJson.getString("sid");
                    String name = objJson.getString("signs_name");
                    String image = objJson.getString("signs_image");
                    String image_thumb = objJson.getString("signs_image_thumb");

                    ItemSigns objItem = new ItemSigns(id, name, image, image_thumb);
                    arrayList.add(objItem);
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