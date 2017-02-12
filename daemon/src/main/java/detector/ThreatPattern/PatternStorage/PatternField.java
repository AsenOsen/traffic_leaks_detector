package detector.ThreatPattern.PatternStorage;

/*************************************
 * Describes the fields which
 ************************************/
public class PatternField
{

    public static class TrafficField
    {
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
        * fields which using to differ threats
        * from each other in context of single pattern
        * format: "field1,field2,...,fieldN"
        * */
        public static final String DIFFER_BY        = "differ-by";
    }


    /*
    * field containing traffic description object
    * */
    public static final String TRAFFIC_PATTERN  = "pattern";

    /*
    *  code name of a pattern
    * */
    public static final String NAME             = "name";

    /*
    * priority of pattern in common priority list
    * */
    public static final String PRIORITY         = "priority";

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
