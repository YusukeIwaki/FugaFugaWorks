package jp.co.crowdworks.fugafugaworks;

public class Message {
    public String author;
    public String content;

    public Message(){} //これがないとcom.google.firebase.database.DatabaseException: Class jp.co.crowdworks.fugafugaworks.Message is missing a constructor with no arguments みたいなエラーで落ちる

    public Message(String author, String content) {
        this.author = author;
        this.content = content;
    }
}
