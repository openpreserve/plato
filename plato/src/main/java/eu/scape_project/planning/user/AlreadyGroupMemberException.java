package eu.scape_project.planning.user;

public class AlreadyGroupMemberException extends Exception {

    private static final long serialVersionUID = -9066930612412877788L;

    public AlreadyGroupMemberException() {
    }

    public AlreadyGroupMemberException(String arg0) {
        super(arg0);
    }

    public AlreadyGroupMemberException(Throwable arg0) {
        super(arg0);
    }

    public AlreadyGroupMemberException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }
}
