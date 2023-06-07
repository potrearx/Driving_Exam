package nemosofts.driving.exam.item;

public class ItemResult {

    private final String id;
    private final String date;
    private final String time;
    private final String exam_result;

    public ItemResult(String id, String date, String time, String exam_result) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.exam_result =exam_result;
    }

    public String getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getExam_result() {
        return exam_result;
    }
}
