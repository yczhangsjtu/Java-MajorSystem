# Java-MajorSystem
A java implementation of an RPG game used for memorizing the major memory system.

--------------------------------------------------------------------------------
Very easy game, involving requirement of memory.
There are characters each with a code consisting of numbers, corresponding to a
word in the Major System.
You(the hero) are one of the characters.
Control the hero by the four arrow keys.
There is no end-of-game, you just keep walking here and there in the map,
gaining scores by solving quizes.
A quiz is a question whose answer consists of only numbers.
You have to gather a group of characters whose codes can cover the answer of
the quesiton.

Press space to talk with the units in front of you if it can be talked to.

--------------------------------------------------------------------------------
Characters: Talking with a character, he will tell you his code and ask you his
name (corresponding word in the Major System).
If you correctly answer the question, he will be added to your team and follows
you.
If he is already in your team, answering the question correctly will make him
drop your team.

Money: If you forget the answer (or simply never known it), spend money to know
the answer.

Quiz: Talking with a quiz (whose icon is a book), if you answers the question
correctly, and your team cover the answer (discussed later), you eliminate the
quiz and get scores.

Transport: Transport you to another point in the map.

Oracle: You can ask the name of any character or find any character.

--------------------------------------------------------------------------------

A team covering a sequence of numbers:

Divide the sequence by length 3, like

    xxxxxxx

	xxx|xxx|x

If each part is contained in the team, then the sequence is covered.
