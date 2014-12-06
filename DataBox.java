package webshtuff;

public class DataBox implements Comparable {

    private String data;
    private int kills = -1;
    private int deaths = -1;
    private int flagCaps = -1;

    public DataBox(String text) {
        data = text;
    }

    public void setKills(int k) {
        kills = k;
    }

    public void setDeaths(int d) {
        deaths = d;
    }

    public void setFlagCaps(int f) {
        flagCaps = f;
    }

    public int getKills() {
        return kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public int getFlagCaps() {
        return flagCaps;
    }

    @Override
    public String toString() {
        return data;
    }

    @Override
    public int compareTo(Object o) {
        DataBox comp = (DataBox) o;
        if (flagCaps == -1) {
            if (comp.getKills() > getKills()) {
                return 1;
            } else {
                return -1;
            }
        } else {
            if (comp.getFlagCaps() > getFlagCaps()) {
                return 1;
            } else {
                return -1;
            }
        }
    }
}
