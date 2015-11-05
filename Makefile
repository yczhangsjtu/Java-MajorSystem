spath=src/com/
bpath=bin/com/
list = path/Canvas.sfx\
	   path/AI.sfx\
	   path/Memo.sfx\
	   path/QuizLibrary.sfx\
	   path/Character.sfx\
	   path/Team.sfx\
	   path/MapContainer.sfx\
	   path/Unit.sfx\
	   path/Jewel.sfx\
	   path/Money.sfx\
	   path/Quiz.sfx\
	   path/Transport.sfx\
	   path/Oracle.sfx\
	   path/UnitContainer.sfx\
	   path/CharacterContainer.sfx\
	   path/JewelContainer.sfx\
	   path/QuizContainer.sfx\
	   path/TransportContainer.sfx\
	   path/OracleContainer.sfx\
	   path/Conversation.sfx

classlist = $(subst path/,$(bpath),$(subst sfx,class,$(list)))
javalist = $(subst path/,$(spath),$(subst sfx,java,$(list)))

$(bpath)Main.class: $(spath)Main.java $(classlist)
	javac -sourcepath src -d bin $<

$(bpath)Canvas.class: $(spath)Canvas.java
	javac -sourcepath src -d bin $<

$(bpath)AI.class: $(spath)AI.java
	javac -sourcepath src -d bin $<

$(bpath)Memo.class: $(spath)Memo.java
	javac -sourcepath src -d bin $<

$(bpath)QuizLibrary.class: $(spath)QuizLibrary.java
	javac -sourcepath src -d bin $<

$(bpath)Character.class: $(spath)Character.java
	javac -sourcepath src -d bin $<

$(bpath)Team.class: $(spath)Team.java
	javac -sourcepath src -d bin $<

$(bpath)MapContainer.class: $(spath)MapContainer.java
	javac -sourcepath src -d bin $<

$(bpath)Unit.class: $(spath)Unit.java
	javac -sourcepath src -d bin $<

$(bpath)Jewel.class: $(spath)Jewel.java
	javac -sourcepath src -d bin $<

$(bpath)Money.class: $(spath)Money.java
	javac -sourcepath src -d bin $<

$(bpath)Quiz.class: $(spath)Quiz.java
	javac -sourcepath src -d bin $<

$(bpath)Transport.class: $(spath)Transport.java
	javac -sourcepath src -d bin $<

$(bpath)Oracle.class: $(spath)Oracle.java
	javac -sourcepath src -d bin $<

$(bpath)UnitContainer.class: $(spath)UnitContainer.java
	javac -sourcepath src -d bin $<

$(bpath)CharacterContainer.class: $(spath)CharacterContainer.java
	javac -sourcepath src -d bin $<

$(bpath)JewelContainer.class: $(spath)JewelContainer.java
	javac -sourcepath src -d bin $<

$(bpath)QuizContainer.class: $(spath)QuizContainer.java
	javac -sourcepath src -d bin $<

$(bpath)TransportContainer.class: $(spath)TransportContainer.java
	javac -sourcepath src -d bin $<

$(bpath)OracleContainer.class: $(spath)OracleContainer.java
	javac -sourcepath src -d bin $<

$(bpath)Conversation.class: $(spath)Conversation.java
	javac -sourcepath src -d bin $<

editor: $(spath)editor/MapEditor.java
	javac -sourcepath src -d bin $<
