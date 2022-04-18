package domain.Member;

import java.util.Objects;

public class Member {

    private String id;
    private String password;
    private String name;
    private String mobileNumber;

    public Member(String id, String password, String name, String mobileNumber) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.mobileNumber = mobileNumber;
    }

        public static Member joinMember(String id, String password, String name, String mobileNumber) {
        return new Member(id, password, name, mobileNumber);
    }

        public String getMobileNumber() {
        return mobileNumber;
    }

        public String getName() {
        return name;
    }

        public String getPassword() {
        return password;
    }

        public String getId() {
        return id;
    }

        public boolean duplicationCheck(String id) {
        if (this.id.equals(id)) return false;
        return true;
    }

        @Override
        public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return id.equals(member.id);
    }

        @Override
        public int hashCode() {
        return Objects.hash(id);
    }

}
