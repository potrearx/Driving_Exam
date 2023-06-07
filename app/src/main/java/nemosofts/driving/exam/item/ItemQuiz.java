package nemosofts.driving.exam.item;

public class ItemQuiz {

    private final String id;
    private final String answer;
    private final String answerA;
    private final String answerB;
    private final String answerC;
    private final String answerD;
    private final String correctAnswer;
    private final String image;
    private String myAnswer;
    private int isAnswerDraw;
    private Boolean isEnabled;

    public ItemQuiz(String id, String answer, String answerA, String answerB, String answerC, String answerD, String correctAnswer, String image) {
        this.id = id;
        this.answer = answer;
        this.answerA = answerA;
        this.answerB = answerB;
        this.answerC = answerC;
        this.answerD = answerD;
        this.correctAnswer = correctAnswer;
        this.image = image;
        this.myAnswer = "";
        this.isAnswerDraw = 1;
        this.isEnabled = false;
    }

    public String getId() {
        return id;
    }

    public String getAnswer() {
        return answer;
    }

    public String getAnswerA() {
        return answerA;
    }

    public String getAnswerB() {
        return answerB;
    }

    public String getAnswerC() {
        return answerC;
    }

    public String getAnswerD() {
        return answerD;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public String getImage() {
        return image;
    }

    public String isMyAnswer() {
        return myAnswer;
    }

    public void setMyAnswer(String myAnswer) {
        this.myAnswer = myAnswer;
    }

    public int isAnswerDraw() {
        return isAnswerDraw;
    }

    public void setAnswerDraw(int answerDraw) {
        this.isAnswerDraw = answerDraw;
    }

    public Boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(Boolean enabled) {
        isEnabled = enabled;
    }
}
