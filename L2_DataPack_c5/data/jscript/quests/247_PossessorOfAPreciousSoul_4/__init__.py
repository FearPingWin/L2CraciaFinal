# Made by disKret
import sys
from net.sf.l2j.gameserver.model.quest import State
from net.sf.l2j.gameserver.model.quest import QuestState
from net.sf.l2j.gameserver.model.quest.jython import QuestJython as JQuest

#NPC
CARADINE = 31740
LADY_OF_LAKE = 31745

#QUEST ITEM
CARADINE_LETTER_LAST = 7679
NOBLESS_TIARA = 7694

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
   htmltext = event
   cond = st.getInt("cond") 
   if event == "31740-3.htm" :
     if cond == 0 :
       st.set("cond","1")
       st.setState(STARTED)
       st.playSound("ItemSound.quest_accept")
   if event == "31740-4.htm" :
     if cond == 1 :
       return htmltext
   if event == "31740-5.htm" :
     if cond == 1 :
       st.set("cond","2")
       st.takeItems(CARADINE_LETTER_LAST,1)
       st.player.teleToLocation(143200,44000,-3040)
       return htmltext
   if event == "31740-5.htm" :
     if cond == 2 :
       return htmltext
   if event == "31745-2.htm" :
     if cond == 2 :
       return htmltext
   if event == "31745-3.htm" :
     if cond == 2 :
       return htmltext
   if event == "31745-4.htm" :
     if cond == 2 :
       return htmltext
   if event == "31745-5.htm" :
     if cond == 2 :
       st.set("cond","0")
       st.getPlayer().setNoble(1)
       st.giveItems(NOBLESS_TIARA,1)
       st.playSound("ItemSound.quest_finish")
       st.setState(COMPLETED)
   return htmltext

 def onTalk (Self,npc,st):
   htmltext = "<html><head><body>I have nothing to say you</body></html>"
   cond = st.getInt("cond")
   npcId = npc.getNpcId()
   id = st.getState()
   if id == CREATED :
     st.set("cond","0")
   if st.getPlayer().isSubClassActive() :
     if npcId == CARADINE and st.getQuestItemsCount(CARADINE_LETTER_LAST) == 1 :
       if cond in [0,1] :
         if id == COMPLETED :
           htmltext = "<html><head><body>This quest have already been completed.</body></html>"
         elif st.getPlayer().getLevel() < 75 : 
           htmltext = "31740-2.htm"
           st.exitQuest(1)
         elif st.getPlayer().getLevel() >= 75 :
           htmltext = "31740-1.htm"
     if npcId == CARADINE and cond == 2 :
         htmltext = "31740-6.htm"
     if npcId == LADY_OF_LAKE and cond == 2 :
       htmltext = "31745-6.htm"
     if npcId == LADY_OF_LAKE and cond == 2 :
       htmltext = "31745-1.htm"
   return htmltext

QUEST       = Quest(247,"247_PossessorOfAPreciousSoul_4","Possessor Of A Precious Soul - 4")
CREATED     = State('Start', QUEST)
STARTED     = State('Started', QUEST,True)
COMPLETED   = State('Completed', QUEST)

QUEST.setInitialState(CREATED)
QUEST.addStartNpc(CARADINE)
CREATED.addTalkId(CARADINE)
STARTED.addTalkId(CARADINE)
STARTED.addTalkId(LADY_OF_LAKE)

print "importing quests: 247: Possessor Of A Precious Soul - 4"
