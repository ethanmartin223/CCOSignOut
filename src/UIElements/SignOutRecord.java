package UIElements;

public class SignOutRecord {
    private String timeSignedOut;
    private String timeSignedIn;
    private String name;
    private String location;
    private String phone;


    public SignOutRecord(String name, String location, String phone, String signOutTime, String signInTime) {
        this.name = name;
        this.location = location;
        this.phone = phone;
        this.timeSignedOut = signOutTime;
        this.timeSignedIn = signInTime;
    }

    public String getTimeSignedOut() { return timeSignedOut; }
    public void setTimeSignedOut(String timeSignedOut) { this.timeSignedOut = timeSignedOut; }

    public String getTimeSignedIn() { return timeSignedIn; }
    public void setTimeSignedIn(String timeSignedIn) { this.timeSignedIn = timeSignedIn; }


    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getPhone() {
        return phone;
    }

    @Override
    public String toString() {
        return name + " (" + location + ")";
    }
}
