package UIElements;

public class SignOutRecord {
    private String name;
    private String location;
    private String phone;

    public SignOutRecord(String name, String location, String phone) {
        this.name = name;
        this.location = location;
        this.phone = phone;
    }

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
