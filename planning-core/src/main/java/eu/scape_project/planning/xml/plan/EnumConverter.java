/**
 * 
 */
package eu.scape_project.planning.xml.plan;

import org.apache.commons.beanutils.Converter;

/**
 * @author Michael Kraxner
 *
 */
public class EnumConverter<T extends Enum<T>> implements Converter {

    private Class<T> type;
    
    public EnumConverter(final Class<T> type) {
        this.type = type;
    }

    /* (non-Javadoc)
     * @see org.apache.commons.beanutils.Converter#convert(java.lang.Class, java.lang.Object)
     */
    @Override
    public Object convert(Class clazz, Object value) {
        if (value == null) {
            return null;
        } else {
            return Enum.valueOf(type, value.toString());
        }
    }

}
