package eu.scape_project.planning.taverna;

import java.net.URI;
import java.util.Set;

public class TavernaPort {

    private Set<URI> uris;
    private String name;
    private int depth;

    public Set<URI> getUris() {
        return uris;
    }

    public void setUris(Set<URI> uris) {
        this.uris = uris;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

}
