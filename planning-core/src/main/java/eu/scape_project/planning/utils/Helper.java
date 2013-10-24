/**
 * 
 */
package eu.scape_project.planning.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Petar Petrov - <me@petarpetrov.org>
 * 
 */
public final class Helper {

    public static boolean isLocalIdentifier(String id) {
        return id.startsWith("file:/");        
    }
    
    public static boolean isRODAidentifier(String id) {
        String re1 = "((?:[a-z][a-z]+))"; // Word 1
        String re2 = "(:)"; // Any Single Character 1
        String re3 = "(\\/)"; // Any Single Character 2
        String re4 = "(\\/)"; // Any Single Character 3
        String re5 = "((?:[a-z][a-z\\.\\d\\-]+)\\.(?:[a-z][a-z\\-]+))(?![\\w\\.])"; // FQDN
                                                                                    // 1
        String re6 = "(\\/)"; // Any Single Character 4
        String re7 = "((?:[a-z][a-z]+))"; // Word 2
        String re8 = "(-)"; // Any Single Character 5
        String re9 = "((?:[a-z][a-z]+))"; // Word 3
        String re10 = "(\\/)"; // Any Single Character 6
        String re11 = "((?:[a-z][a-z]+))"; // Word 4
        String re12 = "(\\/)"; // Any Single Character 7
        String re13 = "((?:[a-z][a-z0-9_]*))"; // Variable Name 1
        String re14 = "(:)"; // Any Single Character 8
        String re15 = "(\\d+)"; // Integer Number 1
        String re16 = "(\\/)"; // Any Single Character 9
        String re17 = "((?:[a-z][a-z0-9_]*))"; // Variable Name 2

        Pattern p = Pattern.compile(re1 + re2 + re3 + re4 + re5 + re6 + re7 + re8 + re9 + re10 + re11 + re12 + re13
            + re14 + re15 + re16 + re17, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher m = p.matcher(id);
        return m.find();

    }

    private Helper() {

    }
}
