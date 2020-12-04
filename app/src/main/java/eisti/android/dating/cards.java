package eisti.android.dating;

public class cards {
    private String userId;
    private String name;
    private String profileImageUrl;
    public cards(String userId, String name,String profileImageUrl){
        this.userId=userId;
        this.name=name;
        this.profileImageUrl=profileImageUrl;
    }

    public String getUserId(){
        return userId;
    }
    public void setUserId(String UserId){
        this.userId=userId;
    }
    public String getName(){
        return name;
    }
    public void setName(){
        this.name=name;
    }
    public String getProfileImageUrl(){
        return profileImageUrl;
    }
    public void setProfileImageUrl(){
        this.profileImageUrl=profileImageUrl;
    }
}
