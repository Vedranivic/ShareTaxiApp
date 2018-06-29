package hr.ferit.vedran.sharetaxi.model;


public class Chat {
    private String id;
    private String user1id;
    private String user2id;

    public Chat(String id, String user1id, String user2id) {
        this.id = id;
        this.user1id = user1id;
        this.user2id = user2id;
    }

    public Chat(){

    }

    public String getId() {
        return id;
    }

    public String getUser1id() {
        return user1id;
    }

    public String getUser2id() {
        return user2id;
    }
}
