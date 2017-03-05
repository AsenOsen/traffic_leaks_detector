# README #

This is a tool which helps you to detect net leaks from local computer to foreign networks (like Internet and etc.). Tool can work in 2 modes: CLI ang GUI.
You can run it in CLI mode to be able watch leaks right in your terminal, or you can use GUI for your platform to watch leaks interactivelly.

### Capabilities ###

 + Leaks patterns description engine, which helps you to describe all known threats in JSON format and read them in logs further.(More below)

 + Socket interaction interface. You can write your own app which will bind to socket and get leaks messages from it.

### Detection algorithms ###

...

### File hierarchy ###

 + daemon/ contains the sources of daemon detector
 + gui/ contains the platform-dependent GUI`s implementation
 + launcher/ contains the example script of launching the daemon

### How do I get set up? ###

* Use pom.xml to build daemon.jar with Maven
* Use libs/ as a local Maven repository

### Which libraries does it uses? ###

 1. It uses libs from Maven repo:
    + org.json
    + com.fasterxml.jackson
    + commons-cli

 2. LibPcaP implementation for Java(located in local maven-repository - see pom.xml) 

### Startup arguments ###

 1. For JRE:
    + **java.library.path**(optional) in case if you have placed Pcap library in non-standard place
    + **file.encoding=UTF-8** - standard encoding
    
 2. For tool:
    + **--mode**=(paranoid|adequate|chill)  
    _detection verbosity mode(adequate by default)_
    + **--gui**="path-to-gui-handler"  
    *path to GUI handler; __daemon will run GUI each time it detects something__*
    + **--locale**=(RU|EN)  
    _language for generation alert messages(EN by default)_
    + **--max-traffic-during-10-sec**=N 
    _max allowed traffic per 10 seconds in bytes(100000 bytes by default)_ 
    + **--min-leak-size**=N  
     _minimal traffic size in bytes which would mean the leakage(32000 by default)_
        
__underline__
### Threat patterns description ###

This tool uses own threat description config in JSON format.  
Take a look on some examples first:

    {
      "name": "Pattern.Undefined",
      "pattern":{
        "differ-by":"processname"
      },
      "comment": "This is default pattern. Using in ThreatPattern.java. Normally, this situation is impossible.",
      "priority": 0
    },

    {
      "name": "Pattern.Port",
      "pattern": {
        "srcport": "^[0-9]{1,5}$",
        "differ-by":"srcport"
      },
      "priority": 10
    },
    
    {
      "name": "Pattern.StdApp.Browser.Metrica.Mailru",
      "pattern": {
        "hostname": ".*((smailru\\.net)|(mail\\.ru))$",
        "orgname": ".*Mail\\.Ru.*"
      },
      "related-patterns": "Pattern\\.StdApp\\.(Browser)",
      "relation-mode": "all",
      "priority": 70
    },
    
    ...
    
As you can see, all potential threats can be described independently as detailed
as you want to via regular expressions in any fields

The config you seen above is part of **patterns.json** file which contains 
default leak patterns.

Common pattern format:

    {
      "name": STRING,
      "pattern": {
        "pid": REGEXP,
        "dstip": REGEXP,
        "srcport": REGEXP,
        "processname": REGEXP,
        "orgname": REGEXP,
        "hostname": REGEXP,
        "differby": "field1,field2,field3, ..."
      },
      "msg": STRING,
      "msg_exciter": STRING,
      "related-patterns": REGEXP,
      "relation-mode": ("all"|"any"),
      "priority": INTEGER
    },

**Fields meanings:**

 + _name_ - pattern`s name  
 + _pid_ - id of a process which caused leak
 + _dstip_ - destination ip traffic was leaking to  
 + _srcport_ - source port from which traffic was leaking  
 + _processname_ - name of a program which caused the leak  
 + _orgname_ - organization which owns the ip traffic was leaking to  
 + _hostname_ - DNS resolution of destination ip  
 + _differby_ - set of fields which will differentiate the threats matching to the same pattern
 + _msg_ - message which will be displayed when some leak will match this pattern;
  inside message string you can use **field vars** like {name} or {dstip} or anything else 
  to add dynamic information
 + _msg_exciter_ - short message about exciter of a leakage; this message will be
 displayed in notification, so it should be short; you also can use **field vars** in here  
 + _related-patterns_ - patterns which also SHOULD match to the leakage
 + _relation-mode_ - all pattern(all) OR at least one pattern(any) should match
 + _priority_ - priority of concrete pattern amongst all patterns

### Filters (Harmless patterns) description ###

This tool also supports the filters also known as _harmless patterns_
to filter threats.

By default there is 3 types of filtering:
 + _paranoid_ - you will be notified about all the leaks, even harmless such as 
 Google analytics, Amazon AWS and even uploading files through browser.
 + _adequate_ - browsers activity will be ignored and 
 standard apps(like Skype, Torrent), too
 + _chill_ - all known patterns will be ignored, you will get
 notifications about totally undefined leak vectors.

Filter patterns have the same fields as threat patterns except:

 + _msg_
 + _msg_exciter_
 + _priority_

### Socket interface(for GUI) ###

Daemon provides the socket interface for another applications
for communication with him. By default this interface is used
by GUI - it gets alerts from daemon and displays them to user.

You can write your own GUI for daemon, which would handle
messages from daemon in specific way.

__IMPORTANT TO KNOW__: If you specify *--gui* parameter on startup, then
 daemon will run specified GUI each time it detects a leak.
 
Socket interface works next way:

 * Daemon opens a port from interval 5000-5010
 * When someone connects to daemon, daemon sends initial string - ":::daemon_protocol_start:::"
 * After this daemon ready to accept commands from client:
    + *quit* - daemon terminates connection and 
    writes to socket ":::daemon_protocol_finish:::"
    + *ping* - when daemon got successfully pinged it 
    writes to socket ":::daemon_protocol_pinged:::"
    + *get_alert* - daemon writes to socket first 
    alert message from alert queue or ":::daemon_protocol_no_msg:::" if queue is empty
    + *ignore_tmp* - accepts a threat from client 
    and adds it to temporary ignore-list
    + *ignore_permanent* - accepts a threat from client 
    and adds it to permanent ignore-list; 
    list stores in a file by path, specified by "user.dir" java`s property,
    name of a file - **ignores.user.json**
    + *grab_all_user_ignores* - **not implemented yet**
    + *delete_ignore* - **not implemented yet**
    
If daemons accepts the unknown command, it writes to socket 
":::daemon_protocol_unknown_command:::" 