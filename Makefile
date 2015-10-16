spath=src/com/
bpath=bin/com/
list = path/Canvas.sfx\
	   path/Memo.sfx\
	   path/Character.sfx\
	   path/MapContainer.sfx\
	   path/Unit.sfx\
	   path/UnitContainer.sfx\
	   path/CharacterContainer.sfx

classlist = $(subst path/,$(bpath),$(subst sfx,class,$(list)))
javalist = $(subst path/,$(spath),$(subst sfx,java,$(list)))

$(bpath)Main.class: $(spath)Main.java $(classlist)
	javac -sourcepath src -d bin $<

$(bpath)Canvas.class: $(spath)Canvas.java
	javac -sourcepath src -d bin $<

$(bpath)Memo.class: $(spath)Memo.java
	javac -sourcepath src -d bin $<

$(bpath)Character.class: $(spath)Character.java
	javac -sourcepath src -d bin $<

$(bpath)MapContainer.class: $(spath)MapContainer.java
	javac -sourcepath src -d bin $<

$(bpath)Unit.class: $(spath)Unit.java
	javac -sourcepath src -d bin $<

$(bpath)UnitContainer.class: $(spath)UnitContainer.java
	javac -sourcepath src -d bin $<

$(bpath)CharacterContainer.class: $(spath)CharacterContainer.java
	javac -sourcepath src -d bin $<
