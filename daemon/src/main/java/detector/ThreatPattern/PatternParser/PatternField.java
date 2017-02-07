package detector.ThreatPattern.PatternParser;

/*************************************
 * Describes the fields which
 ************************************/
public class PatternField
{

    /*
    *  code name of a pattern
    * */
    public static final String NAME             = "name";

    /*
    * priority of pattern in common priority list
    * */
    public static final String PRIORITY         = "priority";

    /*
    * process ID threat should match to
    * */
    public static final String PID              = "pid";

    /*
    * destination IP threat should match to
    * */
    public static final String DST_IP           = "dstip";

    /*
    * source port of some entity(process e.g)
    * threat should match to
    * */
    public static final String SRC_PORT         = "srcport";

    /*
    * name of a system process
    * threat should match to
    * */
    public static final String PROCESS_NAME     = "processname";

    /*
    * name of organization which owns
    * destination IP address
    * threat should match to
    * */
    public static final String ORG_NAME         = "orgname";

    /*
    * name of a host bonded to destination IP
    * through DNS threat should match to
    * */
    public static final String HOST_NAME        = "hostname";

    /*
    * patterns which bonded with some pattern
    * threat should match to (see next field...)
    * */
    public static final String RELATED_PATTERNS = "related-patterns";

    /*
    * mode of relativity: ALL or ANY
    * in first case threat should match to ALL of bonded patterns
    * in second case threat should match to at least one of them
    * */
    public static final String RELATION_MODE    = "relation-mode";

    /*
    * message which will be displayed to user
    * in case threat is matching to some pattern
    * */
    public static final String MESSAGE           = "msg";

    /*
    * short message which describes the exciter
    * entity which caused potential threat
    * */
    public static final String EXCITER          = "msg_exciter";

    /*
    * just a comment of pattern which contains
    * some tech info or etc.
    * it does not locale in app memory
    * */
    public static final String COMMENT          = "comment";

}
