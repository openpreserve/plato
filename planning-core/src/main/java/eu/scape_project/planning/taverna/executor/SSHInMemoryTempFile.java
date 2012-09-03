package eu.scape_project.planning.taverna.executor;

public class SSHInMemoryTempFile implements SSHTempFile {

    private String name;

    private byte[] data;

    // --------------- getter/setter ---------------
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

}
