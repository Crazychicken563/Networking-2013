package webshtuff;

public class TextChat {

    private StringBuilder message = new StringBuilder();
    private StopWatch lifeSpan = new StopWatch();
    private boolean specialCase = false;
    private int teamChat = -1;

    public void append(String a) {
        message.append(a);
    }

    public void append(char a) {
        message.append(a);
    }

    public void teamChat(int t) {
        teamChat = t;
    }

    public int isTeamChat() {
        return teamChat;
    }

    public String getMessage() {
        return message.toString();
    }

    public void delete() {
        if (message.length() != 0) {
            message.deleteCharAt(message.length() - 1);
        }
    }

    public void display() {
        lifeSpan.start();
    }

    public boolean isDead() {
        return lifeSpan.getTime() > 5;
    }

    public void wow() {
        specialCase = true;
    }

    public boolean how() {
        return specialCase;
    }
}
