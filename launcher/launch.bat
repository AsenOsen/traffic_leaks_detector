@ECHO OFF

SET PTH=S:/daemon/

java -da -Djava.library.path="%PTH%libs/" -Dfile.encoding=UTF7 -jar daemon.jar --mode=paranoid --gui="%PTH%GUI/gui.exe" --locale=RU  --max-traffic-during-10-sec=100000 --min-leak-size=32000