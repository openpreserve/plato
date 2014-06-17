package eu.scape_project.planning.services.pa.taverna;

import eu.scape_project.planning.services.IServiceInfo;

/**
 * A Taverna preservation action service information.
 */
public class MyExperimentActionInfo implements IServiceInfo {
    private static final String SERVICE_IDENTIFIER = "myExperiment";

    private String shortname;
    private String descriptor;
    private String info;
    private String url;
    private String contentType;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((contentType == null) ? 0 : contentType.hashCode());
        result = prime * result + ((descriptor == null) ? 0 : descriptor.hashCode());
        result = prime * result + ((info == null) ? 0 : info.hashCode());
        result = prime * result + ((shortname == null) ? 0 : shortname.hashCode());
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        MyExperimentActionInfo other = (MyExperimentActionInfo) obj;
        if (contentType == null) {
            if (other.contentType != null) {
                return false;
            }
        } else if (!contentType.equals(other.contentType)) {
            return false;
        }
        if (descriptor == null) {
            if (other.descriptor != null) {
                return false;
            }
        } else if (!descriptor.equals(other.descriptor)) {
            return false;
        }
        if (info == null) {
            if (other.info != null) {
                return false;
            }
        } else if (!info.equals(other.info)) {
            return false;
        }
        if (shortname == null) {
            if (other.shortname != null) {
                return false;
            }
        } else if (!shortname.equals(other.shortname)) {
            return false;
        }
        if (url == null) {
            if (other.url != null) {
                return false;
            }
        } else if (!url.equals(other.url)) {
            return false;
        }
        return true;
    }

    // --------- getter/setter ----------

    @Override
    public String getServiceIdentifier() {
        return SERVICE_IDENTIFIER;
    }

    @Override
    public String getShortname() {
        return shortname;
    }

    @Override
    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    @Override
    public String getDescriptor() {
        return descriptor;
    }

    @Override
    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }

    @Override
    public String getInfo() {
        return info;
    }

    @Override
    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

}
