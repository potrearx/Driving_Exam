package nemosofts.driving.exam.asyncTask;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import nemosofts.driving.exam.callback.Callback;
import nemosofts.driving.exam.interfaces.QuizListener;
import nemosofts.driving.exam.item.ItemQuiz;
import nemosofts.driving.exam.utils.ApplicationUtil;
import okhttp3.RequestBody;

public class LoadQuiz extends AsyncTask<String, String, String> {

    private final RequestBody requestBody;
    private final QuizListener quizListener;
    private final ArrayList<ItemQuiz> arrayList_img = new ArrayList<>();
    private final ArrayList<ItemQuiz> arrayList_no_img = new ArrayList<>();

    private String message = "", successAPI = "1";

    public LoadQuiz(QuizListener quizListener, RequestBody requestBody) {
        this.quizListener = quizListener;
        this.requestBody = requestBody;
    }

    @Override
    protected void onPreExecute() {
        quizListener.onStart();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            String json = ApplicationUtil.responsePost(Callback.API_URL, requestBody);
            JSONObject mainJson = new JSONObject(json);
            try {
                JSONObject jsonObject = mainJson.getJSONObject(Callback.TAG_ROOT);

                JSONArray jsonArrayArtist = jsonObject.getJSONArray("quiz_img");
                for (int i = 0; i < jsonArrayArtist.length(); i++) {
                    JSONObject objJson = jsonArrayArtist.getJSONObject(i);

                    String id = objJson.getString("id");
                    String answer = objJson.getString("answer");
                    String answer_a = objJson.getString("answer_a");
                    String answer_b = objJson.getString("answer_b");
                    String answer_c = objJson.getString("answer_c");
                    String answer_d = objJson.getString("answer_d");
                    String correctAnswer = objJson.getString("correctAnswer");
                    String image = objJson.getString("image");

                    ItemQuiz objItem = new ItemQuiz(id, answer, answer_a,answer_b, answer_c,answer_d,correctAnswer, image);
                    arrayList_img.add(objItem);
                }

                JSONArray jsonArrayAlbums = jsonObject.getJSONArray("quiz_no_img");
                for (int i = 0; i < jsonArrayAlbums.length(); i++) {
                    JSONObject objJson = jsonArrayAlbums.getJSONObject(i);

                    String id = objJson.getString("id");
                    String answer = objJson.getString("answer");
                    String answer_a = objJson.getString("answer_a");
                    String answer_b = objJson.getString("answer_b");
                    String answer_c = objJson.getString("answer_c");
                    String answer_d = objJson.getString("answer_d");
                    String correctAnswer = objJson.getString("correctAnswer");
                    String image = objJson.getString("image");

                    ItemQuiz objItem = new ItemQuiz(id, answer, answer_a,answer_b, answer_c,answer_d,correctAnswer, image);
                    arrayList_no_img.add(objItem);
                }
            } catch (Exception e) {
                JSONArray jsonArray = mainJson.getJSONArray(Callback.TAG_ROOT);
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                successAPI = jsonObject.getString(Callback.TAG_SUCCESS);
                message = jsonObject.getString(Callback.TAG_MSG);
                e.printStackTrace();
            }
            return successAPI;
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        quizListener.onEnd(s, message, arrayList_img, arrayList_no_img);
        super.onPostExecute(s);
    }
}